package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author nieqiurong 2020/10/8.
 */
public class TableFieldTest {

    @Test
    void convertTest(){
        TableField tableField;
        Assertions.assertFalse(new TableField().setName("desc").setPropertyName(new StrategyConfig(), "desc").isConvert());
        Assertions.assertTrue(new TableField().setName("desc").setPropertyName(new StrategyConfig(), "desc1").isConvert());
        Assertions.assertTrue(new TableField().setName("DESC").setPropertyName(new StrategyConfig(), "desc").isConvert());
        Assertions.assertTrue(new TableField().setName("desc").setKeyWords(true).setPropertyName(new StrategyConfig(), "desc").isConvert());
        Assertions.assertTrue(new TableField().setName("desc").setKeyWords(true).setPropertyName(new StrategyConfig(), "desc1").isConvert());
        Assertions.assertTrue(new TableField().setName("name").setPropertyName(new StrategyConfig().setEntityTableFieldAnnotationEnable(true), "name").isConvert());
        Assertions.assertTrue(new TableField().setName("name").setPropertyName(new StrategyConfig().setEntityTableFieldAnnotationEnable(true), "name1").isConvert());
        Assertions.assertFalse(new TableField().setName("NAME").setPropertyName(new StrategyConfig().setCapitalMode(true), "name").isConvert());
        Assertions.assertTrue(new TableField().setName("USER_NAME").setPropertyName(new StrategyConfig().setCapitalMode(true), "userName").isConvert());
        Assertions.assertTrue(new TableField().setName("USER_NAME").setPropertyName(new StrategyConfig().setCapitalMode(true), "userName1").isConvert());
        Assertions.assertTrue(new TableField().setName("USER_NAME").setPropertyName(new StrategyConfig().setCapitalMode(true), "userName").isConvert());
        Assertions.assertFalse(new TableField().setName("user_name").setPropertyName(new StrategyConfig().setColumnNaming(NamingStrategy.underline_to_camel), "userName").isConvert());
        Assertions.assertTrue(new TableField().setName("USER_NAME").setPropertyName(new StrategyConfig().setColumnNaming(NamingStrategy.underline_to_camel), "userName").isConvert());
        Assertions.assertTrue(new TableField().setName("user_name").setPropertyName(new StrategyConfig().setColumnNaming(NamingStrategy.no_change), "userName").isConvert());
        Assertions.assertFalse(new TableField().setName("USER_NAME").setPropertyName(new StrategyConfig().setColumnNaming(NamingStrategy.no_change), "USER_NAME").isConvert());
        Assertions.assertTrue(new TableField().setName("NAME").setPropertyName(new StrategyConfig().setColumnNaming(NamingStrategy.no_change), "name").isConvert());
        tableField = new TableField().setName("delete").setColumnType(DbColumnType.BOOLEAN).setPropertyName(new StrategyConfig().setEntityBooleanColumnRemoveIsPrefix(true), "delete");
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertFalse(tableField.isConvert());
        tableField = new TableField().setName("delete").setPropertyName("delete", new StrategyConfig().setEntityBooleanColumnRemoveIsPrefix(true), DbColumnType.BOOLEAN);
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertFalse(tableField.isConvert());
        tableField = new TableField().setName("is_delete").setColumnType(DbColumnType.BOOLEAN).setPropertyName(new StrategyConfig().setEntityBooleanColumnRemoveIsPrefix(true), "isDelete");
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertTrue(tableField.isConvert());
        tableField = new TableField().setName("is_delete").setPropertyName("isDelete", new StrategyConfig().setEntityBooleanColumnRemoveIsPrefix(true), DbColumnType.BOOLEAN);
        Assertions.assertEquals("delete", tableField.getPropertyName());
        Assertions.assertTrue(tableField.isConvert());
    }
}
