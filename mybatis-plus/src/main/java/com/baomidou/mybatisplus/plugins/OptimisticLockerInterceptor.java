package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * MyBatis乐观锁插件
 * 
 * <pre>
 * 之前：update user set name = ?, password = ? where id = ?
 * 之后：update user set name = ?, password = ?, version = version+1 where id = ? and version = ?
 * 对象上的version字段上添加{@link Version}注解
 * sql可以不需要写version字段,只要对象version有值就会更新
 * 支持int Integer long Long Date Timestamp
 * 其他类型可以自定义实现,注入versionHandlers,多个以逗号分隔
 * </pre>
 * 
 * @author TaoYu
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public final class OptimisticLockerInterceptor implements Interceptor {

	/**
	 * 根据对象类型缓存version基本信息
	 */
	private static final Map<Class<?>, VersionCache> versionCache = new ConcurrentHashMap<Class<?>, VersionCache>();

	/**
	 * 根据version字段类型缓存的处理器
	 */
	private static final Map<Class<?>, VersionHandler> typeHandlers = new HashMap<Class<?>, VersionHandler>();

	static {
		registerHandler(new BaseTypeHnadler());
		registerHandler(new DateTypeHandler());
	}

	public Object intercept(Invocation invocation) throws Exception {
		// 先判断入参为null或者不是真正的UPDATE语句
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Object parameterObject = invocation.getArgs()[1];
		if (parameterObject == null || !ms.getSqlCommandType().equals(SqlCommandType.UPDATE)) {
			return invocation.proceed();
		}
		// 获得参数类型,去缓存中快速判断是否有version注解才继续执行
		Class<? extends Object> parameterClass = parameterObject.getClass();
		VersionCache versionPo = versionCache.get(parameterClass);
		if (versionPo != null) {
			if (versionPo.isVersionControl) {
				processChangeSql(ms, parameterObject, versionPo);
			}
		} else {
			String versionColumn = null;
			Field versionField = null;
			for (Field field : parameterClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Version.class)) {
					if (!typeHandlers.containsKey(field.getType())) {
						throw new TypeException("乐观锁不支持" + field.getType().getName() + "类型,请自定义实现");
					}
					versionField = field;
					TableName tableName = field.getAnnotation(TableName.class);
					if (tableName != null) {
						versionColumn = tableName.value();
					} else {
						versionColumn = field.getName();
					}
					break;
				}
			}
			if (versionField != null) {
				versionField.setAccessible(true);
				VersionCache cachePo = new VersionCache(true, versionColumn, versionField);
				versionCache.put(parameterClass, cachePo);
				processChangeSql(ms, parameterObject, cachePo);
			} else {
				versionCache.put(parameterClass, new VersionCache(false));
			}
		}
		return invocation.proceed();
	}

	private void processChangeSql(MappedStatement ms, Object parameterObject, VersionCache versionPo) throws Exception {
		Field versionField = versionPo.versionField;
		String versionColumn = versionPo.versionColumn;
		final Object versionValue = versionField.get(parameterObject);
		if (versionValue != null) {// 先判断传参是否携带version,没带跳过插件,不可能去数据库查吧
			Configuration configuration = ms.getConfiguration();
			BoundSql boundSql = ms.getBoundSql(parameterObject);
			String originalSql = boundSql.getSql();
			Update parse = (Update) CCJSqlParserUtil.parse(originalSql);
			List<Column> columns = parse.getColumns();
			List<String> columnNames = new ArrayList<String>();
			for (Column column : columns) {
				columnNames.add(column.getColumnName());
			}
			if (!columnNames.contains(versionColumn)) {// 如果sql没有version手动加一个
				columns.add(new Column(versionColumn));
				parse.setColumns(columns);
			}
			BinaryExpression expression = (BinaryExpression) parse.getWhere();
			if (expression != null && !expression.toString().contains(versionColumn)) {
				EqualsTo equalsTo = new EqualsTo();
				equalsTo.setLeftExpression(new Column(versionColumn));
				VersionHandler targetHandler = typeHandlers.get(versionField.getType());
				Expression rightExpression = targetHandler.getRightExpression(versionValue);
				Expression plusExpression = targetHandler.getPlusExpression(versionValue);
				equalsTo.setRightExpression(rightExpression);
				parse.setWhere(new AndExpression(equalsTo, expression));
				List<Expression> expressions = parse.getExpressions();
				expressions.add(plusExpression);
				parse.setExpressions(expressions);
			}
			MetaObject metaObject = SystemMetaObject.forObject(ms);
			metaObject.setValue("sqlSource", new StaticSqlSource(configuration, parse.toString(), boundSql.getParameterMappings()));
		}
	}

	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	public void setProperties(Properties properties) {
		String versionHandlers = properties.getProperty("versionHandlers");
		if (StringUtils.isNotEmpty(versionHandlers)) {
			String[] userHandlers = versionHandlers.split(",");
			for (String handlerClazz : userHandlers) {
				try {
					VersionHandler versionHandler = (VersionHandler) Class.forName(handlerClazz).newInstance();
					registerHandler(versionHandler);
				} catch (Exception e) {
					throw ExceptionFactory.wrapException("乐观锁插件自定义处理器注册失败", e);
				}

			}
		}
	}

	/**
	 * 注册处理器
	 */
	private static void registerHandler(VersionHandler versionHandler) {
		Class<?>[] handleType = versionHandler.handleType();
		if (ArrayUtils.isNotEmpty(handleType)) {
			for (Class<?> type : handleType) {
				typeHandlers.put(type, versionHandler);
			}
		}
	}

	private class VersionCache {

		private Boolean isVersionControl;

		private String versionColumn;

		private Field versionField;

		public VersionCache(Boolean isVersionControl) {
			this.isVersionControl = isVersionControl;
		}

		public VersionCache(Boolean isVersionControl, String versionColumn, Field versionField) {
			this.isVersionControl = isVersionControl;
			this.versionColumn = versionColumn;
			this.versionField = versionField;
		}
	}

	/**
	 * 基本类型处理器
	 * 
	 * @author TaoYu
	 */
	private static class BaseTypeHnadler implements VersionHandler {

		public Class<?>[] handleType() {
			return new Class<?>[] { Long.class, long.class, Integer.class, int.class, Short.class, short.class };
		}

		public Expression getRightExpression(Object param) {
			return new LongValue(param.toString());
		}

		public Expression getPlusExpression(Object param) {
			return new LongValue(param.toString() + 1);
		}

	}

	/**
	 * 时间类型处理器
	 * 
	 * @author TaoYu
	 */
	private static class DateTypeHandler implements VersionHandler {

		public Class<?>[] handleType() {
			return new Class<?>[] { Date.class, java.sql.Date.class, Timestamp.class };
		}

		/*
		 * 时间处理是大坑!!!!!http://www.yufengof.com/2015/08/17/mysql-datetime-type-millisecond-rounding/
		 */

		public Expression getRightExpression(Object param) {
			Date date = (Date) param;
			String millTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format((Date) param);
			String realTime;
			Integer mills = Integer.valueOf(millTime.substring(20, 21));
			if (mills >= 5) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.SECOND, 1);
				realTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
			} else {
				realTime = millTime.substring(0, 19);
			}
			return new StringValue(realTime);
		}

		public Expression getPlusExpression(Object param) {
			return new StringValue(new Timestamp(new Date().getTime()).toString());
		}

	}

}