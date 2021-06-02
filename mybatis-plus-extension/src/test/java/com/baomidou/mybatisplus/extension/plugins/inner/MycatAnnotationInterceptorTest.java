package com.baomidou.mybatisplus.extension.plugins.inner;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:xyz327@outlook.com">xizhou</a>
 * @since 2021/6/2 12:16 下午
 */
class MycatAnnotationInterceptorTest {

    MycatAnnotationInterceptor mycatAnnotationInterceptor = new MycatAnnotationInterceptor();

    @Test
    void beforeQuery() throws Exception {

        String sql = "/**mycat: sql=select 1 from test */select * form from t_user2;";
        BoundSql boundSql = new BoundSql(new Configuration(), sql, null, null);
        mycatAnnotationInterceptor.beforeQuery(null, null, null, null, null, boundSql);
        mycatAnnotationInterceptor.afterQuery(null, null, null, null, null, boundSql);

        Assertions.assertTrue(boundSql.getSql().contains("/**mycat"));

    }

}
