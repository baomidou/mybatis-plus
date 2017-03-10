package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.builder.StaticSqlSource;
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

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * MyBatis乐观锁插件
 * 
 * <pre>
 * 只支持int和long类型,使用插件需要在需要在 启用的
 * 对象上的version字段上添加{@link Version}注解
 * 注意只要update的对象version有值就会执行执行操作不论底层是否有set version =?
 * </pre>
 * 
 * @author TaoYu
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class OptimisticLockerInterceptor implements Interceptor {

	private static final Map<Class<?>, VersionPo> versionCache = new ConcurrentHashMap<Class<?>, VersionPo>();

	public Object intercept(Invocation invocation) throws Exception {
		// 先判断入参为null或者不是真正的UPDATE语句
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		Object parameterObject = invocation.getArgs()[1];
		if (parameterObject == null || !ms.getSqlCommandType().equals(SqlCommandType.UPDATE)) {
			return invocation.proceed();
		}
		// 获得参数类型,去缓存中快速判断是否有version注解才继续执行
		Class<? extends Object> parameterClass = parameterObject.getClass();
		VersionPo versionPo = versionCache.get(parameterClass);
		if (versionPo != null) {
			if (versionPo.isVersionControl) {
				processChangeSql(ms, parameterObject, versionPo);
			}
		} else {
			String versionColumn = null;
			Field versionField = null;
			for (Field field : parameterClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Version.class)) {
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
				VersionPo cachePo = new VersionPo(true, versionColumn, versionField);
				versionCache.put(parameterClass, cachePo);
				processChangeSql(ms, parameterObject, cachePo);
			} else {
				versionCache.put(parameterClass, new VersionPo(false));
			}
		}
		return invocation.proceed();

	}

	private void processChangeSql(MappedStatement ms, Object parameterObject, VersionPo versionPo) throws Exception {
		Field versionField = versionPo.versionField;
		String versionColumn = versionPo.versionColumn;
		final Object paramVersionValue = versionField.get(parameterObject);
		if (paramVersionValue != null) {// 先判断传参是否携带version,没带跳过插件,不可能去数据库查吧
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
			Expression whereExpression = parse.getWhere();
			if (whereExpression != null && !whereExpression.toString().contains(versionColumn)) {
				EqualsTo equalsTo = new EqualsTo();
				equalsTo.setLeftExpression(new Column(versionColumn));
				LongValue longValue = new LongValue(paramVersionValue.toString());
				equalsTo.setRightExpression(longValue);

				parse.setWhere(new AndExpression(equalsTo, CCJSqlParserUtil.parseCondExpression("("+whereExpression+")")));

				List<Expression> expressions = parse.getExpressions();
				expressions.add(new LongValue(longValue.getValue() + 1));
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
	}

	private class VersionPo {

		private Boolean isVersionControl;

		private String versionColumn;

		private Field versionField;

		public VersionPo(Boolean isVersionControl) {
			this.isVersionControl = isVersionControl;
		}

		public VersionPo(Boolean isVersionControl, String versionColumn, Field versionField) {
			this.isVersionControl = isVersionControl;
			this.versionColumn = versionColumn;
			this.versionField = versionField;
		}
	}
}