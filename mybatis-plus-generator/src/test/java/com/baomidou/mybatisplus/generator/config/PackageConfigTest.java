package com.baomidou.mybatisplus.generator.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

}
