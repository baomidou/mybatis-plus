package com.baomidou.mybatisplus.core.metadata;

import org.apache.ibatis.builder.MapperBuilderAssistant;

/**
 * TableInfo解析接口
 * @author jeff
 * @date 2020/9/8
 */
public interface TableInfoParser<T extends TableInfo> {

    /**
     * 构建TableInfo
     * @param clazz 构建对象类
     * @return TableInfo对象
     */
    T build(MapperBuilderAssistant builderAssistant, Class<?> clazz);

}
