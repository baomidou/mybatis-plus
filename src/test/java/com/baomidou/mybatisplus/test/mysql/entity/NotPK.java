/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableName;

/**
 * <p>
 * 测试实体没有主键依然注入通用方法
 * </p>
 *
 * @author Caratacu
 * @Date 2016-12-22
 */
@TableName("not_pk")
public class NotPK implements Serializable {

    // 静态属性会自动忽略
    private static final long serialVersionUID = 1L;

    private String uuid;

    private String type;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
