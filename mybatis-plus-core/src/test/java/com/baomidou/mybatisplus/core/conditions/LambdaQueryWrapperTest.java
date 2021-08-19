package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2021-01-27
 */
class LambdaQueryWrapperTest extends BaseWrapperTest {

    @Test
    void testLambdaOrderBySqlSegment() {
        Wrappers.<Table>lambdaQuery()
            .orderByDesc(Table::getId);
    }


    @Data
    @TableName( "xxx")
    private static class Table {

        @TableId("`id`")
        private Long id;

        @TableField("`name`")
        private Long name;
    }

}
