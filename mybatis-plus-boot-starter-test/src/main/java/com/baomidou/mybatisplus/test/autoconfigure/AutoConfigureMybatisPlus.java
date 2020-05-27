package com.baomidou.mybatisplus.test.autoconfigure;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

/**
 * {@link ImportAutoConfiguration Auto-configuration imports} for typical Mybatis tests. Most tests should consider
 * using {@link MybatisPlusTest @MybatisTest} rather than using this annotation directly.
 *
 * @author miemie
 * @since 2020-05-27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration
public @interface AutoConfigureMybatisPlus {
}
