package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameJsqlParserInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author nieqiurong
 */
class DynamicTableNameJsqlParserInnerInterceptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicTableNameJsqlParserInnerInterceptor.class);

    private static final DynamicTableNameJsqlParserInnerInterceptor interceptor;

    static {
        interceptor = new DynamicTableNameJsqlParserInnerInterceptor((sql, tableName) -> {
            LOGGER.info("process table : {}", tableName);
            if (tableName.endsWith("`") || tableName.endsWith("]")) {
                char first = tableName.charAt(0);
                char last = tableName.charAt(tableName.length()-1);
                return first + SqlParserUtils.removeWrapperSymbol(tableName) + "_r" + last;
            }
            return tableName + "_r";
        });
        interceptor.setShouldFallback(true);
    }


    private static final String SQL_SELECT_SUB_QUERY = "SELECT /*+ materialize*/ strategy_id"
        + "FROM"
        + " ( SELECT  strat.cf_strategy_id "
        + "   FROM strategy strt,"
        + "        doc_sect_ver prodGrp"
        + "  WHERE  strat.src_id               = prodGrp.struct_doc_sect_id"
        + "           AND strat.module_type   IN ('sdfdsf','assdf')"
        + ")";


    private static final String SQL_SELECT_THREE_JOIN_WITH_ALIASE = "select c.name, s.name, s.id, r.result"
        + " from colleges c "
        + " join students s"
        + "   on c.id = s.college_id"
        + " join results r"
        + "   on s.id = r.student_id"
        + "where c.id = 3"
        + "  and r.dt =  to_date('22-09-2005','dd-mm-yyyy')";

    private static final String SQL_COMPLEX_ONE = "INSERT INTO static_product"
        + "  ("
        + "   DISCOUNT_ID,"
        + "    CATEGORY_ID,"
        + "    PRODUCT_ID"
        + "   )"
        + "  ( SELECT DISTINCT ALLNDC11.BUNDLE_DISCOUNT_ID,"
        + "     ALLNDC11.PRODUCT_ID,"
        + "     ALLNDC11.NDC11"
        + "  FROM ITEM ITEM"
        + " INNER JOIN"
        + "   (SELECT NODE.SOURCE_ID NDC11,"
        + "    PR.PRODUCT_ID,"
        + "     BD1.BUNDLE_DISCOUNT_ID"
        + "    FROM DR_BUNDLE B,"
        + "      DR_BUNDLE_DISCOUNT BD1,"
        + "        DR_BD_PRODUCT PR,"
        + "       map_edge_ver node"
        + "     WHERE B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE"
        + "     AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE"
        + "      AND B.BUNDLE_ID             =BD1.BUNDLE_ID"
        + "    AND B.BUNDLE_STATUS         =3"
        + "    AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID"
        + "     AND BD1.IS_DYNAMIC_CATEGORY!= 1"
        + "     AND NODE.EDGE_TYPE          = 1"
        + "      START WITH"
        + "      ("
        + "        NODE.DEST_ID              = PR.PRODUCT_ID"
        + "      AND B.BUNDLE_ID             =BD1.BUNDLE_ID"
        + "      AND B.BUNDLE_STATUS         =3"
        + "      AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID"
        + "     AND BD1.IS_DYNAMIC_CATEGORY!= 1"
        + "      AND NODE.EDGE_TYPE          = 1"
        + "      AND B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE"
        + "      AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE"
        + "     )"
        + "       CONNECT BY ( PRIOR NODE.SOURCE_ID=NODE.DEST_ID"
        + "    AND PRIOR NODE.EDGE_TYPE           = 1"
        + "    AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE"
        + "   AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE"
        + "    AND prior bd1.bundle_discount_id= bd1.bundle_discount_id)"
        + "    ) ALLNDC11"
        + "  ON (ALLNDC11.NDC11 = ITEM.CAT_MAP_ID)"
        + "  UNION"
        + "   ( SELECT BD1.BUNDLE_DISCOUNT_ID,"
        + "      PR.PRODUCT_ID,"
        + "     ITEM.CAT_MAP_ID"
        + "    FROM DR_BUNDLE B,"
        + "      DR_BUNDLE_DISCOUNT BD1,"
        + "     DR_BD_PRODUCT PR,"
        + "    ITEM ITEM"
        + "    WHERE B.BUNDLE_ID           =BD1.BUNDLE_ID"
        + "   AND B.BUNDLE_STATUS         =3"
        + "   AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID"
        + "    AND BD1.IS_DYNAMIC_CATEGORY!= 1"
        + "   AND item.cat_map_id         =pr.product_id"
        + "    )";

    private static final String SQL_MERGE_COMPLEX = "MERGE INTO  cf_procedure proc USING"
        + " ("
        + " WITH NON_STRATEGY_DETAILS AS"
        + "   ("
        + "   SELECT /*+ materialize*/ cf_strategy_id"
        + "    FROM"
        + "     ( SELECT  strat.cf_strategy_id"
        + "        FROM cf_strategy strat,"
        + "             struct_doc_Sect_ver prodGrp"
        + "        WHERE  strat.src_id               = prodGrp.struct_doc_sect_id"
        + "                 AND strat.src_mgr_id     = prodGrp.mgr_id"
        + "                 AND strat.src_ver_num    = prodGrp.ver_num"
        + "                 AND strat.module_type   IN ('COMPL','PRCMSTR')"
        + "   )  ),"
        + "   NON_STRATEGY_COMPS AS"
        + "   ("
        + "   SELECT /*+ materialize*/ cf_component_id"
        + "   FROM"
        + "   ("
        + "     SELECT comp.cf_component_id AS cf_component_id"
        + "     FROM   cf_component comp,"
        + "            tier_basis_ver tb"
        + "     WHERE  comp.bucket_src_id   = tb.tier_basis_id"
        + "             AND comp.bucket_src_mgr_id  = tb.mgr_id"
        + "             AND comp.bucket_src_ver_num = tb.ver_num"
        + "             AND comp.module_type       IN ('COMPL','PRCMSTR')"
        + "   )"
        + "   ) ,"
        + " NON_STRAT_PERIODS AS ("
        + "   SELECT /*+ materialize*/ cf_period_id"
        + "   FROM"
        + "         cf_period per,"
        + "         struct_doc_sect_ver prodGrp"
        + "   WHERE  per.src_id            = prodGrp.struct_doc_sect_id"
        + "         AND per.src_mgr_id     = prodGrp.mgr_id"
        + "         AND per.src_ver_num    = prodGrp.ver_num"
        + "         AND per.module_type    IN ('COMPL','PRCMSTR')"
        + "         AND per.pmt_status NOT IN ('TERM','REV')"

        + "    SELECT DISTINCT cf_procedure_id"
        + "   FROM"
        + "     (SELECT /*+ LEADING(comp,proc)*/"
        + "           proc.cf_procedure_id AS cf_procedure_id"
        + "     FROM  non_strategy_comps comp,"
        + "           cf_procedure proc"
        + "     WHERE proc.variable_name          ='CALCULATION_LEVEL_RESULT'"
        + "           AND comp.cf_component_id    = proc.cf_component_id"
        + "    UNION ALL"
        + "     SELECT  /*+ LEADING(strat,proc)*/"
        + "           proc.cf_procedure_id AS cf_procedure_id"
        + "     FROM  cf_procedure proc,"
        + "           non_strategy_details strat"
        + "     WHERE proc.variable_name       ='CALCULATION_LEVEL_RESULT'"
        + "           AND strat.cf_strategy_id = proc.cf_strategy_id"
        + "     UNION ALL"
        + "     SELECT  /*+ LEADING(strat,proc)*/"
        + "          proc.cf_procedure_id AS cf_procedure_id"
        + "     FROM cf_procedure proc,"
        + "          non_strat_periods periods"
        + "     WHERE proc.variable_name       ='CALCULATION_LEVEL_RESULT'"
        + "           AND periods.CF_PERIOD_ID = proc.period_id"
        + "     )"
        + "      )TMP ON (proc.cf_procedure_id = tmp.cf_procedure_id)"
        + " WHEN MATCHED THEN"
        + "   UPDATE SET proc.variable_name = 'TierResultSSName';";

    private static final String SQL_MERGE_COMPLEX_TWO = " MERGE INTO cf_procedure_ver procVer USING"
        + "   (SELECT cf_procedure_id"
        + "    FROM cf_procedure proc"
        + "    WHERE proc.variable_name                  = 'TierResultSSName'"
        + "   ) proc_main ON (proc_main.cf_procedure_id = procVer.cf_procedure_id )"
        + " WHEN MATCHED THEN"
        + "   UPDATE SET procVer.variable_name = 'TierResultSSName'"
        + "   WHERE procVer.variable_name <> 'TierResultSSName';";

    @Test
    public void testSelectOneTable() {
        var sql = "SELECT name, age FROM table1 group by xyx";
        assertEquals("SELECT name, age FROM table1_r GROUP BY xyx", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectTwoTables() {
        var sql = "SELECT name, age FROM table1,table2";
        assertEquals("SELECT name, age FROM table1_r, table2_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectThreeTables() {
        var sql = "SELECT name, age FROM table1,table2,table3 group by xyx";
        assertEquals("SELECT name, age FROM table1_r, table2_r, table3_r GROUP BY xyx", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectOneTableWithAliase() {
        var sql = "SELECT name, age FROM table1 t1 whatever group by xyx";
        assertEquals("SELECT name, age FROM table1_r t1 whatever group by xyx", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectTwoTablesWithAliase() {
        var sql = "SELECT name, age FROM table1 t1,table2 t2 whatever group by xyx";
        assertEquals("SELECT name, age FROM table1_r t1,table2_r t2 whatever group by xyx", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectTwoTablesWithAliaseAndNoCondition() {
        var sql = "select xx from table1 a,table2 b";
        assertEquals("SELECT xx FROM table1_r a, table2_r b", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectThreeTablesWithAliase() {
        var sql = "SELECT name, age FROM table1 t1,table2 t2, table3 t3 whatever group by xyx";
        assertEquals("SELECT name, age FROM table1_r t1,table2_r t2, table3_r t3 whatever group by xyx", interceptor.changeTable(sql));
    }


    @Test
    public void testSelectWithSubQuery() {
        assertEquals("SELECT /*+ materialize */ strategy_idFROM(SELECT strat.cf_strategy_id FROM strategy_r strt, doc_sect_ver_r prodGrp WHERE strat.src_id = prodGrp.struct_doc_sect_id AND strat.module_type IN ('sdfdsf', 'assdf'))", interceptor.changeTable(SQL_SELECT_SUB_QUERY));
    }

    @Test
    public void testSelectWithOneJoin() {
        var sql = "SELECT coluname(s) FROM table1 join table2 ON table1.coluname=table2.coluname";
        assertEquals("SELECT coluname(s) FROM table1_r JOIN table2_r ON table1.coluname = table2.coluname", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectOneJoinWithAliase() {
        var sql = "SELECT coluname(s) FROM table1 t1 join table2 t2 ON t1.coluname=t2.coluname";
        assertEquals("SELECT coluname(s) FROM table1_r t1 JOIN table2_r t2 ON t1.coluname = t2.coluname", interceptor.changeTable(sql));
    }

    @Test
    public void testSelectOneLeftJoin() {
        var sql = "SELECT coluname(s) FROM table1 left outer join table2 ON table1.coluname=table2.coluname";
        assertEquals("SELECT coluname(s) FROM table1_r LEFT OUTER JOIN table2_r ON table1.coluname = table2.coluname", interceptor.changeTable(sql));
    }

    @Test
    public void testShouldIgnoreDual() {
        var sql = "select * from dual";
        assertEquals("SELECT * FROM dual_r", interceptor.changeTable(sql));
    }


    @Test
    public void testSelectTwoJoinWithAliase() {
        assertEquals("select c.name, s.name, s.id, r.result from colleges_r c  join students_r s   on c.id = s.college_id join results_r r   on s.id = r.student_idwhere c.id = 3  and r.dt =  to_date('22-09-2005','dd-mm-yyyy')", interceptor.changeTable(SQL_SELECT_THREE_JOIN_WITH_ALIASE));
    }

    @Test
    public void testInsertWithValues() {
        var sql = "INSERT INTO table_name VALUES (value1,value2,value3,...)";
        assertEquals("INSERT INTO table_name_r VALUES (value1,value2,value3,...)", interceptor.changeTable(sql));
    }

    @Test
    public void testInsertComplex() {
        assertEquals("INSERT INTO static_product_r  (   DISCOUNT_ID,    CATEGORY_ID,    PRODUCT_ID   )  ( SELECT DISTINCT ALLNDC11.BUNDLE_DISCOUNT_ID,     ALLNDC11.PRODUCT_ID,     ALLNDC11.NDC11  FROM ITEM_r ITEM INNER JOIN   (SELECT NODE.SOURCE_ID NDC11,    PR.PRODUCT_ID,     BD1.BUNDLE_DISCOUNT_ID    FROM DR_BUNDLE_r B,      DR_BUNDLE_DISCOUNT_r BD1,        DR_BD_PRODUCT_r PR,       map_edge_ver_r node     WHERE B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE     AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE      AND B.BUNDLE_ID             =BD1.BUNDLE_ID    AND B.BUNDLE_STATUS         =3    AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID     AND BD1.IS_DYNAMIC_CATEGORY!= 1     AND NODE.EDGE_TYPE          = 1      START WITH      (        NODE.DEST_ID              = PR.PRODUCT_ID      AND B.BUNDLE_ID             =BD1.BUNDLE_ID      AND B.BUNDLE_STATUS         =3      AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID     AND BD1.IS_DYNAMIC_CATEGORY!= 1      AND NODE.EDGE_TYPE          = 1      AND B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE      AND B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE     )       CONNECT BY ( PRIOR NODE.SOURCE_ID=NODE.DEST_ID    AND PRIOR NODE.EDGE_TYPE           = 1    AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.EFF_START_DATE AND NODE.EFF_END_DATE   AND PRIOR B.DATE_ACTIVATED BETWEEN NODE.VER_START_DATE AND NODE.VER_END_DATE    AND prior bd1.bundle_discount_id= bd1.bundle_discount_id)    ) ALLNDC11  ON (ALLNDC11.NDC11 = ITEM.CAT_MAP_ID)  UNION   ( SELECT BD1.BUNDLE_DISCOUNT_ID,      PR.PRODUCT_ID,     ITEM.CAT_MAP_ID    FROM DR_BUNDLE_r B,      DR_BUNDLE_DISCOUNT_r BD1,     DR_BD_PRODUCT_r PR,    ITEM_r ITEM    WHERE B.BUNDLE_ID           =BD1.BUNDLE_ID   AND B.BUNDLE_STATUS         =3   AND PR.BUNDLE_DISCOUNT_ID   =BD1.BUNDLE_DISCOUNT_ID    AND BD1.IS_DYNAMIC_CATEGORY!= 1   AND item.cat_map_id         =pr.product_id    )", interceptor.changeTable(SQL_COMPLEX_ONE));
    }

    @Test
    public void testInsertWithSelect() {
        var sql = "INSERT INTO Customers (CustomerName, Country) SELECT SupplierName, Country FROM Suppliers;";
        assertEquals("INSERT INTO Customers_r (CustomerName, Country) SELECT SupplierName, Country FROM Suppliers_r", interceptor.changeTable(sql));
    }

    @Test
    public void testDelete2() {
        var sql = "DELETE FROM validation_task WHERE task_name = 'ValidateSoldToCustId' AND conf_id IN (SELECT conf_id FROM validation_conf WHERE conf_name IN ('SaleValidation'))";
        assertEquals("DELETE FROM validation_task_r WHERE task_name = 'ValidateSoldToCustId' AND conf_id IN (SELECT conf_id FROM validation_conf_r WHERE conf_name IN ('SaleValidation'))", interceptor.changeTable(sql));
    }

    @Test
    public void testOracleSpecialDelete() {
        var sql = "delete table1 where column_name=xyz";
        assertEquals("DELETE table1_r WHERE column_name = xyz", interceptor.changeTable(sql));
    }

    @Test
    public void testAlter() {
        var sql = "ALTER TABLE Persons ADD UNIQUE (P_Id)";
        assertEquals("ALTER TABLE Persons_r ADD UNIQUE (P_Id)", interceptor.changeTable(sql));
    }

    @Test
    public void testAlter2() {
        var sql = "ALTER TABLE table_name MODIFY coluname datatype";
        assertEquals("ALTER TABLE table_name_r MODIFY coluname datatype", interceptor.changeTable(sql));
    }

    @Test
    public void testDrop() {
        var sql = "DROP table tname;\n\r";
        assertEquals("DROP table tname_r", interceptor.changeTable(sql));
    }

    @Test
    public void testDropFunction() {
        var sql = "DROP FUNCTION functionName;";
        assertEquals("DROP FUNCTION functionName", interceptor.changeTable(sql));
    }

    @Test
    public void testDropProcedure() {
        var sql = "drop procedure procedureName";
        assertEquals(sql, interceptor.changeTable(sql));
    }

    @Test
    public void testDropView() {
        var sql = "DROP view viewName";
        assertEquals(sql, interceptor.changeTable(sql));
    }

    @Test
    public void testDropIndex() {
        var sql = "DROP INDEX indexName";
        assertEquals(sql, interceptor.changeTable(sql));
    }

    @Test
    public void testUnionAll() {
        var sql = "SELECT coluname(s) FROM table1 UNION ALL SELECT coluname(s) FROM table2;";
        assertEquals("SELECT coluname(s) FROM table1_r UNION ALL SELECT coluname(s) FROM table2_r", interceptor.changeTable(sql));
    }

    @Test
    public void testMerge() {
        var sql = "MERGE INTO employees e  USING hr_records h  ON (e.id = h.emp_id) WHEN MATCHED THEN  UPDATE SET e.address = h.address  WHEN NOT MATCHED THEN    INSERT (id, address) VALUES (h.emp_id, h.address);";
        assertEquals("MERGE INTO employees_r e USING hr_records_r h ON (e.id = h.emp_id) WHEN MATCHED THEN UPDATE SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address) VALUES (h.emp_id, h.address)", interceptor.changeTable(sql));
    }

    @Test
    public void testMergeUsingQuery() {
        var sql = "MERGE INTO employees e USING (SELECT * FROM hr_records WHERE start_date > ADD_MONTHS(SYSDATE, -1)) h  ON (e.id = h.emp_id)  WHEN MATCHED THEN  UPDATE SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address) VALUES (h.emp_id, h.address)";
        assertEquals("MERGE INTO employees_r e USING (SELECT * FROM hr_records_r WHERE start_date > ADD_MONTHS(SYSDATE, -1)) h ON (e.id = h.emp_id) WHEN MATCHED THEN UPDATE SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address) VALUES (h.emp_id, h.address)", interceptor.changeTable(sql));
    }

    @Test
    public void testMergeComplexQuery() {
        assertEquals("MERGE INTO  cf_procedure_r proc USING ( WITH NON_STRATEGY_DETAILS AS   (   SELECT /*+ materialize*/ cf_strategy_id    FROM     ( SELECT  strat.cf_strategy_id        FROM cf_strategy_r strat,             struct_doc_Sect_ver_r prodGrp        WHERE  strat.src_id               = prodGrp.struct_doc_sect_id                 AND strat.src_mgr_id     = prodGrp.mgr_id                 AND strat.src_ver_num    = prodGrp.ver_num                 AND strat.module_type   IN ('COMPL','PRCMSTR')   )  ),   NON_STRATEGY_COMPS AS   (   SELECT /*+ materialize*/ cf_component_id   FROM   (     SELECT comp.cf_component_id AS cf_component_id     FROM   cf_component_r comp,            tier_basis_ver_r tb     WHERE  comp.bucket_src_id   = tb.tier_basis_id             AND comp.bucket_src_mgr_id  = tb.mgr_id             AND comp.bucket_src_ver_num = tb.ver_num             AND comp.module_type       IN ('COMPL','PRCMSTR')   )   ) , NON_STRAT_PERIODS AS (   SELECT /*+ materialize*/ cf_period_id   FROM         cf_period_r per,         struct_doc_sect_ver_r prodGrp   WHERE  per.src_id            = prodGrp.struct_doc_sect_id         AND per.src_mgr_id     = prodGrp.mgr_id         AND per.src_ver_num    = prodGrp.ver_num         AND per.module_type    IN ('COMPL','PRCMSTR')         AND per.pmt_status NOT IN ('TERM','REV')    SELECT DISTINCT cf_procedure_id   FROM     (SELECT /*+ LEADING(comp,proc)*/           proc.cf_procedure_id AS cf_procedure_id     FROM  non_strategy_comps_r comp,           cf_procedure_r proc     WHERE proc.variable_name          ='CALCULATION_LEVEL_RESULT'           AND comp.cf_component_id    = proc.cf_component_id    UNION ALL     SELECT  /*+ LEADING(strat,proc)*/           proc.cf_procedure_id AS cf_procedure_id     FROM  cf_procedure_r proc,           non_strategy_details_r strat     WHERE proc.variable_name       ='CALCULATION_LEVEL_RESULT'           AND strat.cf_strategy_id = proc.cf_strategy_id     UNION ALL     SELECT  /*+ LEADING(strat,proc)*/          proc.cf_procedure_id AS cf_procedure_id     FROM cf_procedure_r proc,          non_strat_periods_r periods     WHERE proc.variable_name       ='CALCULATION_LEVEL_RESULT'           AND periods.CF_PERIOD_ID = proc.period_id     )      )TMP ON (proc.cf_procedure_id = tmp.cf_procedure_id) WHEN MATCHED THEN   UPDATE SET proc.variable_name = 'TierResultSSName';", interceptor.changeTable(SQL_MERGE_COMPLEX));
    }

    @Test
    public void testMergeComplexQuery2() {
        assertEquals("MERGE INTO cf_procedure_ver_r procVer USING (SELECT cf_procedure_id FROM cf_procedure_r proc WHERE proc.variable_name = 'TierResultSSName') proc_main ON (proc_main.cf_procedure_id = procVer.cf_procedure_id) WHEN MATCHED THEN UPDATE SET procVer.variable_name = 'TierResultSSName' WHERE procVer.variable_name <> 'TierResultSSName'", interceptor.changeTable(SQL_MERGE_COMPLEX_TWO));
    }

    @Test
    public void testCreateTable2() {
        var sql = "CREATE TABLE Persons(PersonID int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255));";
        assertEquals("CREATE TABLE Persons_r (PersonID int, LastName varchar (255), FirstName varchar (255), Address varchar (255), City varchar (255))", interceptor.changeTable(sql));
    }

    @Test
    public void testCreateGlobalTable() {
        var sql = "CREATE GLOBAL TEMPORARY TABLE excl_cust (gen_name VARCHAR2(100),run_date TIMESTAMP(3), item_root_uuid  VARCHAR2(22), owner_member_id  NUMBER(20)) ON COMMIT DELETE ROWS";
        assertEquals("CREATE GLOBAL TEMPORARY TABLE excl_cust_r (gen_name VARCHAR2 (100), run_date TIMESTAMP (3), item_root_uuid VARCHAR2 (22), owner_member_id NUMBER (20)) ON COMMIT DELETE ROWS", interceptor.changeTable(sql));
    }

    @Test
    public void testCreateIndex() {
        var sql = "CREATE INDEX temp_name_idx ON table1(name) NOLOGGING PARALLEL (DEGREE 8);";
        assertEquals("CREATE INDEX temp_name_idx ON table1_r (name) NOLOGGING PARALLEL (DEGREE8)", interceptor.changeTable(sql));
    }

    @Test
    public void testCreateView() {
        var sql = "CREATE VIEW dept AS SELECT * FROM dept;";
        assertEquals("CREATE VIEW dept AS SELECT * FROM dept_r", interceptor.changeTable(sql));
    }

    @Test
    public void testCreateView2() {
        var sql = "CREATE VIEW division1_staff AS SELECT ename, empno, job, dname FROM emp, dept WHERE emp.deptno IN (10, 30) AND emp.deptno = dept.deptno;";
        assertEquals("CREATE VIEW division1_staff AS SELECT ename, empno, job, dname FROM emp_r, dept_r WHERE emp.deptno IN (10, 30) AND emp.deptno = dept.deptno", interceptor.changeTable(sql));
    }

    @Test
    public void testCreateType() {
        var sql = "CREATE OR REPLACE TYPE TYPE_NAME IS TABLE OF VARCHAR2(100)";
        assertEquals(sql, interceptor.changeTable(sql));
    }

    @Test
    public void testUpdateTable() {
        var sql = "UPDATE tableName SET column1 = expression1, column2 = expression2";
        assertEquals("UPDATE tableName_r SET column1 = expression1, column2 = expression2", interceptor.changeTable(sql));
    }

    @Test
    public void testUpdateTableSubQuery() {
        var sql = "UPDATE table1 SET table1.value = (SELECT table2.CODE FROM table2 WHERE table1.value = table2.DESC) WHERE table1.UPDATETYPE='blah' AND EXISTS (SELECT table2.CODE  FROM table2    WHERE table1.value = table2.DESC);";
        assertEquals("UPDATE table1_r SET table1.value = (SELECT table2.CODE FROM table2_r WHERE table1.value = table2.DESC) WHERE table1.UPDATETYPE = 'blah' AND EXISTS (SELECT table2.CODE FROM table2_r WHERE table1.value = table2.DESC)", interceptor.changeTable(sql));
    }

    @Test
    public void testUpdateTableSubQuery2() {
        var sql = "UPDATE (SELECT table1.value as OLD, table2.CODE as NEW FROM table1 INNER JOIN table2 ON table1.value = table2.DESC  WHERE table1.UPDATETYPE='blah' ) t SET t.OLD = t.NEW";
        assertEquals("UPDATE (SELECT table1.value as OLD, table2.CODE as NEW FROM table1_r INNER JOIN table2_r ON table1.value = table2.DESC  WHERE table1.UPDATETYPE='blah' ) t SET t.OLD = t.NEW", interceptor.changeTable(sql));
    }

    @Test
    public void testUpdateTableSubQueryWithOracleHint() {
        var sql = "update /*+ PARALLEL OPT_PARAM('parallel_min_percent','0') */ eligible ec set ec.END_DATE = ec.END_DATE + INTERVAL '0 0:0:0.999' DAY TO SECOND";
        assertEquals("update /*+ PARALLEL OPT_PARAM('parallel_min_percent','0') */ eligible_r ec set ec.END_DATE = ec.END_DATE + INTERVAL '0 0:0:0.999' DAY TO SECOND", interceptor.changeTable(sql));
    }

    @Test
    public void testTruncateTable() {
        var sql = "truncate table eligible_item";
        assertEquals("TRUNCATE TABLE eligible_item_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithComment() {
        var sql = "select * from foo -- this is a comment";
        assertEquals("SELECT * FROM foo_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithCommentContainingKeyword() {
        var sql = "select * from foo -- what happens if I say update in a comment";
        assertEquals("SELECT * FROM foo_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithCommentEndingWithKeyword() {
        var sql = "select * from foo -- what happens if I end a comment with an update";
        assertEquals("SELECT * FROM foo_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithCommentInTheMiddle() {
        var sql = "select * -- I like stars \n from foo";
        assertEquals("SELECT * FROM foo_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithCommentInTheMiddleAndEnd() {
        var sql = "select * -- I like stars \n from foo -- comment ending with update";
        assertEquals("SELECT * FROM foo_r", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithMultipleCommentsInTheMiddle() {
        var sql = "select * -- I like stars \n from foo f -- I like foo \n join bar b -- I also like bar \n on f.id = b.id";
        assertEquals("SELECT * FROM foo_r f JOIN bar_r b ON f.id = b.id", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithMultipleCommentsAndNewlines() {
        var sql = "select * -- I like stars \n from foo f -- I like foo \n\n join bar b -- I also like bar \n on f.id = b.id";
        assertEquals("SELECT * FROM foo_r f JOIN bar_r b ON f.id = b.id", interceptor.changeTable(sql));
    }

    @Test
    public void testSqlWithMultipleCommentsInTheMiddleAndEnd() {
        var sql = "select * -- I like stars \n from foo f -- I like foo \n join bar b -- I also like bar \n on f.id = b.id -- comment ending with update";
        assertEquals("SELECT * FROM foo_r f JOIN bar_r b ON f.id = b.id", interceptor.changeTable(sql));
    }

    @Test
    void testSelectForUpdate() {
        //TODO 暂时解决不能使用的问题,当碰到for update nowait这样的,后面的 nowait 会被当做成表但也不是很影响苗老板的动态表过滤.
        var sql = "select * from mp where id = 1 for update";
        assertEquals("SELECT * FROM mp_r WHERE id = 1 FOR UPDATE", interceptor.changeTable(sql));
    }

    @Test
    public void testOnDuplicateKeyUpdate () {
        var sql = "INSERT INTO cf_procedure (_id,password) VALUES ('1','password') ON DUPLICATE KEY UPDATE id = 'UpId', password = 'upPassword';";
        assertEquals("INSERT INTO cf_procedure_r (_id, password) VALUES ('1', 'password') ON DUPLICATE KEY UPDATE id = 'UpId', password = 'upPassword'", interceptor.changeTable(sql));
    }

    @Test
    public void testUpdateIgnore() {
        var sql = "update ignore student set name = 'abc' where id = 4";
        assertEquals("UPDATE IGNORE student_r SET name = 'abc' WHERE id = 4", interceptor.changeTable(sql));

        sql = "UPDATE IGNORE student set name = 'abc' where id = 4";
        assertEquals("UPDATE IGNORE student_r SET name = 'abc' WHERE id = 4", interceptor.changeTable(sql));
    }

    @Test
    public void testInsertIgnore() {
        var sql = "INSERT IGNORE INTO student (userid,username) VALUES (2,'swan'),(4,'bear') ;";
        assertEquals("INSERT IGNORE INTO student_r (userid, username) VALUES (2, 'swan'), (4, 'bear')", interceptor.changeTable(sql));
    }

    @Test
    void testCreateUniqueIndex() {
        var sql = "CREATE UNIQUE INDEX index_name ON table1 (a, b)";
        assertEquals("CREATE UNIQUE INDEX index_name ON table1_r (a, b)", interceptor.changeTable(sql));
        sql = "ALTER TABLE table1_r ADD UNIQUE INDEX `a` (`a`)";
        assertEquals("ALTER TABLE table1_r_r ADD UNIQUE INDEX `a` (`a`)", interceptor.changeTable(sql));
    }

    @Test
    void testCreateFullTextIndex(){
        var sql = "CREATE FULLTEXT INDEX index_name ON table1 (a, b)";
        assertEquals("CREATE FULLTEXT INDEX index_name ON table1_r (a, b)", interceptor.changeTable(sql));
        sql = "ALTER TABLE table1 ADD FULLTEXT INDEX `a`(`a`)";
        assertEquals("ALTER TABLE table1_r ADD FULLTEXT INDEX `a` (`a`)", interceptor.changeTable(sql));
    }


    @Test
    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    void test() {
        // 表名相互包含
        String origin = "SELECT * FROM t_user, t_user_role";
        assertEquals("SELECT * FROM t_user_r, t_user_role_r", interceptor.changeTable(origin));

        // 表名在末尾
        origin = "SELECT * FROM t_user";
        assertEquals("SELECT * FROM t_user_r", interceptor.changeTable(origin));

        // 表名前后有注释
        origin = "SELECT * FROM /**/t_user/* t_user */";
        assertEquals("SELECT * FROM t_user_r", interceptor.changeTable(origin));

        // 值中带有表名
        origin = "SELECT * FROM t_user WHERE name = 't_user'";
        assertEquals("SELECT * FROM t_user_r WHERE name = 't_user'", interceptor.changeTable(origin));

        // 别名被声明要替换
        origin = "SELECT t_user.* FROM t_user_real t_user";
        assertEquals("SELECT t_user.* FROM t_user_real_r t_user", interceptor.changeTable(origin));

        // 别名被声明要替换
        origin = "SELECT t.* FROM t_user_real t left join entity e on e.id = t.id";
        assertEquals("SELECT t.* FROM t_user_real_r t LEFT JOIN entity_r e ON e.id = t.id", interceptor.changeTable(origin));
    }

    @Test
    void testCreateTable() {
        var sql = """
            CREATE TABLE `tag`  (
              `id` int(11) NOT NULL AUTO_INCREMENT,
              `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签名字',
              `type` int(11) NULL DEFAULT NULL COMMENT '所属类别：0文章，1类别',
              PRIMARY KEY (`id`) USING BTREE
            ) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '标签' ROW_FORMAT = Dynamic;
            """;
        assertEquals("CREATE TABLE `tag_r` (`id` int (11) NOT NULL AUTO_INCREMENT, `name` varchar (50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签名字', `type` int (11) NULL DEFAULT NULL COMMENT '所属类别：0文章，1类别', PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '标签' ROW_FORMAT = Dynamic", interceptor.changeTable(sql));
    }

    @Test
    void testCreateTableIfNotExists() {
        var sql = """
            CREATE TABLE IF NOT EXISTS `user_info` (
                `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                `username` VARCHAR(50) NOT NULL UNIQUE,
                `email` VARCHAR(100) NOT NULL UNIQUE,
                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;
        assertEquals("CREATE TABLE IF NOT EXISTS `user_info_r` (`id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, `username` VARCHAR (50) NOT NULL UNIQUE, `email` VARCHAR (100) NOT NULL UNIQUE, `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4", interceptor.changeTable(sql));
    }

    @Test
    void testDropTableIfExists() {
        var sql = "DROP TABLE IF EXISTS `tag`";
        assertEquals("DROP TABLE IF EXISTS `tag_r`", interceptor.changeTable(sql));
    }

    @Test
    void testIssues6730() {
        // https://github.com/baomidou/mybatis-plus/issues/6730
        var sql = "select * from user order by top_bottom_sort desc, 0- EXTRACT(EPOCH FROM req_delivery_time) desc";
        assertEquals("SELECT * FROM user_r ORDER BY top_bottom_sort DESC, 0 - EXTRACT(EPOCH FROM req_delivery_time) DESC", interceptor.changeTable(sql));
    }

    @Test
    void testSelectJoin() {
        var sql = "SELECT * FROM entity e join entity1 e1 on e1.id = e.id WHERE e.id = ? OR e.name = ?";
        assertEquals("SELECT * FROM entity_r e JOIN entity1_r e1 ON e1.id = e.id WHERE e.id = ? OR e.name = ?", interceptor.changeTable(sql));
    }

    @Test
    void testSelectWithAs() {
        var sql = "with with_as_A as (select * from entity) select * from with_as_A";
        assertEquals("WITH with_as_A AS (SELECT * FROM entity_r) SELECT * FROM with_as_A_r", interceptor.changeTable(sql));
    }

    @Test
    void testDuplicateKeyUpdate() {
        var sql = "INSERT INTO entity (name,age) VALUES ('秋秋',18),('秋秋','22') ON DUPLICATE KEY UPDATE age=18";
        assertEquals("INSERT INTO entity_r (name, age) VALUES ('秋秋', 18), ('秋秋', '22') ON DUPLICATE KEY UPDATE age = 18", interceptor.changeTable(sql));
    }

    @Test
    void testDelete() {
        var sql = "delete from entity where id = ?";
        assertEquals("DELETE FROM entity_r WHERE id = ?", interceptor.changeTable(sql));
    }

    @Test
    void testUpdate() {
        var sql = "update entity set name = ? where id = ?";
        assertEquals("UPDATE entity_r SET name = ? WHERE id = ?", interceptor.changeTable(sql));
    }

    @Test
    void testPartition() {
        // 这种jsql解析不了
        var sql = """
            -- 查询2023年Q2分区数据
            SELECT\s
                region,
                SUM(gross_profit) AS 区域总利润,
                AVG(order_value) AS 平均订单金额
            FROM\s
                sales_data
            PARTITION BY\s
                (TO_DATE(order_date, 'YYYY-MM-DD'))
            INTERVAL MONTHLY
            FOR PARTITION BETWEEN '2023-04-01' AND '2023-06-30'
            GROUP BY\s
                region;
            """;
        assertEquals("-- 查询2023年Q2分区数据\n" +
            "SELECT \n" +
            "    region,\n" +
            "    SUM(gross_profit) AS 区域总利润,\n" +
            "    AVG(order_value) AS 平均订单金额\n" +
            "FROM \n" +
            "    sales_data_r\n" +
            "PARTITION BY \n" +
            "    (TO_DATE(order_date, 'YYYY-MM-DD'))\n" +
            "INTERVAL MONTHLY\n" +
            "FOR PARTITION BETWEEN '2023-04-01' AND '2023-06-30'\n" +
            "GROUP BY \n" +
            "    region;\n", interceptor.changeTable(sql));
    }

    @Test
    void test2() {
        var sql = """
            SELECT\s
                COUNT(*) AS 订单总数,
                SUM(o.order_total) AS 总销售额,
                SUM(CASE WHEN o.status = 'completed' THEN 1 ELSE 0 END) AS 完成订单数
            FROM\s
                orders o
            JOIN\s
                customers c ON o.customer_id = c.customer_id
            JOIN\s
                order_items oi ON o.order_id = oi.order_id
            WHERE\s
                c.region = 'North America'
                AND o.order_date BETWEEN '2023-04-01' AND '2023-04-30'
            GROUP BY\s
                o.customer_id
            HAVING\s
                COUNT(*) > 10;
            ORDER BY\s
                total_sales DESC;
            """;
        assertEquals("SELECT COUNT(*) AS 订单总数, SUM(o.order_total) AS 总销售额, SUM(CASE WHEN o.status = 'completed' THEN 1 ELSE 0 END) AS 完成订单数 FROM orders_r o JOIN customers_r c ON o.customer_id = c.customer_id JOIN order_items_r oi ON o.order_id = oi.order_id WHERE c.region = 'North America' AND o.order_date BETWEEN '2023-04-01' AND '2023-04-30' GROUP BY o.customer_id HAVING COUNT(*) > 10", interceptor.changeTable(sql));
    }

    @Test
    void test3() {
        // 这种jsql解析不了的
        var sql = """
            DELIMITER $$
            DECLARE\s
                cur CURSOR FOR\s
                    SELECT employee_id FROM employees WHERE salary < 50000;
                emp_id INT;
            BEGIN
                OPEN cur;
                WHILE TRUE DO
                    FETCH cur INTO emp_id;
                    IF cur_rowcount = 0 THEN
                        LEAVE;
                    END IF;
                   \s
                    UPDATE employees\s
                    SET salary = salary * 1.1\s
                    WHERE employee_id = emp_id;
                   \s
                    INSERT INTO audit_log (employee_id, old_salary, new_salary)
                    VALUES (emp_id, salary_before_update, salary_after_update);
                END WHILE;
                CLOSE cur;
            END
            $$
            DELIMITER ;
            """;
        assertEquals("DELIMITER $$\n" +
            "DECLARE \n" +
            "    cur CURSOR FOR \n" +
            "        SELECT employee_id FROM employees_r WHERE salary < 50000;\n" +
            "    emp_id INT;\n" +
            "BEGIN\n" +
            "    OPEN cur;\n" +
            "    WHILE TRUE DO\n" +
            "        FETCH cur INTO emp_id_r;\n" +
            "        IF cur_rowcount = 0 THEN\n" +
            "            LEAVE;\n" +
            "        END IF;\n" +
            "        \n" +
            "        UPDATE employees_r \n" +
            "        SET salary = salary * 1.1 \n" +
            "        WHERE employee_id = emp_id;\n" +
            "        \n" +
            "        INSERT INTO audit_log_r (employee_id, old_salary, new_salary)\n" +
            "        VALUES (emp_id, salary_before_update, salary_after_update);\n" +
            "    END WHILE;\n" +
            "    CLOSE cur;\n" +
            "END\n" +
            "$$\n" +
            "DELIMITER ;\n", interceptor.changeTable(sql));
    }

    @Test
    void test4() {
        var sql = """
            SELECT *
            FROM employees e
            JOIN departments d ON e.department_id = d.department_id
            WHERE\s
                e.last_name LIKE CONCAT('%', :lastName, '%')
                AND (
                    d.department_name IN (:departmentList)
                    OR :departmentList IS NULL
                )
                AND (
                    e.hire_date >= :startDate
                    OR :startDate IS NULL
                )
            ORDER BY\s
                e.employee_id
            """;
        assertEquals("SELECT * FROM employees_r e JOIN departments_r d ON e.department_id = d.department_id WHERE e.last_name LIKE CONCAT('%', :lastName, '%') AND (d.department_name IN (:departmentList) OR :departmentList IS NULL) AND (e.hire_date >= :startDate OR :startDate IS NULL) ORDER BY e.employee_id", interceptor.changeTable(sql));
    }

    @Test
    void test5() {
        var sql  = """
            SELECT\s
                product_id,
                product_name,
                stock_quantity,
                (SELECT\s
                    SUM(ordered_qty)\s
                 FROM\s
                    purchase_orders po\s
                 WHERE\s
                    po.product_id = products.product_id\s
                    AND po.order_date >= CURDATE() - INTERVAL 3 MONTH) AS recent_order_volume
            FROM\s
                products
            WHERE\s
                stock_quantity < (
                    SELECT\s
                        AVG(recommended_stock)\s
                    FROM\s
                        product_settings\s
                    WHERE\s
                        product_id = products.product_id
                )
                AND recent_order_volume > 500
            """;
        assertEquals("SELECT product_id, product_name, stock_quantity, (SELECT SUM(ordered_qty) FROM purchase_orders_r po WHERE po.product_id = products.product_id AND po.order_date >= CURDATE() - INTERVAL 3 MONTH) AS recent_order_volume FROM products_r WHERE stock_quantity < (SELECT AVG(recommended_stock) FROM product_settings_r WHERE product_id = products.product_id) AND recent_order_volume > 500", interceptor.changeTable(sql));
    }

    @Test
    void test6() {
        var sql = """
            WITH user_activity AS (
                SELECT\s
                    user_id,
                    event_type,
                    event_time,
                    ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY event_time) AS activity_seq
                FROM\s
                    user_events
            )
            SELECT\s
                user_id,
                event_type,
                event_time,
                LAG(event_time) OVER (PARTITION BY user_id ORDER BY event_time) AS prev_event_time
            FROM\s
                user_activity
            WHERE\s
                activity_seq = 5
            """;
        assertEquals("WITH user_activity AS (SELECT user_id, event_type, event_time, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY event_time) AS activity_seq FROM user_events_r) SELECT user_id, event_type, event_time, LAG(event_time) OVER (PARTITION BY user_id ORDER BY event_time) AS prev_event_time FROM user_activity_r WHERE activity_seq = 5", interceptor.changeTable(sql));
    }

    @Test
    void test7() {
        var sql = "select * from db1.test where a = ?";
        assertEquals("SELECT * FROM db1.test_r WHERE a = ?", interceptor.changeTable(sql));
        sql = "select * from db1.`test` where a = ?";
        assertEquals("SELECT * FROM db1.`test_r` WHERE a = ?", interceptor.changeTable(sql));
        sql = "select * from db1.`test` where a = ?";
        assertEquals("SELECT * FROM db1.`test_r` WHERE a = ?", interceptor.changeTable(sql));
    }

    @Test
    void test8() {
        // 这种jsql解析不了的
        var sql = "SELECT * FROM [HR].[dbo].[Employee_Salary_2023];";
        assertEquals("SELECT * FROM [HR].[dbo].[Employee_Salary_2023_r];", interceptor.changeTable(sql));
    }

    @Test
    void test9(){
        // 这种jsql解析不了的
        var sql = """
            SELECT * FROM [SalesDB].[dbo].[Orders]
            JOIN [MarketingDB].[dbo].[Customers]\s
            ON Orders.CustomerID = Customers.CustomerID;
            """;
        assertEquals("SELECT * FROM [SalesDB].[dbo].[Orders_r]\n" +
            "JOIN [MarketingDB].[dbo].[Customers_r] \n" +
            "ON Orders.CustomerID = Customers.CustomerID;\n", interceptor.changeTable(sql));
    }

    @Test
    void test10() {
        var sql = """
            SELECT * FROM ecommerce_orders\s
            PARTITION (p2022, p2023)
            WHERE order_date BETWEEN '2022-01-01' AND '2023-12-31';
            """;
        assertEquals("SELECT * FROM ecommerce_orders_r PARTITION(p2022, p2023) WHERE order_date BETWEEN '2022-01-01' AND '2023-12-31'", interceptor.changeTable(sql));
    }

    @Test
    void test11() {
        var sql = """
            SELECT order_id, customer_id,amount,
                  RANK() OVER (PARTITION BY customer_id ORDER BY amount DESC) AS rank
                FROM orders;
            """;
        assertEquals("SELECT order_id, customer_id, amount, RANK() OVER (PARTITION BY customer_id ORDER BY amount DESC) AS rank FROM orders_r", interceptor.changeTable(sql));
    }

}
