package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

/**
 * 加密测试
 */
public class EncryptTest {

    @Test
    public void md5Base64() {
        Assert.assertEquals("Jgmg8jeuq9EyB1ybYtj1fg==",
            EncryptUtils.md5Base64("犯我中华者虽远必诛"));
    }

    @Test
    public void other() {
        System.out.println(TableInfoHelper.checkRelated(true, "order", "'order'"));
        System.out.println(TableInfoHelper.checkRelated(true, "order", "order"));
        System.out.println(TableInfoHelper.checkRelated(true, "orderFile", "'ORDER_FILE'"));
    }

    @Test
    public void testTableInfoHelper() {
        TableInfo info = TableInfoHelper.initTableInfo(null, Xx.class);
        System.out.println("----------- AllInsertSqlColumn -----------");
        System.out.println(info.getAllInsertSqlColumn());
        System.out.println("----------- AllInsertSqlProperty -----------");
        System.out.println(info.getAllInsertSqlProperty());
        System.out.println("----------- AllSqlSet -----------");
        System.out.println(info.getAllSqlSet(true, "ew.entity."));
        System.out.println("----------- AllSqlWhere -----------");
        System.out.println(info.getAllSqlWhere(true, true, "ew.entity."));
        String logicUpdate = "<script>UPDATE tb_test_data_logic <trim prefix=\"SET\" suffixOverrides=\",\"><if test=\"et.testInt!=null\">test_int=#{et.testInt},</if><if test=\"et.testStr!=null\">test_str=#{et.testStr},</if><if test=\"et.testDouble!=null\">test_double=#{et.testDouble},</if><if test=\"et.testBoolean!=null\">test_boolean=#{et.testBoolean},</if><if test=\"et.testDate!=null\">test_date=#{et.testDate},</if><if test=\"et.testTime!=null\">test_time=#{et.testTime},</if><if test=\"et.testDateTime!=null\">test_date_time=#{et.testDateTime},</if><if test=\"et.testTimestamp!=null\">test_timestamp=#{et.testTimestamp},</if><if test=\"et.createDatetime!=null\">create_datetime=#{et.createDatetime},</if>update_datetime=#{et.updateDatetime},<if test=\"et.version!=null\">version=#{et.version},</if><if test=\"ew != null and ew.sqlSet != null\">${ew.sqlSet}</if></trim> <choose><when test=\"ew!=null and !ew.emptyOfWhere\"><trim prefix=\"WHERE\" prefixOverrides=\"AND|OR\"><if test=\"ew.entity!=null\"><if test=\"ew.entity.id!=null\"> AND id=#{ew.entity.id}</if><if test=\"ew.entity.testInt!=null\"> AND test_int=#{ew.entity.testInt}</if><if test=\"ew.entity.testStr!=null\"> AND test_str=#{ew.entity.testStr}</if><if test=\"ew.entity.testDouble!=null\"> AND test_double=#{ew.entity.testDouble}</if><if test=\"ew.entity.testBoolean!=null\"> AND test_boolean=#{ew.entity.testBoolean}</if><if test=\"ew.entity.testDate!=null\"> AND test_date=#{ew.entity.testDate}</if><if test=\"ew.entity.testTime!=null\"> AND test_time=#{ew.entity.testTime}</if><if test=\"ew.entity.testDateTime!=null\"> AND test_date_time=#{ew.entity.testDateTime}</if><if test=\"ew.entity.testTimestamp!=null\"> AND test_timestamp=#{ew.entity.testTimestamp}</if><if test=\"ew.entity.createDatetime!=null\"> AND create_datetime=#{ew.entity.createDatetime}</if><if test=\"ew.entity.updateDatetime!=null\"> AND update_datetime=#{ew.entity.updateDatetime}</if><if test=\"ew.entity.deleted!=null\"> AND deleted=#{ew.entity.deleted}</if><if test=\"ew.entity.version!=null\"> AND version=#{ew.entity.version}</if></if> AND deleted=0<if test=\"ew.sqlSegment!=null and ew.sqlSegment!=''\"> AND ${ew.sqlSegment}</if></trim></when><otherwise>WHERE deleted=0</otherwise></choose></script>";
    }

    @Data
    public static class Xx {
        private Long id;
        @TableField(fill = FieldFill.INSERT)
        private String x1;
        @TableField(fill = FieldFill.INSERT_UPDATE, strategy = FieldStrategy.NOT_EMPTY)
        private String x2;
        @TableField(fill = FieldFill.UPDATE)
        private String x3;
        @TableField(strategy = FieldStrategy.NOT_EMPTY)
        private String x4;
        @TableField(value = "xx5", strategy = FieldStrategy.IGNORED, update = "%s+1")
        private String x5;
        @TableLogic
        private Integer deleted;
    }
}
