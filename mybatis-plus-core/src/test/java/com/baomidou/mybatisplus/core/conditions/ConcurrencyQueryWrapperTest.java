package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConcurrencyQueryWrapperTest extends BaseWrapperTest {
    @Test
    void testSqlSegmentInMultiThread() {
        QueryWrapper<Table> lqw = new QueryWrapper<>();
        lqw.eq("id", 1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String sqlSegment = lqw.getSqlSegment();
                    System.out.println(Thread.currentThread().getName() + ":" + sqlSegment);
                    Assertions.assertEquals("(id = #{ew.paramNameValuePairs.MPGENVAL1})", sqlSegment);
                }
            }).start();
        }
        String sqlSegment = lqw.getSqlSegment();
        System.out.println(Thread.currentThread().getName() + ":" + sqlSegment);
        Assertions.assertEquals("(id = #{ew.paramNameValuePairs.MPGENVAL1})", sqlSegment);
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
