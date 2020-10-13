package com.baomidou.mybatisplus.generator.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author nieqiurong 2020/10/6.
 */
public class PackageConfigTest {

    @Test
    void joinPackageTest() {
        Assertions.assertEquals("com.baomidou.demo", new PackageConfig().joinPackage("demo"));
        Assertions.assertEquals("com.baomidou.mp.demo", new PackageConfig().setModuleName("mp").joinPackage("demo"));
        Assertions.assertEquals("com.baomihua.demo", new PackageConfig().setParent("com.baomihua").joinPackage("demo"));
        Assertions.assertEquals("com.baomihua.mp.demo", new PackageConfig().setParent("com.baomihua").setModuleName("mp").joinPackage("demo"));
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
        Assertions.assertTrue(packageConfig.getPathInfo().containsKey("aaaa"));
    }

    @Test
    void buildTest(){
        buildAssert(new PackageConfig().setParent("com.baomihua")
            .setModuleName("demo").setController("action").setEntity("entity").setMapper("dao").setService("iservice").setServiceImpl("serviceIm").setPathInfo(Collections.singletonMap("aaaa","bbbb")));
        buildAssert(new PackageConfig.Builder().parent("com.baomihua")
            .moduleName("demo").controller("action").entity("entity").mapper("dao").service("iservice").serviceImpl("serviceIm").pathInfo(Collections.singletonMap("aaaa","bbbb")).build());
    }

}
