package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import lombok.Data;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 加密测试
 */
class EncryptTest {

    @Test
    void md5Base64() {
        assertThat(EncryptUtils.md5Base64("犯我中华者虽远必诛")).isEqualTo("Jgmg8jeuq9EyB1ybYtj1fg==");
    }

    /**
     * related 为 true 表示列名和属性名对不上,需要单独维护映射。
     */
    @Test
    void other() {
        // 驼峰开启:去掉引号后同名,或去掉下划线后同名,都算对得上
        assertThat(TableInfoHelper.checkRelated(true, "order", "'order'")).isFalse();
        assertThat(TableInfoHelper.checkRelated(true, "order", "order")).isFalse();
        assertThat(TableInfoHelper.checkRelated(true, "orderFile", "'ORDER_FILE'")).isFalse();
        assertThat(TableInfoHelper.checkRelated(true, "order", "order_id")).isTrue();
        assertThat(TableInfoHelper.checkRelated(true, "orderFile", "`ORDER_FILES`")).isTrue();
        // 驼峰关闭:只比全大写是否完全相同,下划线不当回事
        assertThat(TableInfoHelper.checkRelated(false, "order", "'ORDER'")).isFalse();
        assertThat(TableInfoHelper.checkRelated(false, "order", "ORDER_FILE")).isTrue();
    }

    /**
     * 特征化 TableInfo 拼出的四段 SQL 片段,记录填充列/自增主键/update 表达式/逻辑删除列的处理规则。
     */
    @Test
    void testTableInfoHelper() {
        TableInfo info = TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Xx.class);
        assertThat(info.getAllInsertSqlColumnMaybeIf("et.")).isEqualTo(
                "<if test=\"et.id != null\">\nid,\n</if>x1,\nx2,\n<if test=\"et.x3 != null\">x3,</if>\n"
                        + "<if test=\"et.x4 != null\">x4,</if>\n<if test=\"et.x5 != null\">xx5,</if>\n"
                        + "<if test=\"et.deleted != null\">deleted,</if>");
        assertThat(info.getAllInsertSqlPropertyMaybeIf("et.")).isEqualTo(
                "<if test=\"et.id != null\">\n#{et.id},\n</if>#{et.x1},\n#{et.x2},\n"
                        + "<if test=\"et.x3 != null\">#{et.x3},</if>\n<if test=\"et.x4 != null\">#{et.x4},</if>\n"
                        + "<if test=\"et.x5 != null\">#{et.x5},</if>\n<if test=\"et.deleted != null\">#{et.deleted},</if>");
        assertThat(info.getAllSqlSet(true, "ew.entity.")).isEqualTo(
                "<if test=\"ew.entity['x1'] != null\">x1=#{ew.entity.x1},</if>\nx2=#{ew.entity.x2},\n"
                        + "x3=#{ew.entity.x3},\n<if test=\"ew.entity['x4'] != null\">x4=#{ew.entity.x4},</if>\n"
                        + "xx5=xx5+1,");
        assertThat(info.getAllSqlWhere(false, true, true, "ew.entity.")).isEqualTo(
                "<if test=\"ew.entity.id != null\">id=#{ew.entity.id}</if>\n"
                        + "<if test=\"ew.entity['x1'] != null\"> AND x1=#{ew.entity.x1}</if>\n"
                        + "<if test=\"ew.entity['x2'] != null and ew.entity['x2'] != ''\"> AND x2=#{ew.entity.x2}</if>\n"
                        + "<if test=\"ew.entity['x3'] != null\"> AND x3=#{ew.entity.x3}</if>\n"
                        + "<if test=\"ew.entity['x4'] != null and ew.entity['x4'] != ''\"> AND x4=#{ew.entity.x4}</if>\n"
                        + "<if test=\"ew.entity['x5'] != null\"> AND xx5=#{ew.entity.x5}</if>");
    }

    @Data
    private static class Xx {

        @TableId(type = IdType.AUTO)
        private Long id;
        @TableField(fill = FieldFill.INSERT)
        private String x1;
        @TableField(fill = FieldFill.INSERT_UPDATE, whereStrategy = FieldStrategy.NOT_EMPTY)
        private String x2;
        @TableField(fill = FieldFill.UPDATE)
        private String x3;
        @TableField(whereStrategy = FieldStrategy.NOT_EMPTY)
        private String x4;
        @TableField(value = "xx5", updateStrategy = FieldStrategy.ALWAYS, update = "%s+1")
        private String x5;
        @TableLogic
        private Integer deleted;
    }
}
