/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;


import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TableNameParser Test
 *
 * @author Nadeem Mohammad
 * @since 2019-04-22
 */
public class TableNameParserTest {

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
        String sql = "SELECT name, age FROM table1 group by xyx";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1"));
    }

    @Test
    public void testSelectTwoTables() {
        String sql = "SELECT name, age FROM table1,table2";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testSelectThreeTables() {
        String sql = "SELECT name, age FROM table1,table2,table3 group by xyx";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2","table3"));
    }

    @Test
    public void testSelectOneTableWithAliase() {
        String sql = "SELECT name, age FROM table1 t1 whatever group by xyx";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1"));
    }

    @Test
    public void testSelectTwoTablesWithAliase() {
        String sql = "SELECT name, age FROM table1 t1,table2 t2 whatever group by xyx";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testSelectTwoTablesWithAliaseAndNoCondition() {
        String sql = "select xx from table1 a,table2 b";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testSelectThreeTablesWithAliase() {
        String sql = "SELECT name, age FROM table1 t1,table2 t2, table3 t3 whatever group by xyx";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2", "table3"));
    }


    @Test
    public void testSelectWithSubQuery() {
        assertThat(new TableNameParser(SQL_SELECT_SUB_QUERY).tables()).isEqualTo(asSet("strategy", "doc_sect_ver"));
    }

    @Test
    public void testSelectWithOneJoin() {
        String sql = "SELECT coluname(s) FROM table1 join table2 ON table1.coluname=table2.coluname";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testSelectOneJoinWithAliase() {
        String sql = "SELECT coluname(s) FROM table1 t1 join table2 t2 ON t1.coluname=t2.coluname";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testSelectOneLeftJoin() {
        String sql = "SELECT coluname(s) FROM table1 left outer join table2 ON table1.coluname=table2.coluname";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }


    @Test
    public void testShouldIgnoreDual() {
        String sql = "select * from dual";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet());
    }


    @Test
    public void testSelectTwoJoinWithAliase() {
        assertThat(new TableNameParser(SQL_SELECT_THREE_JOIN_WITH_ALIASE).tables()).isEqualTo(asSet("colleges", "students", "results"));
    }


    @Test
    public void testInsertWithValues() {
        String sql = "INSERT INTO table_name VALUES (value1,value2,value3,...)";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table_name"));
    }

    @Test
    public void testInsertComplex() {
        assertThat(new TableNameParser(SQL_COMPLEX_ONE).tables()).isEqualTo(asSet("static_product", "DR_BD_PRODUCT", "DR_BUNDLE", "map_edge_ver", "ITEM", "DR_BUNDLE_DISCOUNT"));
    }

    @Test
    public void testInsertWithSelect() {
        String sql = "INSERT INTO Customers (CustomerName, Country) SELECT SupplierName, Country FROM Suppliers;";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("Customers", "Suppliers"));
    }

    @Test
    public void testDelete() {
        String sql = "DELETE FROM validation_task WHERE task_name = 'ValidateSoldToCustId' AND conf_id IN (SELECT conf_id FROM validation_conf WHERE conf_name IN ('SaleValidation'))";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("validation_task", "validation_conf"));
    }

    @Test
    public void testOracleSpecialDelete() {
        String sql = "delete table1 where column_name=xyz";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1"));
    }

    @Test
    public void testAlter() {
        String sql = "ALTER TABLE Persons ADD UNIQUE (P_Id)";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("Persons"));
    }

    @Test
    public void testAlter2() {
        String sql = "ALTER TABLE table_name MODIFY coluname datatype";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table_name"));
    }

    @Test
    public void testDrop() {
        String sql = "DROP table tname;\n\r";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("tname"));
    }

    @Test
    public void testDropFunction() {
        String sql = "DROP FUNCTION functionName;";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet());
    }

    @Test
    public void testDropProcedure() {
        String sql = "drop procedure procedureName";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet());
    }

    @Test
    public void testDropView() {
        String sql = "DROP VIEW viewName";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet());
    }

    @Test
    public void testDropIndex() {
        String sql = "DROP INDEX indexName";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet());
    }

    @Test
    public void testUnionAll() {
        String sql = "SELECT coluname(s) FROM table1 UNION ALL SELECT coluname(s) FROM table2;";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testMerge() {
        String sql = "MERGE INTO employees e  USING hr_records h  ON (e.id = h.emp_id) WHEN MATCHED THEN  UPDATE SET e.address = h.address  WHEN NOT MATCHED THEN    INSERT (id, address) VALUES (h.emp_id, h.address);";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("employees", "hr_records"));
    }

    @Test
    public void testMergeUsingQuery() {
        String sql = "MERGE INTO employees e USING (SELECT * FROM hr_records WHERE start_date > ADD_MONTHS(SYSDATE, -1)) h  ON (e.id = h.emp_id)  WHEN MATCHED THEN  UPDATE SET e.address = h.address WHEN NOT MATCHED THEN INSERT (id, address) VALUES (h.emp_id, h.address)";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("employees", "hr_records"));
    }

    @Test
    public void testMergeComplexQuery() {
        assertThat(new TableNameParser(SQL_MERGE_COMPLEX).tables()).isEqualTo(asSet("non_strategy_comps","cf_procedure", "struct_doc_Sect_ver", "cf_period", "cf_component", "cf_strategy", "tier_basis_ver", "non_strategy_details", "cf_procedure", "non_strat_periods"));
    }

    @Test
    public void testMergeComplexQuery2() {
        assertThat(new TableNameParser(SQL_MERGE_COMPLEX_TWO).tables()).isEqualTo(asSet("cf_procedure_ver", "cf_procedure"));
    }

    @Test
    public void testCreateTable() {
        String sql = "CREATE TABLE Persons(PersonID int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255));";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("Persons"));
    }

    @Test
    public void testCreateGlobalTable() {
        String sql = "CREATE GLOBAL TEMPORARY TABLE excl_cust (gen_name VARCHAR2(100),run_date TIMESTAMP(3), item_root_uuid  VARCHAR2(22), owner_member_id  NUMBER(20)) ON COMMIT DELETE ROWS";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("excl_cust"));
    }

    @Test
    public void testCreateIndex() {
        String sql = "CREATE INDEX temp_name_idx ON table1(name) NOLOGGING PARALLEL (DEGREE 8);";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1"));
    }

    @Test
    public void testCreateView() {
        String sql = "CREATE VIEW dept AS SELECT * FROM dept;";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("dept"));
    }

    @Test
    public void testCreateView2() {
        String sql = "CREATE VIEW division1_staff AS SELECT ename, empno, job, dname FROM emp, dept WHERE emp.deptno IN (10, 30) AND emp.deptno = dept.deptno;";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("dept","emp"));
    }

    @Test
    public void testCreateType() {
        String sql = "CREATE OR REPLACE TYPE TYPE_NAME IS TABLE OF VARCHAR2(100)";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet());
    }

    @Test
    public void testUpdateTable() {
        String sql = "UPDATE tableName SET column1 = expression1, column2 = expression2";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("tableName"));
    }

    @Test
    public void testUpdateTableSubQuery() {
        String sql = "UPDATE table1 SET table1.value = (SELECT table2.CODE FROM table2 WHERE table1.value = table2.DESC) WHERE table1.UPDATETYPE='blah' AND EXISTS (SELECT table2.CODE  FROM table2    WHERE table1.value = table2.DESC);";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testUpdateTableSubQuery2() {
        String sql = "UPDATE (SELECT table1.value as OLD, table2.CODE as NEW FROM table1 INNER JOIN table2 ON table1.value = table2.DESC  WHERE table1.UPDATETYPE='blah' ) t SET t.OLD = t.NEW";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("table1", "table2"));
    }

    @Test
    public void testUpdateTableSubQueryWithOracleHint() {
        String sql = "update /*+ PARALLEL OPT_PARAM('parallel_min_percent','0') */ eligible ec set ec.END_DATE = ec.END_DATE + INTERVAL '0 0:0:0.999' DAY TO SECOND";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("eligible"));
    }

    @Test
    public void testTruncateTable() {
        String sql = "truncate table eligible_item";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("eligible_item"));
    }

    @Test
    public void testSqlWithComment() {
        String sql = "select * from foo -- this is a comment";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo"));
    }

    @Test
    public void testSqlWithCommentContainingKeyword() {
        String sql = "select * from foo -- what happens if I say update in a comment";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo"));
    }

    @Test
    public void testSqlWithCommentEndingWithKeyword() {
        String sql = "select * from foo -- what happens if I end a comment with an update";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo"));
    }

    @Test
    public void testSqlWithCommentInTheMiddle() {
        String sql = "select * -- I like stars \n from foo";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo"));
    }

    @Test
    public void testSqlWithCommentInTheMiddleAndEnd() {
        String sql = "select * -- I like stars \n from foo -- comment ending with update";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo"));
    }

    @Test
    public void testSqlWithMultipleCommentsInTheMiddle() {
        String sql = "select * -- I like stars \n from foo f -- I like foo \n join bar b -- I also like bar \n on f.id = b.id";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo","bar"));
    }

    @Test
    public void testSqlWithMultipleCommentsAndNewlines() {
        String sql = "select * -- I like stars \n from foo f -- I like foo \n\n join bar b -- I also like bar \n on f.id = b.id";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo","bar"));
    }

    @Test
    public void testSqlWithMultipleCommentsInTheMiddleAndEnd() {
        String sql = "select * -- I like stars \n from foo f -- I like foo \n join bar b -- I also like bar \n on f.id = b.id -- comment ending with update";
        assertThat(new TableNameParser(sql).tables()).isEqualTo(asSet("foo","bar"));
    }

    private static Collection<String> asSet(String... a) {
        Set<String> result = new HashSet<String>();
        for (String item : a) {
            result.add(item);
        }
        return result;
    }

}
