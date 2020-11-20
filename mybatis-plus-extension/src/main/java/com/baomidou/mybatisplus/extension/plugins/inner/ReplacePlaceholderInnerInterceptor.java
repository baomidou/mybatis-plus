package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

/**
 * 功能类似于 {@link GlobalConfig.DbConfig#isReplacePlaceholder()},
 * 只是这个是在运行时实时替换,适用范围更广
 *
 * @author miemie
 * @since 2020-11-19
 */
public class ReplacePlaceholderInnerInterceptor implements InnerInterceptor {

    protected final Log logger = LogFactory.getLog(this.getClass());

    private String escapeSymbol;

    public ReplacePlaceholderInnerInterceptor() {
    }

    public ReplacePlaceholderInnerInterceptor(String escapeSymbol) {
        this.escapeSymbol = escapeSymbol;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String sql = boundSql.getSql();
        List<String> find = SqlUtils.findPlaceholder(sql);
        if (CollectionUtils.isNotEmpty(find)) {
            sql = SqlUtils.replaceSqlPlaceholder(sql, find, escapeSymbol);
            PluginUtils.mpBoundSql(boundSql).sql(sql);
        }
    }
}
