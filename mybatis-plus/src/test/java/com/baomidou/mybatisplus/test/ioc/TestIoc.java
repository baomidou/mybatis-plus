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
package com.baomidou.mybatisplus.test.ioc;

/**
 * <p>
 * IOC 演示
 * </p>
 * 
 * @author hubin
 * @Date 2016-07-06
 */
public class TestIoc {

	/**
	 * IOC 控制反转, 依赖倒置
	 */
	public static void main( String[] args ) {
		//new 一个三毛
		Human sanMao = new Human("三毛");

		//三毛试驾宝马X6
		sanMao.driver(new Bmw());

		//三毛试驾哈佛H9
		sanMao.driver(new Haval());
	}

}
