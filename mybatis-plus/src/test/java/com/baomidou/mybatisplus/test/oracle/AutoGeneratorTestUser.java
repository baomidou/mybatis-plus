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
package com.baomidou.mybatisplus.test.oracle;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.ConfigDataSource;
import com.baomidou.mybatisplus.generator.ConfigGenerator;
import com.baomidou.mybatisplus.generator.ConfigIdType;
import com.baomidou.mybatisplus.test.ConfigGeneratorTest;

/**
 * <p>
 * 自动生成映射工具类测试
 * </p>
 * 
 * @author hubin
 * @Date 2016-04-25
 */
public class AutoGeneratorTestUser extends ConfigGeneratorTest {

	/**
	 * 测试 run 执行
	 * <p>
	 * 配置方法查看 {@link ConfigGenerator}
	 * </p>
	 * 
	 */
	public static void main( String[] args ) {
		ConfigGenerator cg = getConfigGenerator();
		
		/* 此处设置 String 类型数据库ID，默认Long类型 */
		cg.setConfigIdType(ConfigIdType.STRING);

		/* oracle 数据库相关配置 */
		cg.setConfigDataSource(ConfigDataSource.ORACLE);
		cg.setDbDriverName("oracle.jdbc.driver.OracleDriver");
		cg.setDbUser("mp");
		cg.setDbPassword("mp");
		cg.setDbUrl("jdbc:oracle:thin:@localhost:1521:xe");

		/*
		 * 表主键 ID 生成类型, 自增该设置无效。
		 * <p>
		 * IdType.AUTO 			数据库ID自增
		 * IdType.INPUT			用户输入ID
		 * IdType.ID_WORKER		全局唯一ID，内容为空自动填充（默认配置）
		 * IdType.UUID			全局唯一ID，内容为空自动填充
		 * </p>
		 */
		cg.setIdType(IdType.UUID);
		
		/*
		 * 指定生成表名（默认，所有表）
		 */
		//cg.setTableNames(new String[]{"user"});
		
		AutoGenerator.run(cg);
	}

}
