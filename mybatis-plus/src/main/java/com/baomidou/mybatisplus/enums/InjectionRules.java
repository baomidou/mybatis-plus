/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.enums;

/**
 * <p>
 * Mybatis Plus 全局注入 规则
 * </p>
 * 
 * @author Caratacus
 * @Date 2016-12-22
 */
public enum InjectionRules {
	REQUIREDPK(1, "需要主键"), UNREQUIREDPK(2, "不需要主键");

	private final int type;

	/** 描述 */
	private final String desc;

	InjectionRules(final int type, final String desc) {
		this.type = type;
		this.desc = desc;
	}

	public static InjectionRules getInjectionRule(int type) {
		InjectionRules[] injectionRules = InjectionRules.values();
		for (InjectionRules rules : injectionRules) {
			if (rules.getType() == type) {
				return rules;
			}
		}
		return REQUIREDPK;
	}

	public int getType() {
		return this.type;
	}

	public String getDesc() {
		return this.desc;
	}

}
