package com.baomidou.mybatisplus.generator.config.po;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 * @author nieqiurong 2020/9/21.
 */
public class TableInfoTest {

    @Test
    void getFieldNamesTest(){
        TableInfo tableInfo;
        tableInfo = new TableInfo();
        tableInfo.addFields(new TableField().setColumnName("name"));
        Assertions.assertEquals(tableInfo.getFieldNames(),"name");

        tableInfo = new TableInfo();
        tableInfo.addFields(new TableField().setColumnName("name"), new TableField().setColumnName("age"));
        Assertions.assertEquals(tableInfo.getFieldNames(),"name, age");

        tableInfo = new TableInfo();
        tableInfo.addFields(new TableField().setColumnName("name"), new TableField().setColumnName("age"), new TableField().setColumnName("phone"));
        Assertions.assertEquals(tableInfo.getFieldNames(),"name, age, phone");
    }

}
