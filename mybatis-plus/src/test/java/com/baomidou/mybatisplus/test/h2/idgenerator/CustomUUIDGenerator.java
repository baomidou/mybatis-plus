package com.baomidou.mybatisplus.test.h2.idgenerator;

import com.baomidou.mybatisplus.core.incrementer.IdGenerator;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.io.Serializable;

public class CustomUUIDGenerator implements IdGenerator {

    @Override
    public Serializable generate(Object entity) {
        String name = (String) SystemMetaObject.forObject(entity).getValue("name");
        if ("旺仔".equals(name)) {
            return "66666666666";
        } else if ("靓仔".equals(name)) {
            return "77777777777";
        }
        return IdWorker.get32UUID();
    }
}
