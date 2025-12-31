package com.baomidou.mybatisplus.extension.handlers;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.TypeFactory;


import java.lang.reflect.Field;

/**
 * jackson tools 实现的字段类型处理器
 *
 * @author milo
 * @since 3.5.15
 */
public class Jackson3TypeHandler extends AbstractJsonTypeHandler<Object> {

    private static ObjectMapper OBJECT_MAPPER;

    public Jackson3TypeHandler(Class<?> type) {
        super(type);
    }

    public Jackson3TypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        ObjectMapper objectMapper = getObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        JavaType javaType = typeFactory.constructType(getFieldType());
        return objectMapper.readValue(json, javaType);
    }

    @Override
    public String toJson(Object obj) {
        return getObjectMapper().writeValueAsString(obj);
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER == null ? Instance.OBJECT_MAPPER: OBJECT_MAPPER;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper should not be null");
        Jackson3TypeHandler.OBJECT_MAPPER = objectMapper;
    }

    private static class Instance {

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    }

}
