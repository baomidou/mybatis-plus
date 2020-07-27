package com.baomidou.mybatisplus.test.plugins.pagination;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SelectBody强转PlainSelect不支持sql里面最外层带union
 * 用SetOperationList处理sql带union的语句
 */
class SelectBodyToPlainSelectTest {

    /**
     * 报错的测试
     */
    @Test
    void testSelectBodyToPlainSelectThrowException() {
        Select selectStatement = null;
        try {
            String originalUnionSql = "select * from test union select * from test";
            selectStatement = (Select) CCJSqlParserUtil.parse(originalUnionSql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        assert selectStatement != null;
        Select finalSelectStatement = selectStatement;
        Assertions.assertThrows(ClassCastException.class, () -> {
            PlainSelect plainSelect = (PlainSelect) finalSelectStatement.getSelectBody();
        });
    }

    private Page<?> page = new Page<>();

    @BeforeEach
    void setup() {
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem order = new OrderItem();
        order.setAsc(true);
        order.setColumn("column");
        orderItems.add(order);
        OrderItem orderEmptyColumn = new OrderItem();
        orderEmptyColumn.setAsc(false);
        orderEmptyColumn.setColumn("");
        orderItems.add(orderEmptyColumn);
        page.setOrders(orderItems);
    }

    @Test
    void testPaginationInterceptorConcatOrderByBefore() {
        String actualSql = new PaginationInterceptor()
            .concatOrderBy("select * from test", page);

        assertThat(actualSql).isEqualTo("SELECT * FROM test ORDER BY column ASC");

        String actualSqlWhere = new PaginationInterceptor()
            .concatOrderBy("select * from test where 1 = 1", page);

        assertThat(actualSqlWhere).isEqualTo("SELECT * FROM test WHERE 1 = 1 ORDER BY column ASC");
    }

    @Test
    void testPaginationInterceptorConcatOrderByFix() {
        String actualSql = new PaginationInterceptor()
            .concatOrderBy("select * from test union select * from test2", page);
        assertThat(actualSql).isEqualTo("SELECT * FROM test UNION SELECT * FROM test2 ORDER BY column ASC");

        String actualSqlUnionAll = new PaginationInterceptor()
            .concatOrderBy("select * from test union all select * from test2", page);
        assertThat(actualSqlUnionAll).isEqualTo("SELECT * FROM test UNION ALL SELECT * FROM test2 ORDER BY column ASC");
    }

    @Test
    void testPaginationInterceptorConcatOrderByFixWithWhere() {
        String actualSqlWhere = new PaginationInterceptor()
            .concatOrderBy("select * from test where 1 = 1 union select * from test2 where 1 = 1", page);
        assertThat(actualSqlWhere).isEqualTo("SELECT * FROM test WHERE 1 = 1 UNION SELECT * FROM test2 WHERE 1 = 1 ORDER BY column ASC");

        String actualSqlUnionAll = new PaginationInterceptor()
            .concatOrderBy("select * from test where 1 = 1 union all select * from test2 where 1 = 1 ", page);
        assertThat(actualSqlUnionAll).isEqualTo("SELECT * FROM test WHERE 1 = 1 UNION ALL SELECT * FROM test2 WHERE 1 = 1 ORDER BY column ASC");
    }

    @Test
    void testPaginationInterceptorOrderByEmptyColumnFix() {
        String actualSql = new PaginationInterceptor()
            .concatOrderBy("select * from test", page);

        assertThat(actualSql).isEqualTo("SELECT * FROM test ORDER BY column ASC");

        String actualSqlWhere = new PaginationInterceptor()
            .concatOrderBy("select * from test where 1 = 1", page);

        assertThat(actualSqlWhere).isEqualTo("SELECT * FROM test WHERE 1 = 1 ORDER BY column ASC");
    }

}
