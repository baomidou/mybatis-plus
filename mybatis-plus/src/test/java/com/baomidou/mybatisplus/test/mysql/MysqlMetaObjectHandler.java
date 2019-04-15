/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * 测试，自定义元对象字段填充控制器，实现公共字段自动写入
 *
 * @author hubin
 * @since 2017-06-25
 */
public class MysqlMetaObjectHandler implements MetaObjectHandler {

    /**
     * 测试 user 表 name 字段为空自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("*************************");
        System.out.println("insert of mysql fill");
        System.out.println("*************************");
        // 测试下划线
        Object createDatetime = this.getFieldValByName("createDatetime", metaObject);
        System.out.println("createDatetime=" + createDatetime);
        if (createDatetime == null) {
            //测试实体没有的字段，配置在公共填充，不应该set到实体里面
            this.setInsertFieldValByName("createDatetime1", LocalDateTime.now(), metaObject)
                .setInsertFieldValByName("createDatetime", LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        System.out.println("*************************");
        System.out.println("update of mysql fill");
        System.out.println("*************************");
        //测试实体没有的字段，配置在公共填充，不应该set到实体里面
        this.setUpdateFieldValByName("updateDatetime1", LocalDateTime.now(), metaObject)
            .setUpdateFieldValByName("updateDatetime", LocalDateTime.now(), metaObject);
    }
}

