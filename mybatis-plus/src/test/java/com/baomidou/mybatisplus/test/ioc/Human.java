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
 * 试驾人员
 * </p>
 * 
 * @author hubin
 * @Date 2016-07-06
 */
public class Human {
	
	private String name;
	
	public Human(String name){
		this.name = name;
	}

	public void driver( ICar car ) {
		System.out.println("\n" + name + " 开始！试驾... ");
		if ( car.start() ) {
			car.driver();
			System.out.println(" 试驾结束！ ");
		} else {
			System.out.println(" 熄火！ ");
		}
	}

}
