package com.baomidou.mybatisplus.generator.config.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.INameConvert;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Date;


/**
 * @author nieqiurong 2020/9/21.
 */
public class TableInfoTest {

    @Test
    void getFieldNamesTest() {
        TableInfo tableInfo;
        tableInfo = new TableInfo();
        tableInfo.addFields(new TableField().setColumnName("name"));
        Assertions.assertEquals(tableInfo.getFieldNames(), "name");

        tableInfo = new TableInfo();
        tableInfo.addFields(new TableField().setColumnName("name"), new TableField().setColumnName("age"));
        Assertions.assertEquals(tableInfo.getFieldNames(), "name, age");

        tableInfo = new TableInfo();
        tableInfo.addFields(new TableField().setColumnName("name"), new TableField().setColumnName("age"), new TableField().setColumnName("phone"));
        Assertions.assertEquals(tableInfo.getFieldNames(), "name, age, phone");
    }

    @Test
    void processTableTest() {
        TableInfo tableInfo = new TableInfo().setName("user");
        tableInfo.processTable(new StrategyConfig(), new GlobalConfig());
        Assertions.assertFalse(tableInfo.isConvert());
        Assertions.assertEquals("UserMapper", tableInfo.getMapperName());
        Assertions.assertEquals("UserXml", tableInfo.getXmlName());
        Assertions.assertEquals("IUserService", tableInfo.getServiceName());
        Assertions.assertEquals("UserServiceImpl", tableInfo.getServiceImplName());
        Assertions.assertEquals("UserController", tableInfo.getControllerName());

        tableInfo = new TableInfo().setName("user");
        tableInfo.processTable(new StrategyConfig(), new GlobalConfig().setEntityName("%sEntity")
            .setXmlName("%sXml").setMapperName("%sDao").setControllerName("%sAction").setServiceName("%sService").setServiceImplName("%sServiceImp"));
        Assertions.assertTrue(tableInfo.isConvert());
        Assertions.assertEquals("UserEntity", tableInfo.getEntityName());
        Assertions.assertEquals("UserDao", tableInfo.getMapperName());
        Assertions.assertEquals("UserXml", tableInfo.getXmlName());
        Assertions.assertEquals("UserService", tableInfo.getServiceName());
        Assertions.assertEquals("UserServiceImp", tableInfo.getServiceImplName());
        Assertions.assertEquals("UserAction", tableInfo.getControllerName());

        tableInfo = new TableInfo().setName("user");
        tableInfo.processTable(new StrategyConfig().setNameConvert(new INameConvert() {
            @Override
            public String entityNameConvert(TableInfo tableInfo) {
                return "E" + tableInfo.getName();
            }

            @Override
            public String propertyNameConvert(TableField field) {
                return field.getName();
            }
        }), new GlobalConfig());
        Assertions.assertTrue(tableInfo.isConvert());
        Assertions.assertEquals("Euser", tableInfo.getEntityName());
    }

    @Test
    void importPackageTest() {
        TableInfo tableInfo;
        StrategyConfig strategyConfig;

        tableInfo = new TableInfo().setName("user");
        tableInfo.importPackage(new StrategyConfig(), new GlobalConfig());
        Assertions.assertEquals(1, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));

        tableInfo = new TableInfo().setName("user").setConvert(true);
        tableInfo.importPackage(new StrategyConfig(), new GlobalConfig());
        Assertions.assertEquals(2, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableName.class.getName()));

        tableInfo = new TableInfo().setName("user");
        strategyConfig = new StrategyConfig();
        tableInfo.importPackage(strategyConfig.setSuperEntityClass("con.baomihua.demo.SuperEntity"), new GlobalConfig());
        Assertions.assertEquals(1, tableInfo.getImportPackages().size());
        Assertions.assertFalse(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains("con.baomihua.demo.SuperEntity"));

        tableInfo = new TableInfo().setName("user");
        strategyConfig = new StrategyConfig();
        tableInfo.importPackage(strategyConfig, new GlobalConfig().setActiveRecord(true));
        Assertions.assertEquals(1, tableInfo.getImportPackages().size());
        Assertions.assertFalse(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Model.class.getName()));

        strategyConfig = new StrategyConfig();
        tableInfo = new TableInfo().setName("user");
        tableInfo.addFields(new TableField().setName("u_id").setPropertyName(strategyConfig, "uid").setColumnType(DbColumnType.LONG).setKeyFlag(true));
        tableInfo.importPackage(strategyConfig, new GlobalConfig());
        Assertions.assertEquals(2, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));

        strategyConfig = new StrategyConfig();
        tableInfo = new TableInfo().setName("user");
        tableInfo.addFields(new TableField().setName("u_id").setPropertyName(strategyConfig, "uid").setColumnType(DbColumnType.LONG).setKeyFlag(true).setKeyIdentityFlag(true));
        tableInfo.importPackage(strategyConfig, new GlobalConfig());
        Assertions.assertEquals(3, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));

        strategyConfig = new StrategyConfig().setLogicDeleteFieldName("delete_flag");
        tableInfo = new TableInfo().setName("user");
        tableInfo.addFields(new TableField().setName("u_id").setPropertyName(strategyConfig, "uid").setColumnType(DbColumnType.LONG).setKeyFlag(true));
        tableInfo.addFields(new TableField().setName("delete_flag").setPropertyName(strategyConfig, "deleteFlag").setColumnType(DbColumnType.BOOLEAN));
        tableInfo.importPackage(strategyConfig, new GlobalConfig());
        Assertions.assertEquals(4, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(com.baomidou.mybatisplus.annotation.TableField.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableLogic.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));

        tableInfo = new TableInfo().setName("user");
        tableInfo.addFields(new TableField().setName("name").setPropertyName(strategyConfig, "name").setColumnType(DbColumnType.STRING));
        tableInfo.importPackage(new StrategyConfig(), new GlobalConfig().setIdType(IdType.ASSIGN_ID));
        Assertions.assertEquals(1, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));

        tableInfo = new TableInfo().setName("user").setHavePrimaryKey(true);
        tableInfo.addFields(new TableField().setName("u_id").setPropertyName(strategyConfig, "uid").setColumnType(DbColumnType.LONG).setKeyFlag(true));
        tableInfo.importPackage(new StrategyConfig(), new GlobalConfig().setIdType(IdType.ASSIGN_ID));
        Assertions.assertEquals(3, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(IdType.class.getName()));

        tableInfo = new TableInfo().setName("user").setHavePrimaryKey(true);
        tableInfo.addFields(new TableField().setName("u_id").setPropertyName(strategyConfig, "uid").setColumnType(DbColumnType.LONG).setKeyFlag(true));
        tableInfo.addFields(new TableField().setName("create_time").setPropertyName(strategyConfig, "createTime").setColumnType(DbColumnType.DATE).setFill(FieldFill.DEFAULT.name()));
        tableInfo.importPackage(new StrategyConfig().entityBuilder().addTableFills(new TableFill("createTime", FieldFill.DEFAULT)).build(), new GlobalConfig());
        Assertions.assertEquals(5, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Date.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(com.baomidou.mybatisplus.annotation.TableField.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(FieldFill.class.getName()));

        tableInfo = new TableInfo().setName("user").setHavePrimaryKey(true);
        tableInfo.addFields(new TableField().setName("u_id").setPropertyName(strategyConfig, "uid").setColumnType(DbColumnType.LONG).setKeyFlag(true));
        tableInfo.addFields(new TableField().setName("version").setPropertyName(strategyConfig, "version").setColumnType(DbColumnType.LONG));
        tableInfo.importPackage(new StrategyConfig().setVersionFieldName("version"), new GlobalConfig());
        Assertions.assertEquals(3, tableInfo.getImportPackages().size());
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Serializable.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(TableId.class.getName()));
        Assertions.assertTrue(tableInfo.getImportPackages().contains(Version.class.getName()));
    }

    @Test
    void setEntityNameTest() {
        Assertions.assertTrue(new TableInfo().setName("user").setEntityName(new StrategyConfig(), "UserEntity").isConvert());
        Assertions.assertFalse(new TableInfo().setName("user").setEntityName(new StrategyConfig(), "User").isConvert());
        Assertions.assertFalse(new TableInfo().setName("USER").setEntityName(new StrategyConfig().setCapitalMode(true), "User").isConvert());
        Assertions.assertTrue(new TableInfo().setName("USER").setEntityName(new StrategyConfig().setCapitalMode(true), "UserEntity").isConvert());
        Assertions.assertTrue(new TableInfo().setName("test_user").setEntityName(new StrategyConfig().setNaming(NamingStrategy.no_change), "TestUser").isConvert());
        Assertions.assertFalse(new TableInfo().setName("user").setEntityName(new StrategyConfig().setNaming(NamingStrategy.no_change), "User").isConvert());
        Assertions.assertFalse(new TableInfo().setName("test_user").setEntityName(new StrategyConfig().setNaming(NamingStrategy.underline_to_camel), "TestUser").isConvert());
        Assertions.assertTrue(new TableInfo().setName("TEST_USER").setEntityName(new StrategyConfig().setNaming(NamingStrategy.underline_to_camel), "TestUser").isConvert());
    }

    @Test
    void getFileNameTest() {
        TableInfo tableInfo = new TableInfo();
        Assertions.assertEquals("UserEntity", tableInfo.getFileName("User", "", () -> "UserEntity"));
        Assertions.assertEquals("UserEntity", tableInfo.getFileName("User", null, () -> "UserEntity"));
        Assertions.assertEquals("UserTable", tableInfo.getFileName("User", "%sTable", () -> "UserEntity"));
        Assertions.assertEquals("UserTable", tableInfo.getFileName("User", "UserTable", () -> "UserEntity"));
    }

}
