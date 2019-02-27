package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 加密测试
 */
class EncryptTest {

    @Test
    void md5Base64() {
        Assertions.assertEquals("Jgmg8jeuq9EyB1ybYtj1fg==",
            EncryptUtils.md5Base64("犯我中华者虽远必诛"));
    }

    @Test
    void other() {
        System.out.println(TableInfoHelper.checkRelated(true, "order", "'order'"));
        System.out.println(TableInfoHelper.checkRelated(true, "order", "order"));
        System.out.println(TableInfoHelper.checkRelated(true, "orderFile", "'ORDER_FILE'"));
    }

    @Test
    void testTableInfoHelper() {
        TableInfo info = TableInfoHelper.initTableInfo(null, Xx.class);
        System.out.println("----------- AllInsertSqlColumn -----------");
        System.out.println(info.getAllInsertSqlColumnMaybeIf());
        System.out.println("----------- AllInsertSqlProperty -----------");
        System.out.println(info.getAllInsertSqlPropertyMaybeIf(null));
        System.out.println("----------- AllSqlSet -----------");
        System.out.println(info.getAllSqlSet(true, "ew.entity."));
        System.out.println("----------- AllSqlWhere -----------");
        System.out.println(info.getAllSqlWhere(true, true, "ew.entity."));
    }

    @Data
    private static class Xx {
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
