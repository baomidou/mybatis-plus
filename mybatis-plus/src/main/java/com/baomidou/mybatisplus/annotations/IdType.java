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
package com.baomidou.mybatisplus.annotations;

/**
 * <p>
 * 数据库表ID类型枚举类
 * </p>
 * 
 * @author hubin
 * @Date 2015-11-10
 */
public enum IdType {
	AUTO_INCREMENT("0", "数据库ID自增"),
	ID_INPUT("1", "用户输入ID"),
	ID_WORKER("2", "IdWorkerKeyGenerator 生成全局唯一ID工具类");

	/** 主键 */
	private final String key;

	/** 描述 */
	private final String desc;

	IdType(final String key, final String desc) {
		this.key = key;
		this.desc = desc;
	}

	public String getKey() {
		return this.key;
	}

	public String getDesc() {
		return this.desc;
	}

}
