package smoke;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 拦截器在 native 下通过 PluginUtils 读取 StatementHandler / BoundSql 属性，验证这些 MetaObject 属性访问有反射登记。
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class SmokeInterceptor implements Interceptor {

    static final AtomicInteger INVOCATIONS = new AtomicInteger();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        INVOCATIONS.incrementAndGet();
        PluginUtils.MPStatementHandler statementHandler = PluginUtils
                .mpStatementHandler((StatementHandler) invocation.getTarget());
        statementHandler.configuration();
        statementHandler.executor();
        statementHandler.parameterHandler();
        statementHandler.mappedStatement();
        statementHandler.boundSql();
        PluginUtils.MPBoundSql boundSql = statementHandler.mPBoundSql();
        boundSql.sql();
        boundSql.parameterObject();
        boundSql.parameterMappings();
        return invocation.proceed();
    }
}
