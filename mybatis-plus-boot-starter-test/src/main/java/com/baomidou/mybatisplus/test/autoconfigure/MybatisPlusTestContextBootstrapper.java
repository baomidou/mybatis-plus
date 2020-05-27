package com.baomidou.mybatisplus.test.autoconfigure;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContextBootstrapper;

/**
 * {@link TestContextBootstrapper} for {@link MybatisPlusTest @MybatisTest} support.
 *
 * @author miemie
 * @since 2020-05-27
 */
class MybatisPlusTestContextBootstrapper extends SpringBootTestContextBootstrapper {

    @Override
    protected String[] getProperties(Class<?> testClass) {
        MybatisPlusTest annotation = AnnotatedElementUtils.getMergedAnnotation(testClass, MybatisPlusTest.class);
        return (annotation != null) ? annotation.properties() : null;
    }

}
