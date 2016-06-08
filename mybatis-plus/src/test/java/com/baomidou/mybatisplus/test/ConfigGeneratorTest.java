package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.generator.ConfigGenerator;

public class ConfigGeneratorTest {

    protected static ConfigGenerator getConfigGenerator() {
        ConfigGenerator cg = new ConfigGenerator();
        cg.setEntityPackage("com.baomidou.entity");//entity 实体包路径
        cg.setMapperPackage("com.baomidou.mapper");//mapper 映射文件路径
        cg.setServicePackage("com.baomidou.service");//service 层路径
        cg.setXmlPackage("com.baomidou.mapper.xml");  // xml层路径（可以不写）
        cg.setServiceImplPackage("com.baomidou.service.impl");  // serviceimpl层路径（可以不写）
        cg.setFileOverride(false);  // 默认值为false , 是否覆盖当前路径下已有文件

        /**
         *  如果配置了就只生成指定表名的文件，如果不配置就全部表
         *  此处会对表名做校验，如果表名不存在 ，则不会生成任何文件。
         *  控制台会提示不存在的表名称
         * */
        cg.setTableNames("user,roles");

		/* 此处可以配置 SuperServiceImpl 子类路径，默认如下 */
        //cg.setSuperServiceImpl("com.baomidou.framework.service.impl.SuperServiceImpl");

		/* 此处设置 String 类型数据库ID，默认Long类型 */
        //cg.setConfigIdType(ConfigIdType.STRING);

        cg.setSaveDir("D:/mybatis-plus/");// 生成文件保存位置

		/*
         * 设置数据库字段是否为驼峰命名，驼峰 true 下划线分割 false
		 */
        cg.setColumnHump(false);
        /*
         * 表是否包括前缀
		 * <p>
		 * 例如 mp_user 生成实体类 false 为 MpUser , true 为 User
		 * </p>
		 */
        cg.setDbPrefix(false);
        return cg;
    }
}
