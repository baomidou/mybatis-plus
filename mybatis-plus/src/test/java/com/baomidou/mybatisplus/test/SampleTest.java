package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.junit.jupiter.api.Test;

class SampleTest {

    @Test
    void testTableInfoHelper2() {
        TableInfo info = TableInfoHelper.initTableInfo(null, CommonLogicData.class);
//        System.out.println("----------- AllInsertSqlColumn -----------");
//        System.out.println(info.getAllInsertSqlColumn());
//        System.out.println("----------- AllInsertSqlProperty -----------");
//        System.out.println(info.getAllInsertSqlProperty());
        System.out.println("----------- AllSqlSet -----------");
        System.out.println(info.getAllSqlSet(true, "ew.entity."));
        System.out.println("----------- AllSqlWhere -----------");
        System.out.println(info.getAllSqlWhere(false, true, "ew.entity."));
    }

    @Test
    void testTableInfoHelper3() {
        MetaClass metaClass = MetaClass.forClass(CommonData.class, new DefaultReflectorFactory());
        String property = metaClass.findProperty("TESTINT", true);
        System.out.println(property);
    }

    @Test
    void testWrapperOrderBy() {
        System.out.println(Wrappers.query().orderByAsc("1", "2", "3", "4").getSqlSegment());
        System.out.println(Wrappers.query().orderByDesc("1", "2", "3", "4").getSqlSegment());
    }

    @Test
    void testClone() {
        QueryWrapper<Object> wrapper = Wrappers.query().orderByAsc("1", "2", "3", "4");
        QueryWrapper<Object> clone = wrapper.clone().orderByDesc("5", "6", "7");
        System.out.println(wrapper.getSqlSegment());
        System.out.println(clone.getSqlSegment());
    }

    @Test
    void testPrefixOrder() {
        System.out.println(Wrappers.query().eq("order_id", 1).getSqlSegment());
    }
}
