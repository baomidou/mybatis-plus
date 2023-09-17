package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author nieqiurong
 * @since 3.5.4
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(InnerInterceptor.class)
@ConditionalOnMissingBean(MybatisPlusInterceptor.class)
public class MybatisPlusInnerInterceptorAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor defaultMybatisPlusInterceptor(List<InnerInterceptor> innerInterceptorList) {
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.setInterceptors(innerInterceptorList);
        return plusInterceptor;
    }

}
