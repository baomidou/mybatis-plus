package com.baomidou.mybatisplus.test.extension.plugins.inner;

import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-28
 */
@Tag("unit")
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
        assertThat(ignoreAllColumns).isEqualTo(Set.of("COLUMN1", "COLUMN2"));
        final Object ignoredTableColumns = getFieldValue(interceptor, "ignoredTableColumns");
        assertThat(ignoredTableColumns)
                .isEqualTo(Map.of("H2USER", Set.of("*"), "TABLE_NAME1", Set.of("COLUMN1", "COLUMN2")));
    }

    private Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = DataChangeRecorderInnerInterceptor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    @Test
    void processInsert() {
        final Insert insert = new Insert();
        insert.setTable(new Table("H2USER"));
        final DataChangeRecorderInnerInterceptor.OperationResult operationResult = interceptor
                .processInsert(insert, null);
        assertThat("H2USER:*").isEqualTo(operationResult.getTableName());
        assertThat(operationResult.isRecordStatus()).isFalse();
        assertThat(operationResult.getChangedData()).isNull();
    }

    @Test
    void processUpdate() {
        final Update update = new Update();
        update.setTable(new Table("H2USER"));
        final DataChangeRecorderInnerInterceptor.OperationResult operationResult = interceptor
                .processUpdate(update, null, null, null);
        assertThat("H2USER:*").isEqualTo(operationResult.getTableName());
        assertThat(operationResult.isRecordStatus()).isFalse();
        assertThat(operationResult.getChangedData()).isNull();
    }

    @Test
    void isDataChangedTest() {
        var columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        assertThat(columnChangeResult.isDataChanged(null)).isFalse();
        assertThat(columnChangeResult.isDataChanged(BigDecimal.ZERO)).isTrue();

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(new Object());
        assertThat(columnChangeResult.isDataChanged(null)).isTrue();
        assertThat(columnChangeResult.isDataChanged(BigDecimal.ZERO)).isTrue();

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(new BigDecimal("0"));
        assertThat(columnChangeResult.isDataChanged(BigDecimal.ZERO)).isFalse();

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(BigDecimal.ZERO);
        assertThat(columnChangeResult.isDataChanged(BigDecimal.ZERO)).isFalse();

        columnChangeResult = new DataChangeRecorderInnerInterceptor.DataColumnChangeResult();
        columnChangeResult.setOriginalValue(BigDecimal.ZERO);
        assertThat(columnChangeResult.isDataChanged("0")).isTrue();
        assertThat(columnChangeResult.isDataChanged(0)).isTrue();

        assertThat(columnChangeResult.isDataChanged(new BigDecimal("0") {
        })).isFalse();
        assertThat(columnChangeResult.isDataChanged(new BigDecimal("1") {
        })).isTrue();
    }

}
