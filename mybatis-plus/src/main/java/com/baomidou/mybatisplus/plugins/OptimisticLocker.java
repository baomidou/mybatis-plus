package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.parameter.ParameterHandler;
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

import com.baomidou.mybatisplus.plugins.annotations.VersionColumn;
import com.baomidou.mybatisplus.plugins.annotations.VersionControl;
import com.baomidou.mybatisplus.toolkit.PluginUtils;

/**
 * MyBatis乐观锁插件
 * 
 * <pre>
 * 只支持int和long类型,使用插件需要在需要在 启用的
 * 对象上加{@link VersionControl}注解,在version字段上添加{@link VersionColumn}注解
 * </pre>
 * 
 * @author TaoYu
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class OptimisticLocker implements Interceptor {

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

		String versionColumn = null;
		Field versionField = null;
		VersionPo ttt = versionCache.get(parameterClass);
		if (ttt != null) {
			if (ttt.isVersionControl) {
				versionColumn = ttt.versionColmnName;
			}
		} else {
			if (parameterClass.isAnnotationPresent(VersionControl.class)) {
				for (Field field : parameterClass.getDeclaredFields()) {
					if (field.isAnnotationPresent(VersionColumn.class)) {
						versionField = field;
						VersionColumn veColumn = field.getAnnotation(VersionColumn.class);
						versionColumn = veColumn.value();
						break;
					}
				}
				if (versionColumn == null || versionColumn.isEmpty()) {
					versionCache.put(parameterClass, new VersionPo(false, versionColumn));
					return invocation.proceed();
				} else {
					versionCache.put(parameterClass, new VersionPo(false, versionColumn));
				}
			} else {
				versionCache.put(parameterClass, new VersionPo(false));
				return invocation.proceed();
			}
		}
		Object originalVersion = metaObject.getValue("delegate.boundSql.parameterObject." + versionColumn);
		Class<?> versionType = versionField.getType();
		if (versionType.equals(Integer.class) || versionField.equals(int.class)) {
			metaObject.setValue("delegate.boundSql.parameterObject." + versionColumn, (Integer) originalVersion + 1);
		} else if (versionType.equals(Long.class) || versionField.equals(long.class)) {
			metaObject.setValue("delegate.boundSql.parameterObject." + versionColumn, (Long) originalVersion + 1);
		} else {
			throw new TypeException("Property " + versionField.getName() + " in " + parameterClass.getSimpleName() + " must be [ long, int ] or [ Long, Integer ]");
		}
		String originalSql = boundSql.getSql();
		StringBuilder builder = new StringBuilder(originalSql);
		builder.append(" and ");
		builder.append(versionColumn);
		builder.append(" = " + originalVersion);
		metaObject.setValue("delegate.boundSql.sql", builder.toString());
		return invocation.proceed();

	}

	public Object plugin(Object target) {
		if (target instanceof StatementHandler || target instanceof ParameterHandler)
			return Plugin.wrap(target, this);
		return target;
	}

	public void setProperties(Properties properties) {
	}

	private class VersionPo {

		private Boolean isVersionControl;

		private String versionColmnName;

		public VersionPo(Boolean isVersionControl) {
			super();
			this.isVersionControl = isVersionControl;
		}

		public VersionPo(Boolean isVersionControl, String versionColmnName) {
			super();
			this.isVersionControl = isVersionControl;
			this.versionColmnName = versionColmnName;
		}

	}

}