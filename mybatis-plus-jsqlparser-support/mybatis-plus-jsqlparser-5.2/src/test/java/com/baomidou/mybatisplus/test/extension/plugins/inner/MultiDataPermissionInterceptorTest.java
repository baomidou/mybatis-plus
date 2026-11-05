package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.jsqlparser.enums.ExpressionAppendMode;
import com.google.common.collect.HashBasedTable;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SQL多表场景的数据权限拦截器测试
 *
 * @author houkunlin
 * @since 3.5.2 +
 */
@Tag("unit")
public class MultiDataPermissionInterceptorTest {

    private static final Logger logger = LoggerFactory.getLogger(MultiDataPermissionInterceptorTest.class);
    /**
     * 这里可以理解为数据库配置的数据权限规则 SQL
     */
    private static final com.google.common.collect.Table<String, String, String> sqlSegmentMap;
    private static final DataPermissionInterceptor interceptor;
    private static final String TEST_1 = "com.baomidou.userMapper.selectByUsername";
    private static final String TEST_2 = "com.baomidou.userMapper.selectById";
    private static final String TEST_3 = "com.baomidou.roleMapper.selectByCompanyId";
    private static final String TEST_4 = "com.baomidou.roleMapper.selectById";
    private static final String TEST_5 = "com.baomidou.roleMapper.selectByRoleId";
    private static final String TEST_6 = "com.baomidou.roleMapper.selectUserInfo";
    private static final String TEST_7 = "com.baomidou.roleMapper.summarySum";
    private static final String TEST_8_1 = "com.baomidou.CustomMapper.selectByOnlyMyData";
    private static final String TEST_8_2 = "com.baomidou.CustomMapper.selectByOnlyOrgData";
    private static final String TEST_8_3 = "com.baomidou.CustomMapper.selectByOnlyDeptData";
    private static final String TEST_8_4 = "com.baomidou.CustomMapper.selectByMyDataOrDeptData";
    private static final String TEST_8_5 = "com.baomidou.CustomMapper.selectByMyData";

    static {
        sqlSegmentMap = HashBasedTable.create();
        sqlSegmentMap.put(TEST_1, "sys_user", "username='123' or userId IN (1,2,3)");
        sqlSegmentMap.put(TEST_2, "sys_user", "u.state=1 and u.amount > 1000");
        sqlSegmentMap.put(TEST_3, "sys_role", "companyId in (1,2,3)");
        sqlSegmentMap.put(TEST_4, "sys_role", "username like 'abc%'");
        sqlSegmentMap.put(TEST_5, "sys_role", "id=1 and role_id in (select id from sys_role)");
        sqlSegmentMap.put(TEST_6, "sys_user", "u.state=1 and u.amount > 1000");
        sqlSegmentMap.put(TEST_6, "sys_user_role", "r.role_id=3 AND r.role_id IN (7,9,11)");
        sqlSegmentMap.put(TEST_7, "`fund`", "a.id = 1 AND a.year = 2022 AND a.create_user_id = 1111");
        sqlSegmentMap.put(TEST_7, "`fund_month`", "b.fund_id = 2 AND b.month <= '2022-05'");
        sqlSegmentMap.put(TEST_8_1, "fund", "user_id=1");
        sqlSegmentMap.put(TEST_8_2, "fund", "org_id=1");
        sqlSegmentMap.put(TEST_8_3, "fund", "dept_id=1");
        sqlSegmentMap.put(TEST_8_4, "fund", "user_id=1 or dept_id=1");
        sqlSegmentMap.put(TEST_8_5, "table1", "u.user_id=1");
        sqlSegmentMap.put(TEST_8_5, "table2", "u.dept_id=1");
        interceptor = new DataPermissionInterceptor(new MultiDataPermissionHandler() {

            @Override
            public Expression getSqlSegment(final Table table, final Expression where, final String mappedStatementId) {
                try {
                    String sqlSegment = sqlSegmentMap.get(mappedStatementId, table.getName());
                    if (sqlSegment == null) {
                        logger.info("{} {} AS {} : NOT FOUND", mappedStatementId, table.getName(), table.getAlias());
                        return null;
                    }
                    if (table.getAlias() != null) {
                        // 替换表别名
                        sqlSegment = sqlSegment.replaceAll("u\\.", table.getAlias().getName() + StringPool.DOT);
                    }
                    Expression sqlSegmentExpression = CCJSqlParserUtil.parseCondExpression(sqlSegment);
                    logger.info(
                            "{} {} AS {} : {}", mappedStatementId, table.getName(), table.getAlias(),
                            sqlSegmentExpression.toString());
                    return sqlSegmentExpression;
                } catch (JSQLParserException e) {
                    throw new IllegalStateException("数据权限规则解析失败", e);
                }
            }
        });
    }

    @Test
    void test1() {
        assertSql(
                TEST_1, "select * from sys_user",
                "SELECT * FROM sys_user WHERE username = '123' OR userId IN (1, 2, 3)");
    }

    @Test
    void test2() {
        assertSql(
                TEST_2, "select u.username from sys_user u join sys_user_role r on u.id=r.user_id where r.role_id=3",
                "SELECT u.username FROM sys_user u JOIN sys_user_role r ON u.id = r.user_id WHERE r.role_id = 3 AND u.state = 1 AND u.amount > 1000");
    }

    @Test
    void test3() {
        assertSql(
                TEST_3, "select * from sys_role where company_id=6",
                "SELECT * FROM sys_role WHERE company_id = 6 AND companyId IN (1, 2, 3)");
    }

    @Test
    void test3unionAll() {
        assertSql(
                TEST_3, "select * from sys_role where company_id=6 union all select * from sys_role where company_id=7",
                "SELECT * FROM sys_role WHERE company_id = 6 AND companyId IN (1, 2, 3) UNION ALL SELECT * FROM sys_role WHERE company_id = 7 AND companyId IN (1, 2, 3)");
    }

    @Test
    void test4() {
        assertSql(
                TEST_4, "select * from sys_role where id=3",
                "SELECT * FROM sys_role WHERE id = 3 AND username LIKE 'abc%'");
    }

    @Test
    void test5() {
        assertSql(
                TEST_5, "select * from sys_role where id=3",
                "SELECT * FROM sys_role WHERE id = 3 AND id = 1 AND role_id IN (SELECT id FROM sys_role)");
    }

    @Test
    void test6() {
        // 显式指定 JOIN 类型时 JOIN 右侧表才能进行拼接条件
        assertSql(
                TEST_6, "select u.username from sys_user u LEFT join sys_user_role r on u.id=r.user_id",
                "SELECT u.username FROM sys_user u LEFT JOIN sys_user_role r ON u.id = r.user_id AND r.role_id = 3 AND r.role_id IN (7, 9, 11) WHERE u.state = 1 AND u.amount > 1000");
    }

    @Test
    void test7() {
        assertSql(
                TEST_7,
                "SELECT c.doc AS title, sum(c.total_paid_amount) AS total_paid_amount, sum(c.balance_amount) AS balance_amount FROM (SELECT `a`.`id`, `a`.`doc`, `b`.`month`, `b`.`total_paid_amount`, `b`.`balance_amount`, row_number() OVER (PARTITION BY `a`.`id` ORDER BY `b`.`month` DESC) AS `row_index` FROM `fund` `a` LEFT JOIN `fund_month` `b` ON `a`.`id` = `b`.`fund_id` AND `b`.`submit` = true) c WHERE c.row_index = 1 GROUP BY title LIMIT 20",
                "SELECT c.doc AS title, sum(c.total_paid_amount) AS total_paid_amount, sum(c.balance_amount) AS balance_amount FROM (SELECT `a`.`id`, `a`.`doc`, `b`.`month`, `b`.`total_paid_amount`, `b`.`balance_amount`, row_number() OVER (PARTITION BY `a`.`id` ORDER BY `b`.`month` DESC) AS `row_index` FROM `fund` `a` LEFT JOIN `fund_month` `b` ON `a`.`id` = `b`.`fund_id` AND `b`.`submit` = true AND b.fund_id = 2 AND b.month <= '2022-05' WHERE a.id = 1 AND a.year = 2022 AND a.create_user_id = 1111) c WHERE c.row_index = 1 GROUP BY title LIMIT 20");
    }

    @Test
    void test8() {
        assertSql(TEST_8_1, "select * from fund where id=3", "SELECT * FROM fund WHERE id = 3 AND user_id = 1");
        assertSql(TEST_8_2, "select * from fund where id=3", "SELECT * FROM fund WHERE id = 3 AND org_id = 1");
        assertSql(TEST_8_3, "select * from fund where id=3", "SELECT * FROM fund WHERE id = 3 AND dept_id = 1");
        assertSql(
                TEST_8_4, "select * from fund where id=3",
                "SELECT * FROM fund WHERE id = 3 AND (user_id = 1 OR dept_id = 1)");
        // 修改之前旧版的多表数据权限对这个SQL的表现形式：
        // 输入 "WITH temp AS (SELECT t1.field1, t2.field2 FROM table1 t1 LEFT JOIN table2 t2 on t1.uid = t2.uid) SELECT * FROM temp"
        // 输出 "WITH temp AS (SELECT t1.field1, t2.field2 FROM table1 t1 LEFT JOIN table2 t2 ON t1.uid = t2.uid) SELECT * FROM temp"
        // 修改之后的多表数据权限对这个SQL的表现形式
        assertSql(
                TEST_8_5,
                "WITH temp AS (SELECT t1.field1, t2.field2 FROM table1 t1 LEFT JOIN table2 t2 on t1.uid = t2.uid) SELECT * FROM temp",
                "WITH temp AS (SELECT t1.field1, t2.field2 FROM table1 t1 LEFT JOIN table2 t2 ON t1.uid = t2.uid AND t2.dept_id = 1 WHERE t1.user_id = 1) SELECT * FROM temp");
    }

    void assertSql(String mappedStatementId, String sql, String targetSql) {
        assertThat(interceptor.parserSingle(sql, mappedStatementId)).isEqualTo(targetSql);
    }

    @Test
    void innerJoinUsing() {
        List<String> tables = new ArrayList<>();
        DataPermissionInterceptor usingInterceptor = new DataPermissionInterceptor(
                (MultiDataPermissionHandler) (table, where, id) -> {
                    assertThat(id).isEqualTo("mapper.query");
                    tables.add(table.getName());
                    if (table.getName().startsWith("with_as")) {
                        return null;
                    }
                    return new EqualsTo(new Column(table.getAlias().getName() + ".tenant_id"), new LongValue(1));
                });
        usingInterceptor.setExpressionAppendMode(ExpressionAppendMode.LAST);
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id, tenant_id) WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id, tenant_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM accounts a JOIN details b USING (link_id) WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM accounts a JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM accounts a JOIN details b USING (link_id, tenant_id) WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM accounts a JOIN details b USING (link_id, tenant_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c USING (link_id) WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1 AND c.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c ON b.link_id = c.link_id WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) INNER JOIN secrets c ON b.link_id = c.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 AND c.tenant_id = 1 WHERE a.id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) LEFT JOIN secrets c ON b.link_id = c.link_id AND c.id = 23 WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT c.amount FROM accounts a INNER JOIN details b USING (link_id) LEFT JOIN secrets c ON b.link_id = c.link_id AND c.id = 23 AND c.tenant_id = 1 WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) RIGHT JOIN secrets c ON b.link_id = c.link_id ORDER BY c.id",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM accounts a INNER JOIN details b USING (link_id) RIGHT JOIN secrets c ON b.link_id = c.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 WHERE c.tenant_id = 1 ORDER BY c.id");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT c.amount FROM (accounts a INNER JOIN details b USING (link_id)) INNER JOIN secrets c ON b.link_id = c.link_id WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT c.amount FROM (accounts a INNER JOIN details b USING (link_id)) INNER JOIN secrets c ON b.link_id = c.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 AND c.tenant_id = 1 WHERE a.id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT c.amount FROM accounts a LEFT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT c.amount FROM accounts a LEFT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id AND b.tenant_id = 1 AND c.tenant_id = 1 WHERE a.id = 1 AND a.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT c.amount FROM accounts a RIGHT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id ORDER BY c.id",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT c.amount FROM accounts a RIGHT JOIN (details b INNER JOIN secrets c USING (link_id)) ON a.link_id = b.link_id AND a.tenant_id = 1 WHERE b.tenant_id = 1 AND c.tenant_id = 1 ORDER BY c.id");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM accounts a INNER JOIN details b ON a.link_id = b.link_id WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM accounts a INNER JOIN details b ON a.link_id = b.link_id AND a.tenant_id = 1 AND b.tenant_id = 1 WHERE a.id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT * FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1", "mapper.query"))
                .isEqualTo(
                        "SELECT * FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT link_id FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT link_id FROM accounts a INNER JOIN details b USING (link_id) WHERE a.id = 1 AND a.tenant_id = 1 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
        tables.clear();
        assertThat(
                usingInterceptor.parserSingle(
                        "SELECT b.amount FROM with_as_accounts a INNER JOIN details b USING (link_id) WHERE a.id = 3",
                        "mapper.query"))
                .isEqualTo(
                        "SELECT b.amount FROM with_as_accounts a INNER JOIN details b USING (link_id) WHERE a.id = 3 AND b.tenant_id = 1");
        assertThat(tables).doesNotHaveDuplicates();
    }
}
