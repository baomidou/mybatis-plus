package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class SampleTest {

    private final ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testTableInfoHelper2() {
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
}
