package com.baomidou.mybatisplus.test.pom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 三个 Boot 版本的 starter 发布 pom 形状完全一致：必带 mybatis-plus 与 spring-boot 的 autoconfigure、jdbc starter， 两个注解处理器只能 optional，BOM
 * 以 import 引入。会随 Boot 大版本变的只有依赖版本，所以版本断言留在各 starter 自己的子类里，公共作用域断言收在这份共享源，不再整文件复制三份。 基类自己不含用例（{@code @Test} 挂在子类上），所以叫
 * {@code *Support}。
 *
 * @author nieqiurong 2019/2/9.
 */
public abstract class StarterPomSupport {

    protected void assertStarterPom(GeneratedPom pom) {
        var mybatisPlus = pom.dependency("mybatis-plus");
        assertThat(mybatisPlus.scope()).isEqualTo("compile");
        assertThat(mybatisPlus.optional()).isFalse();
        var autoconfigure = pom.dependency("spring-boot-autoconfigure");
        assertThat(autoconfigure.scope()).isEqualTo("compile");
        assertThat(autoconfigure.optional()).isFalse();
        var jdbc = pom.dependency("spring-boot-starter-jdbc");
        assertThat(jdbc.scope()).isEqualTo("compile");
        assertThat(jdbc.optional()).isFalse();
        var configurationProcessor = pom.dependency("spring-boot-configuration-processor");
        assertThat(configurationProcessor.scope()).isEqualTo("compile");
        assertThat(configurationProcessor.optional()).isTrue();
        var autoconfigureProcessor = pom.dependency("spring-boot-autoconfigure-processor");
        assertThat(autoconfigureProcessor.scope()).isEqualTo("compile");
        assertThat(autoconfigureProcessor.optional()).isTrue();
        var bom = pom.dependency("spring-boot-dependencies");
        assertThat(bom.scope()).isEqualTo("import");
        assertThat(bom.optional()).isFalse();
    }
}
