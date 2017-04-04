package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;

/**
 * 乐观锁处理器,底层接口
 *
 * @author TaoYu
 */
public interface VersionHandler<T> {

    /**
     * 根据类型,使得对象对应的字段+1
     */
    void plusVersion(Object paramObj, Field field, T versionValue) throws Exception;
}