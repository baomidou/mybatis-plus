package com.baomidou.mybatisplus.extension.plugins.chain;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author miemie
 * @since 2020-06-20
 */
@Data
@Accessors(chain = true)
@SuppressWarnings({"rawtypes"})
public class TenantQiuQiu implements QiuQiu {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private TenantHandler tenantHandler;

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        String sql = mpBs.sql();
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw ExceptionUtils.mpe("Failed to process, please exclude the tableName or statementId.\n Error SQL: %s", e, sql);
        }
    }

    @Override
    public void prepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
    }
}
