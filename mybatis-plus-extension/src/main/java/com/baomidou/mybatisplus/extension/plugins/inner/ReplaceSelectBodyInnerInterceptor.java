package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author miemie
 * @since 2020-11-19
 */
public class ReplaceSelectBodyInnerInterceptor implements InnerInterceptor {

    private static final Pattern pattern = Pattern.compile("\\{@((\\w+?)|(\\w+?:\\w+?)|(\\w+?:\\w+?:\\w+?))}");
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        PluginUtils.MpSqlSource source = PluginUtils.mpSqlSource(ms.getSqlSource());
        String sourceSql = source.sql();
        List<String> list = find(sourceSql);
        if (CollectionUtils.isNotEmpty(list)) {
            sourceSql = getTargetSql(sourceSql, list);
            source.sql(sourceSql);
            // then
            String sql = boundSql.getSql();
            List<String> find = find(sql);
            if (CollectionUtils.isNotEmpty(find)) {
                sql = getTargetSql(sql, find);
                PluginUtils.mpBoundSql(boundSql).sql(sql);
            }
        }
    }

    protected List<String> find(String sql) {
        Matcher matcher = pattern.matcher(sql);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    protected String getTargetSql(String sql, List<String> find) {
        for (String s : find) {
            String s1 = s.substring(2, s.length() - 1);
            int i1 = s1.indexOf(StringPool.COLON);
            String tableName;
            String alisa = null;
            String asAlisa = null;
            if (i1 < 0) {
                tableName = s1;
            } else {
                tableName = s1.substring(0, i1);
                s1 = s1.substring(i1 + 1);
                i1 = s1.indexOf(StringPool.COLON);
                if (i1 < 0) {
                    alisa = s1;
                } else {
                    alisa = s1.substring(0, i1);
                    asAlisa = s1.substring(i1 + 1);
                }
            }
            sql = sql.replace(s, getTargetSelectBody(tableName, alisa, asAlisa));
        }
        return sql;
    }

    protected String getTargetSelectBody(String tableName, String alisa, String asAlisa) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
        Assert.notNull(tableInfo, "can not find TableInfo Cache by \"%s\"", tableName);
        String s = tableInfo.chooseSelect(TableFieldInfo::isSelect);
        if (alisa == null) {
            return s;
        }
        return newSelectBody(s, alisa, asAlisa);
    }

    protected String newSelectBody(String selectBody, String alisa, String asAlisa) {
        String[] split = selectBody.split(StringPool.COMMA);
        StringBuilder sb = new StringBuilder();
        if (asAlisa == null) {
            for (String s : split) {
                sb.append(alisa).append(StringPool.DOT).append(s).append(StringPool.COMMA);
            }
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
