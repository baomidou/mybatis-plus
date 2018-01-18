package com.baomidou.mybatisplus.mapper;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;

import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 返回Map结果集，下划线转驼峰
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/12/19
 */
public class MybatisMapWrapper extends MapWrapper {

    public MybatisMapWrapper(MetaObject metaObject, Map<String, Object> map) {
        super(metaObject, map);
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        if (useCamelCaseMapping
            && ((name.charAt(0) >= 'A' && name.charAt(0) <= 'Z')
            || name.contains("_"))) {
            return StringUtils.underlineToCamel(name);
        }
        return name;
    }
}
