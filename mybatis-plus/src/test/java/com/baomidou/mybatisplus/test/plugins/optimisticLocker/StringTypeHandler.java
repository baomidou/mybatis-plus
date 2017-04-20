package com.baomidou.mybatisplus.test.plugins.optimisticLocker;

import java.lang.reflect.Field;

import com.baomidou.mybatisplus.plugins.VersionHandler;

public class StringTypeHandler implements VersionHandler<String> {

    public void plusVersion(Object paramObj, Field field, String versionValue) throws Exception {
        field.set(paramObj, String.valueOf(Long.parseLong(versionValue) + 1));
    }

}
