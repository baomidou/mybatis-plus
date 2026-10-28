package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@Tag("unit")
class ITableFieldMetaInfoCustomizerTest {

    @Test
    void shouldComposeCustomizersInOrder() {
        TableInfo tableInfo = Mockito.mock(TableInfo.class);
        TableField tableField = Mockito.mock(TableField.class);
        List<String> calls = new ArrayList<>();
        ITableFieldMetaInfoCustomizer first = (currentTableInfo, currentTableField) -> {
            assertThat(currentTableInfo).isSameAs(tableInfo);
            assertThat(currentTableField).isSameAs(tableField);
            calls.add("first");
        };
        ITableFieldMetaInfoCustomizer second = (currentTableInfo, currentTableField) -> {
            assertThat(currentTableInfo).isSameAs(tableInfo);
            assertThat(currentTableField).isSameAs(tableField);
            calls.add("second");
        };

        first.andThen(second).customize(tableInfo, tableField);

        assertThat(calls).containsExactly("first", "second");
    }

    @Test
    void shouldRejectNullCustomizerWhenComposing() {
        ITableFieldMetaInfoCustomizer customizer = (tableInfo, tableField) -> {
        };

        assertThatNullPointerException().isThrownBy(() -> customizer.andThen(null));
    }
}
