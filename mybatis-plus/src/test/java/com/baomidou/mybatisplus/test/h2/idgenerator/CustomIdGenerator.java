package com.baomidou.mybatisplus.test.h2.idgenerator;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.ibatis.reflection.SystemMetaObject;

public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
        String bizKey = entity.getClass().getName();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        String name = (String) SystemMetaObject.forObject(entity).getValue("name");
        //long test
        if (tableInfo.getKeyType() == Long.class) {
            if ("旺仔".equals(name)) {
                return 666L;
            } else if ("靓仔".equals(name)) {
                return 777L;
            }
            return 1L;
        } else {
            // int test
            if ("旺仔".equals(name)) {
                return 666;
            } else if ("靓仔".equals(name)) {
                return 777;
            }
            return 1;
        }
    }

    @Override
    public String nextUUID(Object entity) {
        String name = (String) SystemMetaObject.forObject(entity).getValue("name");
        if ("旺仔".equals(name)) {
            return "66666666666";
        } else if ("靓仔".equals(name)) {
            return "77777777777";
        }
        return IdWorker.get32UUID();
    }
}
