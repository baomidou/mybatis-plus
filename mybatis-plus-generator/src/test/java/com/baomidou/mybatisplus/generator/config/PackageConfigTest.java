package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/6.
 */
@Tag("unit")
public class PackageConfigTest {

    @Test
    void joinPackageTest() {
        assertThat(GeneratorBuilder.packageConfigBuilder().joinPackage("demo")).isEqualTo("com.baomidou.demo");
        assertThat(GeneratorBuilder.packageConfigBuilder().moduleName("mp").joinPackage("demo"))
                .isEqualTo("com.baomidou.mp.demo");
        assertThat(GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").joinPackage("demo"))
                .isEqualTo("com.baomihua.demo");
        assertThat(GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").moduleName("mp").joinPackage("demo"))
                .isEqualTo("com.baomihua.mp.demo");
        assertThat(GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").moduleName("mp").joinPackage(null))
                .isEqualTo("com.baomihua.mp");
        assertThat(GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").moduleName("mp").joinPackage(""))
                .isEqualTo("com.baomihua.mp");
    }

    private void buildAssert(PackageConfig packageConfig) {
        assertThat(packageConfig.getParent()).isEqualTo("com.baomihua.demo");
        assertThat(packageConfig.getModuleName()).isEqualTo("demo");
        assertThat(packageConfig.getController()).isEqualTo("action");
        assertThat(packageConfig.getEntity()).isEqualTo("entity");
        assertThat(packageConfig.getMapper()).isEqualTo("dao");
        assertThat(packageConfig.getService()).isEqualTo("iservice");
        assertThat(packageConfig.getServiceImpl()).isEqualTo("serviceIm");
        assertThat(packageConfig.getPathInfo()).hasSize(1);
        assertThat(packageConfig.getPathInfo()).containsKey(OutputFile.controller);
    }

    @Test
    void buildTest() {
        buildAssert(
                GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").moduleName("demo").controller("action")
                        .entity("entity").mapper("dao").service("iservice").serviceImpl("serviceIm")
                        .pathInfo(Collections.singletonMap(OutputFile.controller, "bbbb")).build());
    }

    @Test
    void shouldBuildCustomFileInfo() {
        var packageConfig = GeneratorBuilder.packageConfigBuilder().build();
        var injectionConfig = GeneratorBuilder.injectionConfigBuilder()
                .customFile(
                        new CustomFile.Builder().fileName("Dto.java").packageName("dto").templatePath("dto.vm").build())
                .customFile(new CustomFile.Builder().fileName("Vo.java").templatePath("vo.vm").build())
                .customFile(
                        new CustomFile.Builder().fileName("Bo.java").packageName("com.baomidou.bo")
                                .templatePath("Bo.vm").build())
                .build();
        Map<String, String> packageInfo = packageConfig.getPackageInfo(injectionConfig);
        assertThat(packageInfo.get("Dto")).isNotNull();
        assertThat(packageInfo.get("Dto")).isEqualTo("com.baomidou.dto");
        assertThat(packageInfo.get("Vo")).isNull();
        assertThat(packageInfo.get("Bo")).isNotNull();
        assertThat(packageInfo.get("Bo")).isEqualTo("com.baomidou.bo");
    }

}
