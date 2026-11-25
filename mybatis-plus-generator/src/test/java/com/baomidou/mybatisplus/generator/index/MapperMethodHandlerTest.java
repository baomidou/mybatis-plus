package com.baomidou.mybatisplus.generator.index;

import com.baomidou.mybatisplus.generator.IGenerateMapperMethodHandler;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.jdbc.DatabaseMetaDataWrapper;
import com.baomidou.mybatisplus.generator.model.MapperMethod;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
public class MapperMethodHandlerTest {

    @Test
    void shouldRecognizePrimaryKeyNames() {
        var method = new DefaultGenerateMapperMethodHandler();
        assertThat(method.isPrimaryKey("PRIMARY")).isTrue();
        assertThat(method.isPrimaryKey("primary")).isTrue();
        assertThat(method.isPrimaryKey("PRIMARY_KEY_1")).isTrue();
        assertThat(method.isPrimaryKey("primary_key_1")).isTrue();
        assertThat(method.isPrimaryKey("id_idx")).isFalse();
    }

    @ParameterizedTest(name = "lambda={0}, property={1}, deleted at {2}")
    @MethodSource("filteredIndexes")
    void joinsOnlyEmittedFields(boolean lambda, boolean property, int deletedPosition) {
        StrategyConfig.Builder strategy = new StrategyConfig.Builder();
        if (property) {
            strategy.entityBuilder().logicDeletePropertyName("deletedAt");
        } else {
            strategy.entityBuilder().logicDeleteColumnName("deleted_at");
        }
        List<String> columns = new ArrayList<>(List.of("name", "code"));
        columns.add(deletedPosition, "deleted_at");
        TableInfo table = table(strategy.build(), false, true, columns);

        List<MapperMethod> methods = handler(lambda, false).getMethodList(table);

        assertThat(methods).hasSize(3);
        assertThat(methods.get(0).getMethod())
                .contains("List<Contact> selectByNameAndCode(String name, String code)", "selectList(")
                .doesNotContain("deletedAt", "And(", ".);");
        assertThat(methods.get(1).getMethod())
                .contains("int updateByNameAndCode(Contact entity, String name, String code)");
        assertThat(methods.get(2).getMethod()).contains("int deleteByNameAndCode(String name, String code)");
        for (MapperMethod method : methods) {
            assertThat(method.getTableFieldList()).extracting(TableField::getPropertyName)
                    .containsExactly("name", "code");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void emitsNoMethodsWhenEveryIndexFieldIsLogicalDelete(boolean lambda) {
        StrategyConfig strategy = new StrategyConfig.Builder().entityBuilder().logicDeleteColumnName("deleted_at")
                .build();
        assertThat(handler(lambda, false).getMethodList(table(strategy, false, false, List.of("deleted_at"))))
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preservesCompositeMethodsWhenNoFieldIsFiltered(boolean lambda) {
        TableInfo table = table(new StrategyConfig.Builder().build(), false, true, List.of("name", "code"));
        List<MapperMethod> methods = handler(lambda, false).getMethodList(table);
        assertThat(methods).hasSize(3);
        assertThat(methods.get(0).getMethod()).contains(
                "List<Contact> selectByNameAndCode(String name, String code)", "eq(ObjectUtils.isNotNull(name)",
                ".eq(ObjectUtils.isNotNull(code)");
        assertThat(methods.get(0).getMethod()).doesNotContain("selectOne(");
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void stillSkipsCompositeIndexesByDefault(boolean lambda) {
        StrategyConfig strategy = new StrategyConfig.Builder().entityBuilder().logicDeleteColumnName("deleted_at")
                .build();
        TableInfo table = table(strategy, false, true, List.of("name", "deleted_at"));
        IGenerateMapperMethodHandler handler = lambda
                ? new DefaultGenerateMapperLambdaMethodHandler()
                : new DefaultGenerateMapperMethodHandler();
        assertThat(handler.getMethodList(table)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void retainsListReturnTypeAndNullChecksAfterFilteringUniqueCompositeIndex(boolean lambda) {
        StrategyConfig strategy = new StrategyConfig.Builder().entityBuilder().logicDeleteColumnName("deleted_at")
                .build();
        List<MapperMethod> methods = handler(lambda, false)
                .getMethodList(table(strategy, false, true, List.of("name", "deleted_at")));
        assertThat(methods).hasSize(3);
        assertThat(methods.get(0).getMethod())
                .contains("List<Contact> selectByName(String name)", "selectList(", "eq(ObjectUtils.isNotNull(name)")
                .doesNotContain("selectOne(");
    }

    @Test
    void usesColumnConstantsAfterFilteringLogicalDelete() {
        StrategyConfig strategy = new StrategyConfig.Builder().entityBuilder().logicDeleteColumnName("deleted_at")
                .enableColumnConstant().build();
        List<MapperMethod> methods = handler(false, false)
                .getMethodList(table(strategy, false, false, List.of("name", "deleted_at")));
        assertThat(methods.get(0).getMethod())
                .contains("selectByName(String name)", "eq(ObjectUtils.isNotNull(name), Contact.NAME, name)")
                .doesNotContain(".);");
    }

    @ParameterizedTest
    @MethodSource("kotlinIndexes")
    void retainsKotlinParameterNullabilityWhileJoiningEmittedFields(boolean lambda, int deletedPosition) {
        StrategyConfig strategy = new StrategyConfig.Builder().entityBuilder().logicDeletePropertyName("deletedAt")
                .build();
        List<String> columns = new ArrayList<>(List.of("name", "code"));
        columns.add(deletedPosition, "deleted_at");
        List<MapperMethod> methods = handler(lambda, false).getMethodList(table(strategy, true, true, columns));
        String nameType = deletedPosition == 0 ? "String?" : "String";
        assertThat(methods.get(0).getMethod())
                .contains(
                        "fun selectByNameAndCode(name: " + nameType + ", code: String?) :List<Contact>?", "selectList(")
                .doesNotContain("deletedAt", ".)");
        assertThat(methods.get(1).getMethod())
                .contains("fun updateByNameAndCode(entity: Contact, name: " + nameType + ", code: String?) :Int");
        assertThat(methods.get(2).getMethod())
                .contains("fun deleteByNameAndCode(name: " + nameType + ", code: String?) :Int");
    }

    private static IGenerateMapperMethodHandler handler(boolean lambda, boolean singleIndex) {
        return lambda
                ? new DefaultGenerateMapperLambdaMethodHandler(singleIndex)
                : new DefaultGenerateMapperMethodHandler(singleIndex);
    }

    private TableInfo table(StrategyConfig strategy, boolean kotlin, boolean unique, List<String> columns) {
        TableInfo table = mock(TableInfo.class);
        GlobalConfig.Builder global = new GlobalConfig.Builder();
        if (kotlin) {
            global.enableKotlin();
        }
        when(table.getGlobalConfig()).thenReturn(global.build());
        when(table.getStrategyConfig()).thenReturn(strategy);
        when(table.getEntityName()).thenReturn("Contact");
        Map<String, TableField> fields = new LinkedHashMap<>();
        List<DatabaseMetaDataWrapper.Index> indexes = new ArrayList<>();
        for (String column : columns) {
            String property = "deleted_at".equals(column) ? "deletedAt" : column;
            TableField field = mock(TableField.class);
            when(field.getName()).thenReturn(column);
            when(field.getColumnName()).thenReturn(column);
            when(field.getPropertyName()).thenReturn(property);
            when(field.getCapitalName()).thenReturn(Character.toUpperCase(property.charAt(0)) + property.substring(1));
            when(field.getColumnType()).thenReturn(DbColumnType.STRING);
            when(field.getPropertyType()).thenReturn("String");
            fields.put(column, field);
            DatabaseMetaDataWrapper.Index index = mock(DatabaseMetaDataWrapper.Index.class);
            when(index.getName()).thenReturn("contact_index");
            when(index.getColumnName()).thenReturn(column);
            when(index.isUnique()).thenReturn(unique);
            indexes.add(index);
        }
        when(table.getTableFieldMap()).thenReturn(fields);
        when(table.getIndexList()).thenReturn(indexes);
        return table;
    }

    static Stream<Arguments> filteredIndexes() {
        return Stream.of(false, true).flatMap(
                lambda -> Stream.of(false, true).flatMap(
                        property -> Stream.of(0, 1, 2).map(position -> Arguments.of(lambda, property, position))));
    }

    static Stream<Arguments> kotlinIndexes() {
        return Stream.of(false, true)
                .flatMap(lambda -> Stream.of(0, 1, 2).map(position -> Arguments.of(lambda, position)));
    }

}
