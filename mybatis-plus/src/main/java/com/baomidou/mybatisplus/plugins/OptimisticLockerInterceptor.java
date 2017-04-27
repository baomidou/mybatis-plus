/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
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
import org.apache.ibatis.type.UnknownTypeHandler;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;

/**
 * <p>
 * MyBatis乐观锁插件
 * </p>
 * 
 * <pre>
 * 之前：update user set name = ?, password = ? where id = ?
 * 之后：update user set name = ?, password = ?, version = version+1 where id = ? and version = ?
 * 对象上的version字段上添加{@link Version}注解
 * sql可以不需要写version字段,只要对象version有值就会更新
 * 支持,int Integer, long Long, Date,Timestamp
 * 其他类型可以自定义实现,注入versionHandlers,多个以逗号分隔
 * </pre>
 *
 * @author TaoYu 小锅盖
 * @since 2017-04-08
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public final class OptimisticLockerInterceptor implements Interceptor {

	/**
	 * 根据对象类型缓存version基本信息
	 */
	private static final Map<Class<?>, LockerCache> versionCache = new ConcurrentHashMap<>();

	/**
	 * 根据version字段类型缓存的处理器
	 */
	private static final Map<Type, VersionHandler<?>> typeHandlers = new HashMap<>();

	private static final Expression RIGHT_EXPRESSION = new Column("?");

	static {
		IntegerTypeHandler integerTypeHandler = new IntegerTypeHandler();
		typeHandlers.put(int.class, integerTypeHandler);
		typeHandlers.put(Integer.class, integerTypeHandler);

		LongTypeHandler longTypeHandler = new LongTypeHandler();
		typeHandlers.put(long.class, longTypeHandler);
		typeHandlers.put(Long.class, longTypeHandler);

		typeHandlers.put(Date.class, new DateTypeHandler());
		typeHandlers.put(Timestamp.class, new TimestampTypeHandler());
	}

	public Object intercept(Invocation invocation) throws Exception {
		StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
		MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
		// 先判断是不是真正的UPDATE操作
		MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
		if (!ms.getSqlCommandType().equals(SqlCommandType.UPDATE)) {
			return invocation.proceed();
		}
		BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
		// 获得参数类型,去缓存中快速判断是否有version注解才继续执行
		Class<?> parameterClass = ms.getParameterMap().getType();
		LockerCache lockerCache = versionCache.get(parameterClass);
		if (lockerCache != null) {
			if (lockerCache.lock) {
				processChangeSql(ms, boundSql, lockerCache);
			}
		} else {
			Field versionField = getVersionField(parameterClass);
			if (versionField != null) {
				Class<?> fieldType = versionField.getType();
				if (!typeHandlers.containsKey(fieldType)) {
					throw new TypeException("乐观锁不支持" + fieldType.getName() + "类型,请自定义实现");
				}
				final TableField tableField = versionField.getAnnotation(TableField.class);
				String versionColumn = versionField.getName();
				if (tableField != null) {
					versionColumn = tableField.value();
				}
				LockerCache lc = new LockerCache(true, versionColumn, versionField, typeHandlers.get(fieldType));
				versionCache.put(parameterClass, lc);
				processChangeSql(ms, boundSql, lc);
			} else {
				versionCache.put(parameterClass, LockerCache.INSTANCE);
			}
		}
		return invocation.proceed();

	}

	private Field getVersionField(Class<?> parameterClass) {
		if (parameterClass != Object.class) {
			for (Field field : parameterClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Version.class)) {
					field.setAccessible(true);
					return field;
				}
			}
			return getVersionField(parameterClass.getSuperclass());
		}
		return null;

	}

	private void processChangeSql(MappedStatement ms, BoundSql boundSql, LockerCache lockerCache) throws Exception {
		Object parameterObject = boundSql.getParameterObject();
		if (parameterObject instanceof ParamMap) {
			ParamMap<?> paramMap = (ParamMap<?>) parameterObject;
			parameterObject = paramMap.get("et");
			EntityWrapper<?> entityWrapper = (EntityWrapper<?>) paramMap.get("ew");
			if (entityWrapper != null) {
				Object entity = entityWrapper.getEntity();
				if (entity != null && lockerCache.field.get(entity) == null) {
					changSql(ms, boundSql, parameterObject, lockerCache);
				}
			}
		} else {
			changSql(ms, boundSql, parameterObject, lockerCache);
		}
	}

	@SuppressWarnings("unchecked")
	private void changSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, LockerCache lockerCache)
			throws Exception {
		Field versionField = lockerCache.field;
		String versionColumn = lockerCache.column;
		final Object versionValue = versionField.get(parameterObject);
		if (versionValue != null) {// 先判断传参是否携带version,没带跳过插件
			Configuration configuration = ms.getConfiguration();
			// 给字段赋新值
			lockerCache.versionHandler.plusVersion(parameterObject, versionField, versionValue);
			// 处理where条件,添加?
			Update jsqlSql = (Update) CCJSqlParserUtil.parse(boundSql.getSql());
			BinaryExpression expression = (BinaryExpression) jsqlSql.getWhere();
			if (expression != null && !expression.toString().contains(versionColumn)) {
				EqualsTo equalsTo = new EqualsTo();
				equalsTo.setLeftExpression(new Column(versionColumn));
				equalsTo.setRightExpression(RIGHT_EXPRESSION);
				jsqlSql.setWhere(new AndExpression(equalsTo, expression));
				List<ParameterMapping> parameterMappings = new LinkedList<>(boundSql.getParameterMappings());
				parameterMappings.add(jsqlSql.getExpressions().size(), getVersionMappingInstance(configuration));
				MetaObject boundSqlMeta = configuration.newMetaObject(boundSql);
				boundSqlMeta.setValue("sql", jsqlSql.toString());
				boundSqlMeta.setValue("parameterMappings", parameterMappings);
			}
			// 设置参数
			boundSql.setAdditionalParameter("originVersionValue", versionValue);
		}
	}
	
	private volatile ParameterMapping parameterMapping;
	
	private ParameterMapping getVersionMappingInstance(Configuration configuration) {
		if (parameterMapping == null) {
			synchronized (OptimisticLockerInterceptor.class) {
				if (parameterMapping == null) {
					parameterMapping = new ParameterMapping.Builder(configuration, "originVersionValue",
							new UnknownTypeHandler(configuration.getTypeHandlerRegistry())).build();
				}
			}
		}
		return parameterMapping;
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {
		String versionHandlers = properties.getProperty("versionHandlers");
		if (StringUtils.isNotEmpty(versionHandlers)) {
			for (String handlerClazz : versionHandlers.split(",")) {
				try {
					registerHandler(Class.forName(handlerClazz));
				} catch (Exception e) {
					throw ExceptionFactory.wrapException("乐观锁插件自定义处理器注册失败", e);
				}
			}
		}
	}

	/**
	 * 注册处理器
	 */
	private static void registerHandler(Class<?> versionHandlerClazz) throws Exception {
		ParameterizedType parameterizedType = (ParameterizedType) versionHandlerClazz.getGenericInterfaces()[0];
		Object versionInstance = versionHandlerClazz.newInstance();
		if (!(versionInstance instanceof VersionHandler)) {
			throw new TypeException("参数未实现VersionHandler,不能注入");
		} else {
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments.length == 0) {
				throw new IllegalArgumentException("处理器泛型未定义");
			} else if (Object.class.equals(actualTypeArguments[0])) {
				throw new IllegalArgumentException("处理器泛型不能为Object");
			} else {
				typeHandlers.put(actualTypeArguments[0], (VersionHandler<?>) versionInstance);
			}
		}
	}

	// *****************************基本类型处理器*****************************
	private static class IntegerTypeHandler implements VersionHandler<Integer> {

		public void plusVersion(Object paramObj, Field field, Integer versionValue) throws Exception {
			field.set(paramObj, versionValue + 1);
		}
	}

	private static class LongTypeHandler implements VersionHandler<Long> {

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

	/**
	 * 缓存对象
	 */
	@SuppressWarnings("rawtypes")
	private static class LockerCache {

		public static final LockerCache INSTANCE = new LockerCache();

		private boolean lock;
		private String column;
		private Field field;
		private VersionHandler versionHandler;

		public LockerCache() {
		}

		LockerCache(Boolean lock, String column, Field field, VersionHandler versionHandler) {
			this.lock = lock;
			this.column = column;
			this.field = field;
			this.versionHandler = versionHandler;
		}
	}

}