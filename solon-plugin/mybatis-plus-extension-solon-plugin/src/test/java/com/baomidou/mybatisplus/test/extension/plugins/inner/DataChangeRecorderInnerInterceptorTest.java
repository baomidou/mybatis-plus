package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
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
    void processUpdate() {
        final Update update = new Update();
        update.setTable(new Table("H2USER"));
        final DataChangeRecorderInnerInterceptor.OperationResult operationResult = interceptor.processUpdate(update, null, null, null);
        Assertions.assertEquals(operationResult.getTableName(), "H2USER:*");
        Assertions.assertFalse(operationResult.isRecordStatus());
        Assertions.assertNull(operationResult.getChangedData());
    }
}
