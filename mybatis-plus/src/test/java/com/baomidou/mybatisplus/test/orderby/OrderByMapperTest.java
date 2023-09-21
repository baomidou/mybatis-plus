package com.baomidou.mybatisplus.test.orderby;

import com.baomidou.mybatisplus.core.metadata.OrderFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author nieqiurong
 */
public class OrderByMapperTest extends BaseDbTest<OrderByMapper> {
    @Test
    void test() {
        doTest(mapper -> {
            mapper.selectList(Wrappers.emptyWrapper());

            mapper.selectList(Wrappers.<OrderByEntity>lambdaQuery().select(OrderByEntity::getName));
        });
        TableInfo tableInfo = TableInfoHelper.getTableInfo(OrderByEntity.class);
        for (OrderFieldInfo orderByField : tableInfo.getOrderByFields()) {
            if ("oid".equals(orderByField.getColumn())) {
                Assertions.assertEquals(orderByField.getSort(), 3);
                Assertions.assertEquals(orderByField.getType(), Constants.DESC);
            } else if ("age".equals(orderByField.getColumn())) {
                Assertions.assertEquals(orderByField.getSort(), 2);
                Assertions.assertEquals(orderByField.getType(), Constants.ASC);
            } else if ("tel".equals(orderByField.getColumn())) {
                Assertions.assertEquals(orderByField.getSort(), Short.MAX_VALUE);
                Assertions.assertEquals(orderByField.getType(), Constants.DESC);
            }
        }
    }

    @Override
    protected String tableDataSql() {
        return "insert into t_order_test(oid,name,nl,tel) values(1,'demo1',12, '13311111111'),(2,'demo2',13, '13322222222'),(3,'demo3',14, '13333333333');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists t_order_test", "CREATE TABLE IF NOT EXISTS t_order_test (" +
            "oid BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "tel VARCHAR(30) NULL DEFAULT NULL," +
            "nl INT NULL DEFAULT NULL," +
            "PRIMARY KEY (oid))");
    }

}
