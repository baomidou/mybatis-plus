/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.handlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;

/**
 * Fastjson2 实现 JSON 字段类型处理器
 *
 * @author nieqiurong
 * @since 3.5.5
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class Fastjson2TypeHandler extends AbstractJsonTypeHandler<Object> {

    public Fastjson2TypeHandler(Class<?> type) {
        super(type);
    }

    public Fastjson2TypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        return JSON.parseObject(json, this.getFieldType());
    }

    @Override
    public String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteMapNullValue,
            JSONWriter.Feature.WriteNullListAsEmpty, JSONWriter.Feature.WriteNullStringAsEmpty);
    }

}
