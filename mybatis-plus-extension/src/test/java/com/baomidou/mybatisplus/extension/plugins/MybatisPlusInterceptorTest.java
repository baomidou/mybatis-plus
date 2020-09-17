package com.baomidou.mybatisplus.extension.plugins;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-30
 */
class MybatisPlusInterceptorTest {

    @Test
    void setProperties() {
        Properties properties = new Properties();
        properties.setProperty("@page", "com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor");
        properties.setProperty("page:maxLimit", "10");
        properties.setProperty("page:dbType", "h2");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.setProperties(properties);
        List<InnerInterceptor> interceptors = interceptor.getInterceptors();

        assertThat(interceptors).isNotEmpty();
        assertThat(interceptors.size()).isEqualTo(1);

        InnerInterceptor page = interceptors.get(0);
        assertThat(page).isInstanceOf(PaginationInnerInterceptor.class);

        PaginationInnerInterceptor pii = (PaginationInnerInterceptor) page;
        assertThat(pii.getMaxLimit()).isEqualTo(10);
        assertThat(pii.getDbType()).isEqualTo(DbType.H2);
    }
}
