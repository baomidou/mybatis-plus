package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameJsqlParserInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@Tag("unit")
@Slf4j
class DynamicTableNameJsqlParserInnerInterceptorTest {

    private static final String JSQLPARSER_VERSION = jsqlParserVersion();

    private static final DynamicTableNameJsqlParserInnerInterceptor interceptor;

    static {
        interceptor = new DynamicTableNameJsqlParserInnerInterceptor((sql, tableName) -> {
            log.info("process table : {}", tableName);
            if (tableName.endsWith("`") || tableName.endsWith("]")) {
                char first = tableName.charAt(0);
                char last = tableName.charAt(tableName.length() - 1);
                return first + SqlParserUtils.removeWrapperSymbol(tableName) + "_r" + last;
            }
            return tableName + "_r";
        });
        interceptor.setShouldFallback(true);
    }

    private static final String SQL_SELECT_SUB_QUERY = singleLineSql("""
            SELECT /*+ materialize*/ strategy_idFROM ( SELECT strat.cf_strategy_id
            FROM strategy strt, doc_sect_ver prodGrp WHERE strat.src_id =
            prodGrp.struct_doc_sect_id AND strat.module_type IN ('sdfdsf','assdf'))""");

    private static final String SQL_SELECT_THREE_JOIN_WITH_ALIASE = singleLineSql("""
            select c.name, s.name, s.id, r.result from colleges c join students s on c.id = s.college_id
            join results r on s.id = r.student_idwhere c.id = 3 and r.dt =
            to_date('22-09-2005','dd-mm-yyyy')""");

    private static final String SQL_COMPLEX_ONE = singleLineSql("""
            INSERT INTO static_product ( DISCOUNT_ID, CATEGORY_ID, PRODUCT_ID ) (
            SELECT DISTINCT ALLNDC11.BUNDLE_DISCOUNT_ID, ALLNDC11.PRODUCT_ID, ALLNDC11.NDC11
            FROM ITEM ITEM INNER JOIN (SELECT NODE.SOURCE_ID NDC11, PR.PRODUCT_ID,
            BD1.BUNDLE_DISCOUNT_ID FROM DR_BUNDLE B, DR_BUNDLE_DISCOUNT BD1, DR_BD_PRODUCT
            PR, map_edge_ver node WHERE B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE
            AND NODE.EFF_END_DATE AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE
            AND NODE.VER_END_DATE AND B.BUNDLE_ID =BD1.BUNDLE_ID
            AND B.BUNDLE_STATUS =3 AND PR.BUNDLE_DISCOUNT_ID =BD1.BUNDLE_DISCOUNT_ID
            AND BD1.IS_DYNAMIC_CATEGORY!= 1 AND NODE.EDGE_TYPE = 1
            START WITH ( NODE.DEST_ID = PR.PRODUCT_ID
            AND B.BUNDLE_ID =BD1.BUNDLE_ID AND B.BUNDLE_STATUS =3
            AND PR.BUNDLE_DISCOUNT_ID =BD1.BUNDLE_DISCOUNT_ID AND BD1.IS_DYNAMIC_CATEGORY!= 1
            AND NODE.EDGE_TYPE = 1 AND B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE
            AND NODE.EFF_END_DATE AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE
            AND NODE.VER_END_DATE ) CONNECT BY ( PRIOR NODE.SOURCE_ID=NODE.DEST_ID
            AND PRIOR NODE.EDGE_TYPE = 1 AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE
            AND NODE.EFF_END_DATE AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE
            AND NODE.VER_END_DATE AND prior bd1.bundle_discount_id= bd1.bundle_discount_id) ) ALLNDC11
            ON (ALLNDC11.NDC11 = ITEM.CAT_MAP_ID) UNION ( SELECT BD1.BUNDLE_DISCOUNT_ID,
            PR.PRODUCT_ID, ITEM.CAT_MAP_ID FROM DR_BUNDLE B, DR_BUNDLE_DISCOUNT BD1,
            DR_BD_PRODUCT PR, ITEM ITEM WHERE B.BUNDLE_ID =BD1.BUNDLE_ID
            AND B.BUNDLE_STATUS =3 AND PR.BUNDLE_DISCOUNT_ID =BD1.BUNDLE_DISCOUNT_ID
            AND BD1.IS_DYNAMIC_CATEGORY!= 1 AND item.cat_map_id =pr.product_id )""");

    private static final String SQL_MERGE_COMPLEX = singleLineSql("""
            MERGE INTO cf_procedure proc USING ( WITH NON_STRATEGY_DETAILS AS (
            SELECT /*+ materialize*/ cf_strategy_id FROM ( SELECT strat.cf_strategy_id
            FROM cf_strategy strat, struct_doc_Sect_ver prodGrp
            WHERE strat.src_id = prodGrp.struct_doc_sect_id
            AND strat.src_mgr_id = prodGrp.mgr_id
            AND strat.src_ver_num = prodGrp.ver_num
            AND strat.module_type IN ('COMPL','PRCMSTR') ) ), NON_STRATEGY_COMPS AS (
            SELECT /*+ materialize*/ cf_component_id FROM ( SELECT comp.cf_component_id AS
            cf_component_id FROM cf_component comp, tier_basis_ver tb
            WHERE comp.bucket_src_id = tb.tier_basis_id
            AND comp.bucket_src_mgr_id = tb.mgr_id AND comp.bucket_src_ver_num =
            tb.ver_num AND comp.module_type IN ('COMPL','PRCMSTR') ) ) ,
            NON_STRAT_PERIODS AS ( SELECT /*+ materialize*/ cf_period_id
            FROM cf_period per, struct_doc_sect_ver prodGrp
            WHERE per.src_id = prodGrp.struct_doc_sect_id
            AND per.src_mgr_id = prodGrp.mgr_id AND per.src_ver_num = prodGrp.ver_num
            AND per.module_type IN ('COMPL','PRCMSTR') AND per.pmt_status NOT IN ('TERM','REV')
            SELECT DISTINCT cf_procedure_id FROM (SELECT /*+ LEADING(comp,proc)*/
            proc.cf_procedure_id AS cf_procedure_id FROM non_strategy_comps comp, cf_procedure
            proc WHERE proc.variable_name ='CALCULATION_LEVEL_RESULT'
            AND comp.cf_component_id = proc.cf_component_id UNION ALL
            SELECT /*+ LEADING(strat,proc)*/ proc.cf_procedure_id AS cf_procedure_id
            FROM cf_procedure proc, non_strategy_details strat
            WHERE proc.variable_name ='CALCULATION_LEVEL_RESULT'
            AND strat.cf_strategy_id = proc.cf_strategy_id UNION ALL
            SELECT /*+ LEADING(strat,proc)*/ proc.cf_procedure_id AS cf_procedure_id
            FROM cf_procedure proc, non_strat_periods periods
            WHERE proc.variable_name ='CALCULATION_LEVEL_RESULT'
            AND periods.CF_PERIOD_ID = proc.period_id ) )TMP
            ON (proc.cf_procedure_id = tmp.cf_procedure_id) WHEN MATCHED THEN UPDATE
            SET proc.variable_name = 'TierResultSSName';""");

    private static final String SQL_MERGE_COMPLEX_TWO = singleLineSql("""
            MERGE INTO cf_procedure_ver procVer USING (SELECT cf_procedure_id FROM cf_procedure proc
            WHERE proc.variable_name = 'TierResultSSName' ) proc_main
            ON (proc_main.cf_procedure_id = procVer.cf_procedure_id ) WHEN MATCHED THEN UPDATE
            SET procVer.variable_name = 'TierResultSSName' WHERE procVer.variable_name <> 'TierResultSSName';""");

    @Test
    public void shouldReplaceSingleTableName() {
        var sql = "SELECT name, age FROM table1 group by xyx";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT name, age FROM table1_r GROUP BY xyx");
    }

    @Test
    public void shouldReplaceTwoTableNames() {
        var sql = "SELECT name, age FROM table1,table2";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT name, age FROM table1_r, table2_r");
    }

    @Test
    public void shouldReplaceThreeTableNames() {
        var sql = "SELECT name, age FROM table1,table2,table3 group by xyx";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT name, age FROM table1_r, table2_r, table3_r GROUP BY xyx");
    }

    @Test
    public void shouldReplaceTableNameWithAlias() {
        var sql = "SELECT name, age FROM table1 t1 whatever group by xyx";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT name, age FROM table1_r t1 whatever group by xyx");
    }

    @Test
    public void shouldReplaceTwoTableNamesWithAlias() {
        var sql = "SELECT name, age FROM table1 t1,table2 t2 whatever group by xyx";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT name, age FROM table1_r t1,table2_r t2 whatever group by xyx");
    }

    @Test
    public void shouldReplaceTwoTableNamesWithAliasAndNoCondition() {
        var sql = "select xx from table1 a,table2 b";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT xx FROM table1_r a, table2_r b");
    }

    @Test
    public void shouldReplaceThreeTableNamesWithAlias() {
        var sql = "SELECT name, age FROM table1 t1,table2 t2, table3 t3 whatever group by xyx";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT name, age FROM table1_r t1,table2_r t2, table3_r t3 whatever group by xyx");
    }

    @Test
    public void shouldReplaceTableNameInSubQuery() {
        assertThat(interceptor.changeTable(SQL_SELECT_SUB_QUERY)).isEqualTo(singleLineSql("""
                SELECT /*+ materialize */ strategy_idFROM(SELECT strat.cf_strategy_id
                FROM strategy_r strt, doc_sect_ver_r prodGrp WHERE strat.src_id = prodGrp.struct_doc_sect_id
                AND strat.module_type IN ('sdfdsf', 'assdf'))"""));
    }

    @Test
    public void shouldReplaceTableNameInJoin() {
        var sql = "SELECT coluname(s) FROM table1 join table2 ON table1.coluname=table2.coluname";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT coluname(s) FROM table1_r JOIN table2_r ON table1.coluname = table2.coluname");
    }

    @Test
    public void shouldReplaceTableNameInJoinWithAlias() {
        var sql = "SELECT coluname(s) FROM table1 t1 join table2 t2 ON t1.coluname=t2.coluname";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT coluname(s) FROM table1_r t1 JOIN table2_r t2 ON t1.coluname = t2.coluname");
    }

    @Test
    public void shouldReplaceTableNameInLeftJoin() {
        var sql = "SELECT coluname(s) FROM table1 left outer join table2 ON table1.coluname=table2.coluname";
        assertThat(interceptor.changeTable(sql)).isEqualTo(
                "SELECT coluname(s) FROM table1_r LEFT OUTER JOIN table2_r ON table1.coluname = table2.coluname");
    }

    @Test
    public void shouldIgnoreDualTable() {
        var sql = "select * from dual";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM dual_r");
    }

    @Test
    public void shouldReplaceTwoJoinTableNamesWithAlias() {
        assertThat(interceptor.changeTable(SQL_SELECT_THREE_JOIN_WITH_ALIASE)).isEqualTo(singleLineSql("""
                select c.name, s.name, s.id, r.result from colleges_r c join students_r s
                on c.id = s.college_id join results_r r on s.id = r.student_idwhere c.id = 3
                and r.dt = to_date('22-09-2005','dd-mm-yyyy')"""));
    }

    @Test
    public void shouldReplaceTableNameInInsertValues() {
        var sql = "INSERT INTO table_name VALUES (value1,value2,value3,...)";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("INSERT INTO table_name_r VALUES (value1,value2,value3,...)");
    }

    @Test
    public void shouldReplaceTableNameInComplexInsert() {
        assertThat(interceptor.changeTable(SQL_COMPLEX_ONE)).isEqualTo(singleLineSql("""
                INSERT INTO static_product_r ( DISCOUNT_ID, CATEGORY_ID, PRODUCT_ID ) (
                SELECT DISTINCT ALLNDC11.BUNDLE_DISCOUNT_ID, ALLNDC11.PRODUCT_ID, ALLNDC11.NDC11
                FROM ITEM_r ITEM INNER JOIN (SELECT NODE.SOURCE_ID NDC11, PR.PRODUCT_ID,
                BD1.BUNDLE_DISCOUNT_ID FROM DR_BUNDLE_r B, DR_BUNDLE_DISCOUNT_r BD1,
                DR_BD_PRODUCT_r PR, map_edge_ver_r node WHERE B.DATE_ACTIVATED BETWEEN
                NODE.EFF_START_DATE AND NODE.EFF_END_DATE AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE
                AND NODE.VER_END_DATE AND B.BUNDLE_ID =BD1.BUNDLE_ID
                AND B.BUNDLE_STATUS =3 AND PR.BUNDLE_DISCOUNT_ID =BD1.BUNDLE_DISCOUNT_ID
                AND BD1.IS_DYNAMIC_CATEGORY!= 1 AND NODE.EDGE_TYPE = 1
                START WITH ( NODE.DEST_ID = PR.PRODUCT_ID
                AND B.BUNDLE_ID =BD1.BUNDLE_ID AND B.BUNDLE_STATUS =3
                AND PR.BUNDLE_DISCOUNT_ID =BD1.BUNDLE_DISCOUNT_ID AND BD1.IS_DYNAMIC_CATEGORY!= 1
                AND NODE.EDGE_TYPE = 1 AND B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE
                AND NODE.EFF_END_DATE AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE
                AND NODE.VER_END_DATE ) CONNECT BY ( PRIOR NODE.SOURCE_ID=NODE.DEST_ID
                AND PRIOR NODE.EDGE_TYPE = 1 AND PRIOR B.DATE_ACTIVATED BETWEEN
                NODE.EFF_START_DATE AND NODE.EFF_END_DATE AND PRIOR B.DATE_ACTIVATED BETWEEN
                NODE.VER_START_DATE AND NODE.VER_END_DATE AND prior bd1.bundle_discount_id=
                bd1.bundle_discount_id) ) ALLNDC11 ON (ALLNDC11.NDC11 = ITEM.CAT_MAP_ID) UNION (
                SELECT BD1.BUNDLE_DISCOUNT_ID, PR.PRODUCT_ID, ITEM.CAT_MAP_ID
                FROM DR_BUNDLE_r B, DR_BUNDLE_DISCOUNT_r BD1, DR_BD_PRODUCT_r PR, ITEM_r ITEM
                WHERE B.BUNDLE_ID =BD1.BUNDLE_ID AND B.BUNDLE_STATUS =3
                AND PR.BUNDLE_DISCOUNT_ID =BD1.BUNDLE_DISCOUNT_ID AND BD1.IS_DYNAMIC_CATEGORY!= 1
                AND item.cat_map_id =pr.product_id )"""));
    }

    @Test
    public void shouldReplaceTableNameInInsertSelect() {
        var sql = "INSERT INTO Customers (CustomerName, Country) SELECT SupplierName, Country FROM Suppliers;";
        assertThat(interceptor.changeTable(sql)).isEqualTo(
                "INSERT INTO Customers_r (CustomerName, Country) SELECT SupplierName, Country FROM Suppliers_r");
    }

    @Test
    public void shouldReplaceTableNameInDeleteWithSubQuery() {
        var sql = singleLineSql("""
                DELETE FROM validation_task WHERE task_name = 'ValidateSoldToCustId'
                AND conf_id IN (SELECT conf_id FROM validation_conf WHERE conf_name IN ('SaleValidation'))""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                DELETE FROM validation_task_r WHERE task_name = 'ValidateSoldToCustId'
                AND conf_id IN (SELECT conf_id FROM validation_conf_r WHERE conf_name IN ('SaleValidation'))"""));
    }

    @Test
    public void shouldHandleOracleSpecialDelete() {
        var sql = "delete table1 where column_name=xyz";
        assertThat(interceptor.changeTable(sql)).isEqualTo("DELETE table1_r WHERE column_name = xyz");
    }

    @Test
    public void shouldReplaceTableNameInAlter() {
        var sql = "ALTER TABLE Persons ADD UNIQUE (P_Id)";
        assertThat(interceptor.changeTable(sql)).isEqualTo("ALTER TABLE Persons_r ADD UNIQUE (P_Id)");
    }

    @Test
    public void shouldReplaceTableNameInAlterTable() {
        var sql = "ALTER TABLE table_name MODIFY coluname datatype";
        assertThat(interceptor.changeTable(sql)).isEqualTo("ALTER TABLE table_name_r MODIFY coluname datatype");
    }

    @Test
    public void shouldReplaceTableNameInDrop() {
        var sql = "DROP table tname;\n\r";
        assertThat(interceptor.changeTable(sql)).isEqualTo("DROP table tname_r");
    }

    @Test
    public void shouldReplaceTableNameInDropFunction() {
        var sql = "DROP FUNCTION functionName;";
        assertThat(interceptor.changeTable(sql)).isEqualTo("DROP FUNCTION functionName");
    }

    @Test
    public void shouldReplaceTableNameInDropProcedure() {
        var sql = "drop procedure procedureName";
        var expectedSql = "5.4".equals(JSQLPARSER_VERSION) ? "DROP procedure procedureName" : sql;
        assertThat(interceptor.changeTable(sql)).isEqualTo(expectedSql);
    }

    @Test
    public void shouldKeepViewNameInDropView() {
        var sql = "DROP view viewName";
        assertThat(interceptor.changeTable(sql)).isEqualTo(sql);
    }

    @Test
    public void shouldKeepIndexNameInDropIndex() {
        var sql = "DROP INDEX indexName";
        assertThat(interceptor.changeTable(sql)).isEqualTo(sql);
    }

    @Test
    public void shouldReplaceTableNamesInUnionAll() {
        var sql = "SELECT coluname(s) FROM table1 UNION ALL SELECT coluname(s) FROM table2;";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT coluname(s) FROM table1_r UNION ALL SELECT coluname(s) FROM table2_r");
    }

    @Test
    public void shouldReplaceTableNameInMerge() {
        var sql = singleLineSql("""
                MERGE INTO employees e USING hr_records h ON (e.id = h.emp_id) WHEN MATCHED THEN UPDATE
                SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address)
                VALUES (h.emp_id, h.address);""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                MERGE INTO employees_r e USING hr_records_r h ON (e.id = h.emp_id) WHEN MATCHED THEN UPDATE
                SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address)
                VALUES (h.emp_id, h.address)"""));
    }

    @Test
    public void shouldReplaceTableNamesInMergeUsing() {
        var sql = singleLineSql("""
                MERGE INTO employees e USING (SELECT * FROM hr_records WHERE start_date > ADD_MONTHS(SYSDATE,
                -1)) h ON (e.id = h.emp_id) WHEN MATCHED THEN UPDATE SET e.address = h.address
                WHEN NOT MATCHED THEN INSERT (id, address) VALUES (h.emp_id, h.address)""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                MERGE INTO employees_r e USING (SELECT * FROM hr_records_r
                WHERE start_date > ADD_MONTHS(SYSDATE, -1)) h ON (e.id = h.emp_id) WHEN MATCHED THEN UPDATE
                SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address)
                VALUES (h.emp_id, h.address)"""));
    }

    @Test
    public void shouldReplaceTableNamesInComplexMerge() {
        assertThat(interceptor.changeTable(SQL_MERGE_COMPLEX)).isEqualTo(singleLineSql("""
                MERGE INTO cf_procedure_r proc USING ( WITH NON_STRATEGY_DETAILS AS (
                SELECT /*+ materialize*/ cf_strategy_id FROM ( SELECT strat.cf_strategy_id
                FROM cf_strategy_r strat, struct_doc_Sect_ver_r prodGrp
                WHERE strat.src_id = prodGrp.struct_doc_sect_id
                AND strat.src_mgr_id = prodGrp.mgr_id
                AND strat.src_ver_num = prodGrp.ver_num
                AND strat.module_type IN ('COMPL','PRCMSTR') ) ), NON_STRATEGY_COMPS AS (
                SELECT /*+ materialize*/ cf_component_id FROM (
                SELECT comp.cf_component_id AS cf_component_id FROM cf_component_r comp,
                tier_basis_ver_r tb WHERE comp.bucket_src_id = tb.tier_basis_id
                AND comp.bucket_src_mgr_id = tb.mgr_id AND comp.bucket_src_ver_num =
                tb.ver_num AND comp.module_type IN ('COMPL','PRCMSTR') ) ) ,
                NON_STRAT_PERIODS AS ( SELECT /*+ materialize*/ cf_period_id
                FROM cf_period_r per, struct_doc_sect_ver_r prodGrp
                WHERE per.src_id = prodGrp.struct_doc_sect_id
                AND per.src_mgr_id = prodGrp.mgr_id AND per.src_ver_num =
                prodGrp.ver_num AND per.module_type IN ('COMPL','PRCMSTR')
                AND per.pmt_status NOT IN ('TERM','REV') SELECT DISTINCT cf_procedure_id
                FROM (SELECT /*+ LEADING(comp,proc)*/ proc.cf_procedure_id AS
                cf_procedure_id FROM non_strategy_comps_r comp, cf_procedure_r proc
                WHERE proc.variable_name ='CALCULATION_LEVEL_RESULT'
                AND comp.cf_component_id = proc.cf_component_id UNION ALL
                SELECT /*+ LEADING(strat,proc)*/ proc.cf_procedure_id AS cf_procedure_id
                FROM cf_procedure_r proc, non_strategy_details_r strat
                WHERE proc.variable_name ='CALCULATION_LEVEL_RESULT'
                AND strat.cf_strategy_id = proc.cf_strategy_id UNION ALL
                SELECT /*+ LEADING(strat,proc)*/ proc.cf_procedure_id AS cf_procedure_id
                FROM cf_procedure_r proc, non_strat_periods_r periods
                WHERE proc.variable_name ='CALCULATION_LEVEL_RESULT'
                AND periods.CF_PERIOD_ID = proc.period_id ) )TMP
                ON (proc.cf_procedure_id = tmp.cf_procedure_id) WHEN MATCHED THEN UPDATE
                SET proc.variable_name = 'TierResultSSName';"""));
    }

    @Test
    public void shouldReplaceTableNamesInComplexMerge2() {
        assertThat(interceptor.changeTable(SQL_MERGE_COMPLEX_TWO)).isEqualTo(singleLineSql("""
                MERGE INTO cf_procedure_ver_r procVer USING (SELECT cf_procedure_id FROM cf_procedure_r proc
                WHERE proc.variable_name = 'TierResultSSName') proc_main
                ON (proc_main.cf_procedure_id = procVer.cf_procedure_id) WHEN MATCHED THEN UPDATE
                SET procVer.variable_name = 'TierResultSSName' WHERE procVer.variable_name <> 'TierResultSSName'"""));
    }

    @Test
    public void shouldReplaceTableNameInCreateTable() {
        var sql = singleLineSql("""
                CREATE TABLE Persons(PersonID int,LastName varchar(255),FirstName varchar(255),Address
                varchar(255),City varchar(255));""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                CREATE TABLE Persons_r (PersonID int, LastName varchar (255), FirstName varchar (255), Address
                varchar (255), City varchar (255))"""));
    }

    @Test
    public void shouldReplaceTableNameInCreateGlobalTable() {
        var sql = singleLineSql("""
                CREATE GLOBAL TEMPORARY TABLE excl_cust (gen_name VARCHAR2(100),run_date TIMESTAMP(3),
                item_root_uuid VARCHAR2(22), owner_member_id NUMBER(20)) ON COMMIT DELETE ROWS""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                CREATE GLOBAL TEMPORARY TABLE excl_cust_r (gen_name VARCHAR2 (100), run_date TIMESTAMP (3),
                item_root_uuid VARCHAR2 (22), owner_member_id NUMBER (20)) ON COMMIT DELETE ROWS"""));
    }

    @Test
    public void shouldReplaceTableNameInCreateIndex() {
        var sql = "CREATE INDEX temp_name_idx ON table1(name) NOLOGGING PARALLEL (DEGREE 8);";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("CREATE INDEX temp_name_idx ON table1_r (name) NOLOGGING PARALLEL (DEGREE8)");
    }

    @Test
    public void shouldReplaceTableNameInCreateView() {
        var sql = "CREATE VIEW dept AS SELECT * FROM dept;";
        assertThat(interceptor.changeTable(sql)).isEqualTo("CREATE VIEW dept AS SELECT * FROM dept_r");
    }

    @Test
    public void shouldReplaceTableNameInCreateView2() {
        var sql = singleLineSql("""
                CREATE VIEW division1_staff AS SELECT ename, empno, job, dname FROM emp, dept
                WHERE emp.deptno IN (10, 30) AND emp.deptno = dept.deptno;""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                CREATE VIEW division1_staff AS SELECT ename, empno, job, dname FROM emp_r, dept_r
                WHERE emp.deptno IN (10, 30) AND emp.deptno = dept.deptno"""));
    }

    @Test
    public void shouldReplaceTableNameInCreateType() {
        var sql = "CREATE OR REPLACE TYPE TYPE_NAME IS TABLE OF VARCHAR2(100)";
        assertThat(interceptor.changeTable(sql)).isEqualTo(sql);
    }

    @Test
    public void shouldReplaceTableNameInUpdate() {
        var sql = "UPDATE tableName SET column1 = expression1, column2 = expression2";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("UPDATE tableName_r SET column1 = expression1, column2 = expression2");
    }

    @Test
    public void shouldReplaceTableNameInUpdateSubQuery() {
        var sql = singleLineSql("""
                UPDATE table1 SET table1.value = (SELECT table2.CODE FROM table2
                WHERE table1.value = table2.DESC) WHERE table1.UPDATETYPE='blah'
                AND EXISTS (SELECT table2.CODE FROM table2 WHERE table1.value = table2.DESC);""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                UPDATE table1_r SET table1.value = (SELECT table2.CODE FROM table2_r
                WHERE table1.value = table2.DESC) WHERE table1.UPDATETYPE = 'blah'
                AND EXISTS (SELECT table2.CODE FROM table2_r WHERE table1.value = table2.DESC)"""));
    }

    @Test
    public void shouldReplaceTableNameInUpdateSubQuery2() {
        var sql = singleLineSql("""
                UPDATE (SELECT table1.value as OLD, table2.CODE as NEW FROM table1 INNER JOIN table2
                ON table1.value = table2.DESC WHERE table1.UPDATETYPE='blah' ) t SET t.OLD = t.NEW""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                UPDATE (SELECT table1.value as OLD, table2.CODE as NEW FROM table1_r INNER JOIN table2_r
                ON table1.value = table2.DESC WHERE table1.UPDATETYPE='blah' ) t SET t.OLD = t.NEW"""));
    }

    @Test
    public void shouldReplaceTableNameInUpdateSubQueryWithOracleHint() {
        var sql = singleLineSql("""
                update /*+ PARALLEL OPT_PARAM('parallel_min_percent','0') */ eligible ec
                set ec.END_DATE = ec.END_DATE + INTERVAL '0 0:0:0.999' DAY TO SECOND""");
        var expectedSql = sql.replace("eligible ec", "eligible_r ec");
        if ("5.4".equals(JSQLPARSER_VERSION)) {
            expectedSql = expectedSql.replace("update ", "UPDATE ").replace(" set ", " SET ");
        }
        assertThat(interceptor.changeTable(sql)).isEqualTo(expectedSql);
    }

    @Test
    public void shouldReplaceTableNameInTruncate() {
        var sql = "truncate table eligible_item";
        assertThat(interceptor.changeTable(sql)).isEqualTo("TRUNCATE TABLE eligible_item_r");
    }

    @Test
    public void shouldReplaceTableNameWithComment() {
        var sql = "select * from foo -- this is a comment";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r");
    }

    @Test
    public void shouldReplaceTableNameWithCommentContainingKeyword() {
        var sql = "select * from foo -- what happens if I say update in a comment";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r");
    }

    @Test
    public void shouldReplaceTableNameWithCommentEndingWithKeyword() {
        var sql = "select * from foo -- what happens if I end a comment with an update";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r");
    }

    @Test
    public void shouldReplaceTableNameWithCommentInMiddle() {
        var sql = "select * -- I like stars \n from foo";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r");
    }

    @Test
    public void shouldReplaceTableNameWithCommentInMiddleAndEnd() {
        var sql = "select * -- I like stars \n from foo -- comment ending with update";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r");
    }

    @Test
    public void shouldReplaceTableNameWithMultipleCommentsInMiddle() {
        var sql = """
                select * -- I like stars
                 from foo f -- I like foo
                 join bar b -- I also like bar
                 on f.id = b.id""";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r f JOIN bar_r b ON f.id = b.id");
    }

    @Test
    public void shouldReplaceTableNameWithMultipleCommentsAndNewlines() {
        var sql = """
                select * -- I like stars
                 from foo f -- I like foo

                 join bar b -- I also like bar
                 on f.id = b.id""";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r f JOIN bar_r b ON f.id = b.id");
    }

    @Test
    public void shouldReplaceTableNameWithMultipleCommentsInMiddleAndEnd() {
        var sql = """
                select * -- I like stars
                 from foo f -- I like foo
                 join bar b -- I also like bar
                 on f.id = b.id -- comment ending with update""";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM foo_r f JOIN bar_r b ON f.id = b.id");
    }

    @Test
    void shouldReplaceTableNameInSelectForUpdate() {
        //TODO 暂时解决不能使用的问题,当碰到for update nowait这样的,后面的 nowait 会被当做成表但也不是很影响苗老板的动态表过滤.
        var sql = "select * from mp where id = 1 for update";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM mp_r WHERE id = 1 FOR UPDATE");
    }

    @Test
    public void shouldReplaceTableNameInOnDuplicateKeyUpdate() {
        var sql = singleLineSql("""
                INSERT INTO cf_procedure (_id,password) VALUES ('1','password')
                ON DUPLICATE KEY UPDATE id = 'UpId', password = 'upPassword';""");
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                INSERT INTO cf_procedure_r (_id, password) VALUES ('1', 'password')
                ON DUPLICATE KEY UPDATE id = 'UpId', password = 'upPassword'"""));
    }

    @Test
    public void shouldReplaceTableNameInUpdateIgnore() {
        var sql = "update ignore student set name = 'abc' where id = 4";
        assertThat(interceptor.changeTable(sql)).isEqualTo("UPDATE IGNORE student_r SET name = 'abc' WHERE id = 4");

        sql = "UPDATE IGNORE student set name = 'abc' where id = 4";
        assertThat(interceptor.changeTable(sql)).isEqualTo("UPDATE IGNORE student_r SET name = 'abc' WHERE id = 4");
    }

    @Test
    public void shouldReplaceTableNameInInsertIgnore() {
        var sql = "INSERT IGNORE INTO student (userid,username) VALUES (2,'swan'),(4,'bear') ;";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("INSERT IGNORE INTO student_r (userid, username) VALUES (2, 'swan'), (4, 'bear')");
    }

    @Test
    void shouldReplaceTableNameInCreateUniqueIndex() {
        var sql = "CREATE UNIQUE INDEX index_name ON table1 (a, b)";
        assertThat(interceptor.changeTable(sql)).isEqualTo("CREATE UNIQUE INDEX index_name ON table1_r (a, b)");
        sql = "ALTER TABLE table1_r ADD UNIQUE INDEX `a` (`a`)";
        assertThat(interceptor.changeTable(sql)).isEqualTo("ALTER TABLE table1_r_r ADD UNIQUE INDEX `a` (`a`)");
    }

    @Test
    void shouldReplaceTableNameInCreateFullTextIndex() {
        var sql = "CREATE FULLTEXT INDEX index_name ON table1 (a, b)";
        assertThat(interceptor.changeTable(sql)).isEqualTo("CREATE FULLTEXT INDEX index_name ON table1_r (a, b)");
        sql = "ALTER TABLE table1 ADD FULLTEXT INDEX `a`(`a`)";
        var expectedSql = switch (JSQLPARSER_VERSION) {
            case "4.9", "5.0" -> "ALTER TABLE table1_r ADD FULLTEXT INDEX `a`(`a`)";
            default -> "ALTER TABLE table1_r ADD FULLTEXT INDEX `a` (`a`)";
        };
        assertThat(interceptor.changeTable(sql)).isEqualTo(expectedSql);
    }

    @Test
    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    void shouldReplaceDynamicTableNameWithEdgeCases() {
        // 表名相互包含
        String origin = "SELECT * FROM t_user, t_user_role";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r, t_user_role_r");

        // 表名在末尾
        origin = "SELECT * FROM t_user";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r");

        // 表名前后有注释
        origin = "SELECT * FROM /**/t_user/* t_user */";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r");

        // 值中带有表名
        origin = "SELECT * FROM t_user WHERE name = 't_user'";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT * FROM t_user_r WHERE name = 't_user'");

        // 别名被声明要替换
        origin = "SELECT t_user.* FROM t_user_real t_user";
        assertThat(interceptor.changeTable(origin)).isEqualTo("SELECT t_user.* FROM t_user_real_r t_user");

        // 别名被声明要替换
        origin = "SELECT t.* FROM t_user_real t left join entity e on e.id = t.id";
        assertThat(interceptor.changeTable(origin))
                .isEqualTo("SELECT t.* FROM t_user_real_r t LEFT JOIN entity_r e ON e.id = t.id");
    }

    @Test
    void shouldReplaceTableNameInCreateTableSimple() {
        var sql = """
                CREATE TABLE `tag`  (
                  `id` int(11) NOT NULL AUTO_INCREMENT,
                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签名字',
                  `type` int(11) NULL DEFAULT NULL COMMENT '所属类别：0文章，1类别',
                  PRIMARY KEY (`id`) USING BTREE
                ) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '标签' ROW_FORMAT = Dynamic;
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                CREATE TABLE `tag_r` (`id` int (11) NOT NULL AUTO_INCREMENT, `name` varchar (50) CHARACTER
                SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签名字', `type` int (11) NULL DEFAULT
                NULL COMMENT '所属类别：0文章，1类别', PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT =
                10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '标签' ROW_FORMAT = Dynamic"""));
    }

    @Test
    void shouldReplaceTableNameInCreateTableIfNotExists() {
        var sql = """
                CREATE TABLE IF NOT EXISTS `user_info` (
                    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                    `username` VARCHAR(50) NOT NULL UNIQUE,
                    `email` VARCHAR(100) NOT NULL UNIQUE,
                    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                CREATE TABLE IF NOT EXISTS `user_info_r` (`id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                `username` VARCHAR (50) NOT NULL UNIQUE, `email` VARCHAR (100) NOT NULL UNIQUE, `created_at`
                TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4"""));
    }

    @Test
    void shouldReplaceTableNameInDropTableIfExists() {
        var sql = "DROP TABLE IF EXISTS `tag`";
        assertThat(interceptor.changeTable(sql)).isEqualTo("DROP TABLE IF EXISTS `tag_r`");
    }

    @Test
    void shouldHandleIssues6730() {
        // https://github.com/baomidou/mybatis-plus/issues/6730
        var sql = "select * from user order by top_bottom_sort desc, 0- EXTRACT(EPOCH FROM req_delivery_time) desc";
        assertThat(interceptor.changeTable(sql)).isEqualTo(
                "SELECT * FROM user_r ORDER BY top_bottom_sort DESC, 0 - EXTRACT(EPOCH FROM req_delivery_time) DESC");
    }

    @Test
    void shouldReplaceTableNameInSelectJoin() {
        var sql = "SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE e.id = ? OR e.name = ?";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("SELECT * FROM entity_r e JOIN entity1_r e1 ON e1.id = e.id WHERE e.id = ? OR e.name = ?");
    }

    @Test
    void shouldReplaceTableNameInSelectWithAs() {
        var sql = "with with_as_A as (select * from entity) select * from with_as_A";
        assertThat(interceptor.changeTable(sql))
                .isEqualTo("WITH with_as_A AS (SELECT * FROM entity_r) SELECT * FROM with_as_A_r");
    }

    @Test
    void shouldHandleDuplicateKeyUpdate() {
        var sql = "INSERT INTO entity (name,age) VALUES ('秋秋',18),('秋秋','22') ON DUPLICATE KEY UPDATE age=18";
        assertThat(interceptor.changeTable(sql)).isEqualTo(
                "INSERT INTO entity_r (name, age) VALUES ('秋秋', 18), ('秋秋', '22') ON DUPLICATE KEY UPDATE age = 18");
    }

    @Test
    void shouldReplaceTableNameInDelete() {
        var sql = "delete from entity where id = ?";
        assertThat(interceptor.changeTable(sql)).isEqualTo("DELETE FROM entity_r WHERE id = ?");
    }

    @Test
    void shouldReplaceTableNameInUpdateSimple() {
        var sql = "update entity set name = ? where id = ?";
        assertThat(interceptor.changeTable(sql)).isEqualTo("UPDATE entity_r SET name = ? WHERE id = ?");
    }

    @Test
    void shouldReplaceTableNameInPartition() {
        // 这种jsql解析不了
        var sql = """
                -- 查询2023年Q2分区数据
                SELECT
                    region,
                    SUM(gross_profit) AS 区域总利润,
                    AVG(order_value) AS 平均订单金额
                FROM
                    sales_data
                PARTITION BY
                    (TO_DATE(order_date, 'YYYY-MM-DD'))
                INTERVAL MONTHLY
                FOR PARTITION BETWEEN '2023-04-01' AND '2023-06-30'
                GROUP BY
                    region;
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo("""
                -- 查询2023年Q2分区数据
                SELECT
                    region,
                    SUM(gross_profit) AS 区域总利润,
                    AVG(order_value) AS 平均订单金额
                FROM
                    sales_data_r
                PARTITION BY
                    (TO_DATE(order_date, 'YYYY-MM-DD'))
                INTERVAL MONTHLY
                FOR PARTITION BETWEEN '2023-04-01' AND '2023-06-30'
                GROUP BY
                    region;
                """);
    }

    @Test
    void test2() {
        var sql = """
                SELECT
                    COUNT(*) AS 订单总数,
                    SUM(o.order_total) AS 总销售额,
                    SUM(CASE WHEN o.status = 'completed' THEN 1 ELSE 0 END) AS 完成订单数
                FROM
                    orders o
                JOIN
                    customers c ON o.customer_id = c.customer_id
                JOIN
                    order_items oi ON o.order_id = oi.order_id
                WHERE
                    c.region = 'North America'
                    AND o.order_date BETWEEN '2023-04-01' AND '2023-04-30'
                GROUP BY
                    o.customer_id
                HAVING
                    COUNT(*) > 10;
                ORDER BY
                    total_sales DESC;
                """;
        String expectedSql;
        if ("4.9".equals(JSQLPARSER_VERSION)) {
            // 回退解析保留原 SQL 的格式和第二条语句。
            expectedSql = sql.replace("orders o", "orders_r o").replace("customers c", "customers_r c")
                    .replace("order_items oi", "order_items_r oi");
        } else {
            expectedSql = singleLineSql("""
                    SELECT COUNT(*) AS 订单总数, SUM(o.order_total) AS 总销售额,
                    SUM(CASE WHEN o.status = 'completed' THEN 1 ELSE 0 END) AS 完成订单数
                    FROM orders_r o JOIN customers_r c ON o.customer_id = c.customer_id
                    JOIN order_items_r oi ON o.order_id = oi.order_id
                    WHERE c.region = 'North America' AND o.order_date BETWEEN '2023-04-01' AND '2023-04-30'
                    GROUP BY o.customer_id HAVING COUNT(*) > 10""");
        }
        assertThat(interceptor.changeTable(sql)).isEqualTo(expectedSql);
    }

    @Test
    void test3() {
        // 这种jsql解析不了的
        var sql = """
                DELIMITER $$
                DECLARE
                    cur CURSOR FOR
                        SELECT employee_id FROM employees WHERE salary < 50000;
                    emp_id INT;
                BEGIN
                    OPEN cur;
                    WHILE TRUE DO
                        FETCH cur INTO emp_id;
                        IF cur_rowcount = 0 THEN
                            LEAVE;
                        END IF;

                        UPDATE employees
                        SET salary = salary * 1.1
                        WHERE employee_id = emp_id;

                        INSERT INTO audit_log (employee_id, old_salary, new_salary)
                        VALUES (emp_id, salary_before_update, salary_after_update);
                    END WHILE;
                    CLOSE cur;
                END
                $$
                DELIMITER ;
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo("""
                DELIMITER $$
                DECLARE
                    cur CURSOR FOR
                        SELECT employee_id FROM employees_r WHERE salary < 50000;
                    emp_id INT;
                BEGIN
                    OPEN cur;
                    WHILE TRUE DO
                        FETCH cur INTO emp_id_r;
                        IF cur_rowcount = 0 THEN
                            LEAVE;
                        END IF;

                        UPDATE employees_r
                        SET salary = salary * 1.1
                        WHERE employee_id = emp_id;

                        INSERT INTO audit_log_r (employee_id, old_salary, new_salary)
                        VALUES (emp_id, salary_before_update, salary_after_update);
                    END WHILE;
                    CLOSE cur;
                END
                $$
                DELIMITER ;
                """);
    }

    @Test
    void test4() {
        var sql = """
                SELECT *
                FROM employees e
                JOIN departments d ON e.department_id = d.department_id
                WHERE
                    e.last_name LIKE CONCAT('%', :lastName, '%')
                    AND (
                        d.department_name IN (:departmentList)
                        OR :departmentList IS NULL
                    )
                    AND (
                        e.hire_date >= :startDate
                        OR :startDate IS NULL
                    )
                ORDER BY
                    e.employee_id
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                SELECT * FROM employees_r e JOIN departments_r d ON e.department_id = d.department_id
                WHERE e.last_name LIKE CONCAT('%', :lastName, '%') AND (d.department_name IN (:departmentList)
                OR :departmentList IS NULL) AND (e.hire_date >= :startDate OR :startDate IS NULL)
                ORDER BY e.employee_id"""));
    }

    @Test
    void test5() {
        var sql = """
                SELECT
                    product_id,
                    product_name,
                    stock_quantity,
                    (SELECT
                        SUM(ordered_qty)
                     FROM
                        purchase_orders po
                     WHERE
                        po.product_id = products.product_id
                        AND po.order_date >= CURDATE() - INTERVAL 3 MONTH) AS recent_order_volume
                FROM
                    products
                WHERE
                    stock_quantity < (
                        SELECT
                            AVG(recommended_stock)
                        FROM
                            product_settings
                        WHERE
                            product_id = products.product_id
                    )
                    AND recent_order_volume > 500
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                SELECT product_id, product_name, stock_quantity, (SELECT SUM(ordered_qty)
                FROM purchase_orders_r po WHERE po.product_id = products.product_id
                AND po.order_date >= CURDATE() - INTERVAL 3 MONTH) AS recent_order_volume FROM products_r
                WHERE stock_quantity < (SELECT AVG(recommended_stock) FROM product_settings_r
                WHERE product_id = products.product_id) AND recent_order_volume > 500"""));
    }

    @Test
    void test6() {
        var sql = """
                WITH user_activity AS (
                    SELECT
                        user_id,
                        event_type,
                        event_time,
                        ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY event_time) AS activity_seq
                    FROM
                        user_events
                )
                SELECT
                    user_id,
                    event_type,
                    event_time,
                    LAG(event_time) OVER (PARTITION BY user_id ORDER BY event_time) AS prev_event_time
                FROM
                    user_activity
                WHERE
                    activity_seq = 5
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                WITH user_activity AS (SELECT user_id, event_type, event_time, ROW_NUMBER() OVER (PARTITION BY
                user_id ORDER BY event_time) AS activity_seq FROM user_events_r)
                SELECT user_id, event_type, event_time, LAG(event_time) OVER (PARTITION BY user_id
                ORDER BY event_time) AS prev_event_time FROM user_activity_r WHERE activity_seq = 5"""));
    }

    @Test
    void test7() {
        var sql = "select * from db1.test where a = ?";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM db1.test_r WHERE a = ?");
        sql = "select * from db1.`test` where a = ?";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM db1.`test_r` WHERE a = ?");
        sql = "select * from db1.`test` where a = ?";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM db1.`test_r` WHERE a = ?");
    }

    @Test
    void test8() {
        // 这种jsql解析不了的
        var sql = "SELECT * FROM [HR].[dbo].[Employee_Salary_2023];";
        assertThat(interceptor.changeTable(sql)).isEqualTo("SELECT * FROM [HR].[dbo].[Employee_Salary_2023_r];");
    }

    @Test
    void test9() {
        // 这种jsql解析不了的
        var sql = """
                SELECT * FROM [SalesDB].[dbo].[Orders]
                JOIN [MarketingDB].[dbo].[Customers]
                ON Orders.CustomerID = Customers.CustomerID;
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo("""
                SELECT * FROM [SalesDB].[dbo].[Orders_r]
                JOIN [MarketingDB].[dbo].[Customers_r]
                ON Orders.CustomerID = Customers.CustomerID;
                """);
    }

    @Test
    void test10() {
        var sql = """
                SELECT * FROM ecommerce_orders
                PARTITION (p2022, p2023)
                WHERE order_date BETWEEN '2022-01-01' AND '2023-12-31';
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                SELECT * FROM ecommerce_orders_r PARTITION(p2022, p2023) WHERE order_date BETWEEN '2022-01-01'
                AND '2023-12-31'"""));
    }

    @Test
    void test11() {
        var sql = """
                SELECT order_id, customer_id,amount,
                      RANK() OVER (PARTITION BY customer_id ORDER BY amount DESC) AS rank
                    FROM orders;
                """;
        assertThat(interceptor.changeTable(sql)).isEqualTo(singleLineSql("""
                SELECT order_id, customer_id, amount, RANK() OVER (PARTITION BY customer_id
                ORDER BY amount DESC) AS rank FROM orders_r"""));
    }

    private static String singleLineSql(String sql) {
        return sql.replace('\n', ' ');
    }

    private static String jsqlParserVersion() {
        var resource = "/META-INF/maven/com.github.jsqlparser/jsqlparser/pom.properties";
        try (var input = Objects.requireNonNull(
                CCJSqlParserUtil.class.getResourceAsStream(resource), "Missing JSqlParser Maven metadata")) {
            var properties = new Properties();
            properties.load(input);
            return Objects.requireNonNull(properties.getProperty("version"), "Missing JSqlParser version");
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read JSqlParser Maven metadata", e);
        }
    }
}
