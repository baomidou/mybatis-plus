package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * @author nieqiurong 2020/10/6.
 */
public class PackageConfigTest {

    @Test
    void joinPackageTest() {
        Assertions.assertEquals("com.baomidou.demo", GeneratorBuilder.packageConfigBuilder().joinPackage("demo"));
        Assertions.assertEquals("com.baomidou.mp.demo", GeneratorBuilder.packageConfigBuilder().moduleName("mp").joinPackage("demo"));
        Assertions.assertEquals("com.baomihua.demo", GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").joinPackage("demo"));
        Assertions.assertEquals("com.baomihua.mp.demo", GeneratorBuilder.packageConfigBuilder().parent("com.baomihua").moduleName("mp").joinPackage("demo"));
    }

    private void buildAssert(PackageConfig packageConfig){
        Assertions.assertEquals(packageConfig.getParent(),"com.baomihua.demo");
        Assertions.assertEquals(packageConfig.getModuleName(),"demo");
        Assertions.assertEquals(packageConfig.getController(),"action");
        Assertions.assertEquals(packageConfig.getEntity(),"entity");
        Assertions.assertEquals(packageConfig.getMapper(),"dao");
        Assertions.assertEquals(packageConfig.getService(),"iservice");
        Assertions.assertEquals(packageConfig.getServiceImpl(),"serviceIm");
        Assertions.assertEquals(1,packageConfig.getPathInfo().size());
        Assertions.assertTrue(packageConfig.getPathInfo().containsKey(OutputFile.controller));
    }

    @Test
    void buildTest(){
        buildAssert(GeneratorBuilder.packageConfigBuilder().parent("com.baomihua")
            .moduleName("demo").controller("action").entity("entity")
            .mapper("dao").service("iservice").serviceImpl("serviceIm")
            .pathInfo(Collections.singletonMap(OutputFile.controller,"bbbb")).build());
    }

    @Test
    void testCustomFile() {
        var packageConfig = GeneratorBuilder.packageConfigBuilder().build();
        var injectionConfig = GeneratorBuilder.injectionConfigBuilder()
            .customFile(new CustomFile.Builder()
                .fileName("Dto.java").packageName("dto").templatePath("dto.vm")
                .build())
            .customFile(new CustomFile.Builder()
                .fileName("Vo.java").templatePath("vo.vm")
                .build())
            .customFile(new CustomFile.Builder()
                .fileName("Bo.java").packageName("com.baomidou.bo").templatePath("Bo.vm")
                .build())
            .build();
        Map<String, String> packageInfo = packageConfig.getPackageInfo(injectionConfig);
        Assertions.assertNotNull(packageInfo.get("Dto"));
        Assertions.assertEquals("com.baomidou.dto", packageInfo.get("Dto"));
        Assertions.assertNull(packageInfo.get("Vo"));
        Assertions.assertNotNull(packageInfo.get("Bo"));
        Assertions.assertEquals("com.baomidou.bo", packageInfo.get("Bo"));
    }

}
