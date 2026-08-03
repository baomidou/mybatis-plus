package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.parser.JsqlParserGlobal;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author miemie
 * @since 2020-06-28
 */
class DataChangeRecorderInnerInterceptorTest {

    private final DataChangeRecorderInnerInterceptor interceptor = new DataChangeRecorderInnerInterceptor();

    @BeforeEach
    public void initProperties() {
        Properties properties = new Properties();
        properties.put("ignoredTableColumns", "table_name1.column1,column2; h2user.*; *.column1,COLUMN2");
        interceptor.setProperties(properties);
    }
    @Test
    void setProperties() throws Exception {
        final Object ignoreAllColumns = getFieldValue(interceptor, "ignoreAllColumns");
        Assertions.assertEquals(Set.of("COLUMN1", "COLUMN2"), ignoreAllColumns);
        final Object ignoredTableColumns = getFieldValue(interceptor, "ignoredTableColumns");
        Assertions.assertEquals(Map.of("H2USER", Set.of("*"), "TABLE_NAME1", Set.of("COLUMN1", "COLUMN2")), ignoredTableColumns);
    }

    private Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = DataChangeRecorderInnerInterceptor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return  field.get(obj);
    }

    @Test
    void processInsert() {
        final Insert insert = new Insert();
        insert.setTable(new Table("H2USER"));
        final DataChangeRecorderInnerInterceptor.OperationResult operationResult = interceptor.processInsert(insert, null);
        Assertions.assertEquals(operationResult.getTableName(), "H2USER:*");
        Assertions.assertFalse(operationResult.isRecordStatus());
        Assertions.assertNull(operationResult.getChangedData());
    }

    @Test
    void processInsertWithForeachAdditionalParameter() throws Exception {
        Configuration configuration = new Configuration();
        List<ParameterMapping> parameterMappings = List.of(
            new ParameterMapping.Builder(configuration, "__frch_item_0.id", Long.class).build(),
            new ParameterMapping.Builder(configuration, "__frch_item_0.name", String.class).build(),
            new ParameterMapping.Builder(configuration, "__frch_item_1.id", Long.class).build(),
            new ParameterMapping.Builder(configuration, "__frch_item_1.name", String.class).build()
        );
        BoundSql boundSql = new BoundSql(configuration, "insert into sys_user (id, name) values (?, ?), (?, ?)",
            parameterMappings, Map.of("list", List.of(
                Map.of("id", 1L, "name", "Alice"),
                Map.of("id", 2L, "name", "Bob")
            )));
        boundSql.setAdditionalParameter("__frch_item_0", Map.of("id", 1L, "name", "Alice"));
        boundSql.setAdditionalParameter("__frch_item_1", Map.of("id", 2L, "name", "Bob"));
        Insert insert = (Insert) JsqlParserGlobal.parse("insert into sys_user (id, name) values (?, ?), (?, ?)");

        DataChangeRecorderInnerInterceptor.OperationResult operationResult = interceptor.processInsert(insert, boundSql);

        Assertions.assertEquals("sys_user", operationResult.getTableName());
        Assertions.assertTrue(operationResult.isRecordStatus());
        Assertions.assertTrue(operationResult.getChangedData().contains("\"ID\":\"null->1\""));
        Assertions.assertTrue(operationResult.getChangedData().contains("\"NAME\":\"null->Alice\""));
    }

    @Test
    void processUpdate() {
        final Update update = new Update();
        update.setTable(new Table("H2USER"));
        final DataChangeRecorderInnerInterceptor.OperationResult operationResult = interceptor.processUpdate(update, null, null, null);
        Assertions.assertEquals(operationResult.getTableName(), "H2USER:*");
        Assertions.assertFalse(operationResult.isRecordStatus());
        Assertions.assertNull(operationResult.getChangedData());
    }

    @Test
    void isDataChangedTest() {
        var columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        Assertions.assertFalse(columnChangeResult.isDataChanged(null));
        Assertions.assertTrue(columnChangeResult.isDataChanged(BigDecimal.ZERO));

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(new Object());
        Assertions.assertTrue(columnChangeResult.isDataChanged(null));
        Assertions.assertTrue(columnChangeResult.isDataChanged(BigDecimal.ZERO));

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(new BigDecimal("0"));
        Assertions.assertFalse(columnChangeResult.isDataChanged(BigDecimal.ZERO));

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(BigDecimal.ZERO);
        Assertions.assertFalse(columnChangeResult.isDataChanged(BigDecimal.ZERO));

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(BigDecimal.ZERO);
        Assertions.assertTrue(columnChangeResult.isDataChanged("0"));
        Assertions.assertTrue(columnChangeResult.isDataChanged(0));

        Assertions.assertFalse(columnChangeResult.isDataChanged(new BigDecimal("0") {}));
        Assertions.assertTrue(columnChangeResult.isDataChanged(new BigDecimal("1") {}));
    }

}
