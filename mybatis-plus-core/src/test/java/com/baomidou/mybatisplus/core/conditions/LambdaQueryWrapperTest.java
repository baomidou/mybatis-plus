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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LambdaQueryWrapper 测试
 *
 * @author miemie
 * @since 2021-01-27
 */
class LambdaQueryWrapperTest extends BaseWrapperTest {

    @BeforeAll
    static void initTableInfo() {
        TableInfo tableInfo = TableInfoHelper
                .initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Table.class);
        assertThat(tableInfo.getTableName()).isEqualTo("xxx");
    }

    @Test
    void testLambdaOrderBySqlSegment() {
        LambdaQueryWrapper<?> lqw = Wrappers.<Table>lambdaQuery().orderByDesc(Table::getId);
        assertThat(lqw.getSqlSegment()).isEqualTo(" ORDER BY `id` DESC");
        lqw.clear();
        assertThat(lqw.getSqlSegment()).isEmpty();
        lqw = Wrappers.<Table>lambdaQuery().eq(Table::getId, 1).nested(false, x -> x.eq(Table::getName, "李白"));
        assertThat(lqw.getSqlSegment()).isEqualTo("(`id` = #{ew.paramNameValuePairs.MPGENVAL1})");
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
