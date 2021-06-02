package com.baomidou.mybatisplus.extension.plugins.inner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import lombok.Data;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @see <a href="https://www.yuque.com/ccazhw/tuacvk/dqizp3">Mycat1 注解</a>
 * @see <a href="https://www.yuque.com/books/share/6606b3b6-3365-4187-94c4-e51116894695/f9f24306bbd3992c1baff00cdb0956a4">Mycat2 注解</a>
 * @since 2021/6/2 11:04 上午
 */
public class MycatAnnotationInterceptor implements PostInnerInterceptor {

    private static final String PROHIBITION_SYMBOL = "?";

    private final Set<MyCatAnnotation> mycatAnnotation = new HashSet<>();
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    {
        mycatAnnotation.add(MyCatAnnotation.of("/**mycat", "*/", true));
        mycatAnnotation.add(MyCatAnnotation.of("/*balance", "*/", true));

        mycatAnnotation.add(MyCatAnnotation.of("/*!mycat", "*/", false));
        mycatAnnotation.add(MyCatAnnotation.of("/*#mycat", "*/", false));

        // mycat2
        mycatAnnotation.add(MyCatAnnotation.of("/*+ mycat", "*/", true));
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        parseSql(boundSql.getSql());
    }

    @Override
    public void afterQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        PluginUtils.mpBoundSql(boundSql).sql(buildSql(boundSql.getSql()));
    }

    private String buildSql(String originalSql) {
        String mycatAnno = threadLocal.get();
        if (StringUtils.isBlank(mycatAnno)) {
            return originalSql;
        }
        return mycatAnno + " " + originalSql;
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        BoundSql boundSql = PluginUtils.mpStatementHandler(sh).boundSql();
        parseSql(boundSql.getSql());
    }

    @Override
    public void afterPrepare(StatementHandler sh, Connection connections, Integer transactionTimeout) {
        BoundSql boundSql = PluginUtils.mpStatementHandler(sh).boundSql();
        PluginUtils.mpBoundSql(boundSql).sql(buildSql(boundSql.getSql()));
    }

    private void parseSql(String originSql) {
        MyCatAnnotation mycatAnno = findMycatAnno(originSql);
        if (mycatAnno == null) {
            return;
        }
        if (!mycatAnno.allow) {
            throw new IllegalArgumentException(String.format("不支持的 mycat 注解语法: %s%s", mycatAnno.prefix, mycatAnno.suffix));
        }
        int start = originSql.indexOf(mycatAnno.prefix);
        int end = originSql.indexOf(mycatAnno.suffix, start);
        String mycatAnnotation = originSql.substring(start, end + mycatAnno.suffix.length());
        // mycat 注解中不能使用 #{} . #{} 会被编译成 ? 而导致无法替换参数
        if (mycatAnnotation.contains(PROHIBITION_SYMBOL)) {
            throw new IllegalStateException("mybatis 不支持 mycat 注解中使用 #{} 的方式注入参数! 请使用 ${} 代替. 如果是存在 ? ,请删除");
        }
        threadLocal.set(mycatAnnotation);
    }

    private MyCatAnnotation findMycatAnno(String originSql) {
        for (MyCatAnnotation anno : mycatAnnotation) {
            if (originSql.contains(anno.prefix)) {
                return anno;
            }
        }
        return null;
    }

    @Override
    public void afterCompletion() {
        threadLocal.remove();
    }


    @Data(staticConstructor = "of")
    public static class MyCatAnnotation {

        private final String prefix;
        private final String suffix;
        private final boolean allow;

    }
}
