/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.ConfigGenerator;

/**
 * <p>
 * 自动生成映射工具类测试
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class AutoGeneratorTest {

	/**
	 * 测试 run 执行
	 * <p>
	 * 配置方法查看 {@link ConfigGenerator}
	 * </p>
	 * 
	 */
	public static void main(String[] args) {
		ConfigGenerator cg = new ConfigGenerator();
		cg.setEntityPackage("com.baomidou.entity");// 实体包路径
		cg.setMapperPackage("com.baomidou.mapper");// 映射文件路径
		cg.setSaveDir("/Users/hubin/mybatis-plus/");// 生成文件保存位置

		/* 数据库相关配置 */
		cg.setDbDriverName("com.mysql.jdbc.Driver");
		cg.setDbUser("root");
		cg.setDbPassword("");
		cg.setDbUrl("jdbc:mysql://127.0.0.1:3306/mybatis-plus?characterEncoding=utf8");

		/**
		 * 表是否包括前缀
		 * <p>
		 * 例如 mp_user 生成实体类 false 为 MpUser , true 为 User
		 * </p>
		 */
		cg.setDbPrefix(false);
		AutoGenerator.run(cg);
	}

}
