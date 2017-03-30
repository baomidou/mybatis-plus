package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.UnknownTypeHandler;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * MyBatis乐观锁插件
 * <p>
 * 
 * <pre>
 * 之前：update user set name = ?, password = ? where id = ?
 * 之后：update user set name = ?, password = ?, version = version+1 where id = ? and version = ?
 * 对象上的version字段上添加{@link Version}注解
 * sql可以不需要写version字段,只要对象version有值就会更新
 * 支持short,Short,int Integer, long Long, Date Timestamp
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
	private static final Map<Class<?>, CachePo> versionCache = new ConcurrentHashMap<Class<?>, CachePo>();

	/**
	 * 根据version字段类型缓存的处理器
	 */
	private static final Map<Class<?>, VersionHandler<?>> typeHandlers = new HashMap<Class<?>, VersionHandler<?>>();

	static {
		ShortTypeHandler shortTypeHnadler = new ShortTypeHandler();
		typeHandlers.put(short.class, shortTypeHnadler);
		typeHandlers.put(Short.class, shortTypeHnadler);

		IntegerTypeHandler integerTypeHnadler = new IntegerTypeHandler();
		typeHandlers.put(int.class, integerTypeHnadler);
		typeHandlers.put(Integer.class, integerTypeHnadler);

		LongTypeHnadler longTypeHnadler = new LongTypeHnadler();
		typeHandlers.put(long.class, longTypeHnadler);
		typeHandlers.put(Long.class, longTypeHnadler);

		typeHandlers.put(Date.class, new DateTypeHandler());
		typeHandlers.put(Timestamp.class, new TimestampTypeHandler());
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
		Class<?> realClass = ms.getParameterMap().getType();
		Object realParameterObject = parameterObject;
		if (parameterObject instanceof ParamMap) {
			EntityWrapper<?> tt = (EntityWrapper<?>) ((ParamMap<?>) parameterObject).get("ew");
			realParameterObject = tt.getEntity();
		}
		CachePo versionPo = versionCache.get(realClass);
		if (versionPo != null) {
			if (versionPo.isVersionControl) {
				return processChangeSql(ms, parameterObject, realParameterObject, versionPo, invocation);
			}
		} else {
			String versionColumn = null;
			Field versionField = null;
			for (final Field field : realClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Version.class)) {
					if (!typeHandlers.containsKey(field.getType())) {
						throw new TypeException("乐观锁不支持" + field.getType().getName() + "类型,请自定义实现");
					}
					versionField = field;
					final TableName tableName = field.getAnnotation(TableName.class);
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
				CachePo cachePo = new CachePo(true, versionColumn, versionField);
				versionCache.put(parameterClass, cachePo);
				return processChangeSql(ms, parameterObject, realParameterObject, cachePo, invocation);
			} else {
				versionCache.put(parameterClass, new CachePo(false));
			}
		}
		return invocation.proceed();

	}

	private static final Expression JDBCPARAMETER = new JdbcParameter();
	private static final Expression RIGHTEXPRESSION = new Column("?");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object processChangeSql(MappedStatement ms, Object parameterObject, Object realParameterObject, CachePo cachePo, Invocation invocation) throws Exception {
		Field versionField = cachePo.versionField;
		String versionColumn = cachePo.versionColumn;
		final Object versionValue = versionField.get(realParameterObject);
		SqlSource originSqlSource;
		if (cachePo.getSqlSource() == null) {
			originSqlSource = ms.getSqlSource();
			cachePo.setSqlSource(originSqlSource);
		} else {
			originSqlSource = cachePo.getSqlSource();
		}
		Configuration configuration = ms.getConfiguration();
		MetaObject metaObject = configuration.newMetaObject(ms);
		if (versionValue != null) {// 先判断传参是否携带version,没带跳过插件
			BoundSql originBoundSql = originSqlSource.getBoundSql(parameterObject);
			// 处理
			Update jsqlSql = (Update) CCJSqlParserUtil.parse(originBoundSql.getSql());
			List<Column> columns = jsqlSql.getColumns();
			List<String> columnNames = new ArrayList<String>();
			for (Column column : columns) {
				columnNames.add(column.getColumnName());
			}
			List<Expression> expressions = jsqlSql.getExpressions();
			if (!columnNames.contains(versionColumn)) {
				columns.add(new Column(versionColumn));
				jsqlSql.setColumns(columns);
				expressions.add(JDBCPARAMETER);
				jsqlSql.setExpressions(expressions);
			}

			// 给字段赋新值
			VersionHandler targetHandler = typeHandlers.get(versionField.getType());
			targetHandler.plusVersion(realParameterObject, versionField, versionValue);
			// 设置sqlSource
			SqlSource sqlSource = ms.getSqlSource();
			if (!(sqlSource instanceof OptimisticLockerSqlSource)) {
				sqlSource = new OptimisticLockerSqlSource(configuration);
				metaObject.setValue("sqlSource", sqlSource);
			}
			OptimisticLockerSqlSource optimisticLockerSqlSource = (OptimisticLockerSqlSource) sqlSource;
			// 处理where条件,添加?
			BinaryExpression expression = (BinaryExpression) jsqlSql.getWhere();
			if (expression != null && !expression.toString().contains(versionColumn)) {
				EqualsTo equalsTo = new EqualsTo();
				equalsTo.setLeftExpression(new Column(versionColumn));
				equalsTo.setRightExpression(RIGHTEXPRESSION);
				jsqlSql.setWhere(new AndExpression(equalsTo, expression));
				List<ParameterMapping> parameterMappings = new LinkedList<ParameterMapping>(originBoundSql.getParameterMappings());
				parameterMappings.add(expressions.size(), createVersionMapping(configuration));
				optimisticLockerSqlSource.setParameterMappings(parameterMappings);
			} else {
				optimisticLockerSqlSource.setParameterMappings(originBoundSql.getParameterMappings());
			}
			// 设置参数
			Map<String, Object> additionalParameters = new HashMap<String, Object>();
			additionalParameters.put("originVersionValue", versionValue);
			additionalParameters.putAll((Map<String, Object>) configuration.newMetaObject(originBoundSql).getValue("additionalParameters"));
			optimisticLockerSqlSource.setSql(jsqlSql.toString());
			optimisticLockerSqlSource.setAdditionalParameters(additionalParameters);
		} else {
			metaObject.setValue("sqlSource", originSqlSource);
		}
		return invocation.proceed();
	}

	private ParameterMapping parameterMapping;

	private ParameterMapping createVersionMapping(Configuration configuration) {
		if (parameterMapping == null) {
			synchronized (OptimisticLockerInterceptor.class) {
				if (parameterMapping == null) {
					parameterMapping = new ParameterMapping.Builder(configuration, "originVersionValue", new UnknownTypeHandler(configuration.getTypeHandlerRegistry())).build();
				}
			}
		}
		return parameterMapping;
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
					VersionHandler<?> versionHandler = (VersionHandler<?>) Class.forName(handlerClazz).newInstance();
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
	private static void registerHandler(VersionHandler<?> versionHandler) {
		Type[] genericInterfaces = versionHandler.getClass().getGenericInterfaces();
		ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
		Class<?> type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		typeHandlers.put(type, versionHandler);
	}

	/**
	 * 缓存对象
	 */
	private class CachePo {

		private Boolean isVersionControl;

		private String versionColumn;

		private Field versionField;

		private SqlSource sqlSource;

		public CachePo(Boolean isVersionControl) {
			this.isVersionControl = isVersionControl;
		}

		public CachePo(Boolean isVersionControl, String versionColumn, Field versionField) {
			this.isVersionControl = isVersionControl;
			this.versionColumn = versionColumn;
			this.versionField = versionField;
		}

		public SqlSource getSqlSource() {
			return sqlSource;
		}

		public void setSqlSource(SqlSource sqlSource) {
			this.sqlSource = sqlSource;
		}

	}

	/**
	 * 乐观锁数据源,主要是为动态参数设计
	 */
	private class OptimisticLockerSqlSource implements SqlSource {

		private Configuration configuration;
		private String sql;
		private List<ParameterMapping> parameterMappings;
		private Map<String, Object> additionalParameters;

		public OptimisticLockerSqlSource(Configuration configuration) {
			this.configuration = configuration;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			BoundSql boundSql = new BoundSql(configuration, sql, parameterMappings, parameterObject);
			if (additionalParameters != null && additionalParameters.size() > 0) {
				for (Entry<String, Object> item : additionalParameters.entrySet()) {
					boundSql.setAdditionalParameter(item.getKey(), item.getValue());
				}
			}
			additionalParameters.clear();
			return boundSql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public void setParameterMappings(List<ParameterMapping> parameterMappings) {
			this.parameterMappings = parameterMappings;
		}

		public void setAdditionalParameters(Map<String, Object> additionalParameters) {
			this.additionalParameters = additionalParameters;
		}

	}
	// *****************************基本类型处理器*****************************

	private static class ShortTypeHandler implements VersionHandler<Short> {

		public void plusVersion(Object paramObj, Field field, Short versionValue) throws Exception {
			field.set(paramObj, (short) (versionValue + 1));
		}
	}

	private static class IntegerTypeHandler implements VersionHandler<Integer> {

		public void plusVersion(Object paramObj, Field field, Integer versionValue) throws Exception {
			field.set(paramObj, versionValue + 1);
		}
	}

	private static class LongTypeHnadler implements VersionHandler<Long> {

		public void plusVersion(Object paramObj, Field field, Long versionValue) throws Exception {
			field.set(paramObj, versionValue + 1);
		}
	}

	// ***************************** 时间类型处理器*****************************
	private static class DateTypeHandler implements VersionHandler<Date> {

		public void plusVersion(Object paramObj, Field field, Date versionValue) throws Exception {
			field.set(paramObj, new Date());
		}
	}

	private static class TimestampTypeHandler implements VersionHandler<Timestamp> {

		public void plusVersion(Object paramObj, Field field, Timestamp versionValue) throws Exception {
			field.set(paramObj, new Timestamp(new Date().getTime()));
		}
	}
}