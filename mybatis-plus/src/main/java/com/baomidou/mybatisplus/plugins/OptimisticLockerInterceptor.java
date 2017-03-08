package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.statement.StatementHandler;
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
import org.apache.ibatis.type.TypeException;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * MyBatis乐观锁插件
 * 
 * <pre>
 * 只支持int和long类型,使用插件需要在需要在 启用的
 * 对象上的version字段上添加{@link Version}注解
 * </pre>
 * 
 * @author TaoYu
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class OptimisticLockerInterceptor implements Interceptor {

	private static final Map<Class<?>, VersionPo> versionCache = new ConcurrentHashMap<Class<?>, VersionPo>();

	public Object intercept(Invocation invocation) throws Exception {
		StatementHandler handler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
		MetaObject metaObject = SystemMetaObject.forObject(handler);
		MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
		if (ms.getSqlCommandType() != SqlCommandType.UPDATE) {
			return invocation.proceed();
		}
		BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
		Object parameterObject = boundSql.getParameterObject();
		Class<? extends Object> parameterClass = parameterObject.getClass();

		VersionPo versionPo = versionCache.get(parameterClass);
		if (versionPo != null && versionPo.isVersionControl) {
			toVersionSql(metaObject, boundSql, parameterObject, versionPo);

		} else {
			String versionColumn = null;
			String versionProperty = null;
			Class<?> versionType = null;
			for (Field field : parameterClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Version.class)) {
					versionProperty = field.getName();
					versionType = field.getType();
					if (field.isAnnotationPresent(TableName.class)) {
						versionColumn = field.getAnnotation(TableName.class).value();
					} else {
						versionColumn = field.getName();
					}
					break;
				}
			}
			if (StringUtils.isNotEmpty(versionColumn)) {
				versionPo = new VersionPo(true, versionProperty, versionColumn, versionType);
				versionCache.put(parameterClass, versionPo);
				toVersionSql(metaObject, boundSql, parameterObject, versionPo);
			} else {
				versionCache.put(parameterClass, new VersionPo(false));
			}
		}
		return invocation.proceed();

	}

	private void toVersionSql(MetaObject metaObject, BoundSql boundSql, Object parameterObject, VersionPo versionPo) throws Exception {
		final String versionProperty = versionPo.versionProperty;
		Object originalVersion = metaObject.getValue("delegate.boundSql.parameterObject." + versionProperty);
		if (originalVersion != null) {
			final Class<?> versionType = versionPo.versionType;
			final String versionColumn = versionPo.versionColumn;
			Object increValue = null;
			if (versionType.equals(Integer.class) || versionType.equals(int.class)) {
				increValue = (Integer) originalVersion + 1;
			} else if (versionType.equals(Long.class) || versionType.equals(long.class)) {
				increValue = (Long) originalVersion + 1;
			} else {
				throw new TypeException("Property " + versionProperty + " in " + parameterObject.getClass().getSimpleName() + " must be [ long, int ] or [ Long, Integer ]");
			}
			metaObject.setValue("delegate.boundSql.parameterObject." + versionProperty, increValue);
			String versionSql = new StringBuilder(boundSql.getSql()).append(" AND ").append(versionColumn).append(" = " + originalVersion).toString();
			metaObject.setValue("delegate.boundSql.sql", versionSql);
		}
	}

	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	public void setProperties(Properties properties) {
	}

	private class VersionPo {

		private Boolean isVersionControl;

		private String versionProperty;

		private String versionColumn;

		private Class<?> versionType;

		public VersionPo(Boolean isVersionControl) {
			super();
			this.isVersionControl = isVersionControl;
		}

		public VersionPo(Boolean isVersionControl, String versionProperty, String versionColumn, Class<?> versionType) {
			super();
			this.isVersionControl = isVersionControl;
			this.versionProperty = versionProperty;
			this.versionColumn = versionColumn;
			this.versionType = versionType;
		}
	}
}