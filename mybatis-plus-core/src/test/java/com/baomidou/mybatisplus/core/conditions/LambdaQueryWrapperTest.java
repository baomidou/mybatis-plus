package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * LambdaQueryWrapper 测试
 *
 * @author miemie
 * @since 2021-01-27
 */
class LambdaQueryWrapperTest extends BaseWrapperTest {

    @BeforeAll
    static void initTableInfo() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Table.class);
        Assertions.assertEquals("xxx", tableInfo.getTableName());
    }

    @Test
    void testLambdaOrderBySqlSegment() {
        LambdaQueryWrapper lqw = Wrappers.<Table>lambdaQuery().orderByDesc(Table::getId);
        Assertions.assertEquals(lqw.getSqlSegment(), " ORDER BY `id` DESC");
        lqw.clear();
        Assertions.assertEquals(lqw.getSqlSegment(), "");
        lqw = Wrappers.<Table>lambdaQuery().eq(Table::getId, 1).nested(false, x -> x.eq(Table::getName, "李白"));
        Assertions.assertEquals(lqw.getSqlSegment(), "(`id` = #{ew.paramNameValuePairs.MPGENVAL1})");
    }


    @Data
    @TableName("xxx")
    private static class Table {

        @TableId("`id`")
        private Long id;

        @TableField("`name`")
        private Long name;
    }
}
