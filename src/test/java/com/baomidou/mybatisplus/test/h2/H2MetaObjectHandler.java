package com.baomidou.mybatisplus.test.h2;
/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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

import java.sql.Timestamp;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.mapper.MetaObjectHandler;

/**
 * <p>
 * 测试，自定义元对象字段填充控制器，实现公共字段自动写入
 * </p>
 *
 * @author hubin
 * @Date 2017-06-25
 */
public class H2MetaObjectHandler extends MetaObjectHandler {

    /**
     * 测试 user 表 name 字段为空自动填充
     */
    public void insertFill(MetaObject metaObject) {
        System.out.println("*************************");
        System.out.println("insert fill");
        System.out.println("*************************");

        // 测试下划线
        Object testType = getFieldValByName("testType", metaObject);
        System.out.println("testType=" + testType);
        if (testType == null) {
            setFieldValByName("testType", 3, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        System.out.println("*************************");
        System.out.println("update fill");
        System.out.println("*************************");
        setFieldValByName("lastUpdatedDt", new Timestamp(System.currentTimeMillis()), metaObject);
    }
}

