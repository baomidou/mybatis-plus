package com.baomidou.mybatisplus.extension.plugins;

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
        properties.setProperty("inner:page", "com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor");
        properties.setProperty("page:limit", "10");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.setProperties(properties);
        List<InnerInterceptor> interceptors = interceptor.getInterceptors();

        assertThat(interceptors).isNotEmpty();
        assertThat(interceptors.size()).isEqualTo(1);

        InnerInterceptor page = interceptors.get(0);
        assertThat(page).isInstanceOf(PaginationInnerInterceptor.class);

        PaginationInnerInterceptor pii = (PaginationInnerInterceptor) page;
        assertThat(pii.getLimit()).isEqualTo(10);
    }
}
