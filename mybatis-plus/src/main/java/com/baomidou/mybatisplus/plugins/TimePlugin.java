package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;

/**
 * <h1>运行Sql和效率插件</h1>
 * 
 * <pre>
 * 主要是开发阶段使用,方便查看sql和执行时间
 * </pre>
 * 
 * @author TaoYu
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
		@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
		@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }) })
public class TimePlugin implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {

		Statement statement;
		Object firstArg = invocation.getArgs()[0];
		if (Proxy.isProxyClass(firstArg.getClass())) {
			statement = (Statement) SystemMetaObject.forObject(firstArg).getValue("h.statement");
		} else {
			statement = (Statement) firstArg;
		}
		long start = System.currentTimeMillis();
		Object proceed = invocation.proceed();
		long end = System.currentTimeMillis();
		String originalSql = statement.toString();
		int index = originalSql.indexOf(':');
		String sql = originalSql;
		if (index > 0) {
			sql = originalSql.substring(index + 1, originalSql.length());
		}
		System.err.println(sql + "\n 执行此条时间为: " + (end - start) + "毫秒 \n");
		return proceed;
	}

	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	public void setProperties(Properties properties) {

	}

}
