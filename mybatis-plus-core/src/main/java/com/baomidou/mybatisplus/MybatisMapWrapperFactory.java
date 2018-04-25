package com.baomidou.mybatisplus;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import com.baomidou.mybatisplus.mapper.MybatisMapWrapper;

/**
 * <p>
 * 开启返回map结果集的下划线转驼峰
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/12/19
 */
public class MybatisMapWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return object != null && object instanceof Map;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MybatisMapWrapper(metaObject, (Map) object);
    }
}
