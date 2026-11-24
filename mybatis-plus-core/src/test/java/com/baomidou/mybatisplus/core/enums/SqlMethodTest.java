package com.baomidou.mybatisplus.core.enums;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 注入器 SQL 模板的整表闸门：新增常量、改方法名或改模板文本都会红
 */
@Tag("unit")
class SqlMethodTest {

    private static final Map<SqlMethod, String> METHOD_NAMES = Map.ofEntries(
            entry(SqlMethod.INSERT_ONE, "insert"), entry(SqlMethod.UPSERT_ONE, "upsert"),
            entry(SqlMethod.DELETE_BY_ID, "deleteById"), entry(SqlMethod.DELETE, "delete"),
            entry(SqlMethod.DELETE_BY_IDS, "deleteByIds"), entry(SqlMethod.LOGIC_DELETE_BY_ID, "deleteById"),
            entry(SqlMethod.LOGIC_DELETE_BY_MAP, "deleteByMap"), entry(SqlMethod.LOGIC_DELETE, "delete"),
            entry(SqlMethod.LOGIC_DELETE_BY_IDS, "deleteByIds"), entry(SqlMethod.UPDATE_BY_ID, "updateById"),
            entry(SqlMethod.UPDATE, "update"), entry(SqlMethod.LOGIC_UPDATE_BY_ID, "updateById"),
            entry(SqlMethod.SELECT_BY_ID, "selectById"), entry(SqlMethod.SELECT_BY_IDS, "selectByIds"),
            entry(SqlMethod.SELECT_COUNT, "selectCount"), entry(SqlMethod.SELECT_LIST, "selectList"),
            entry(SqlMethod.SELECT_MAPS, "selectMaps"), entry(SqlMethod.SELECT_OBJS, "selectObjs"));

    private static final Map<SqlMethod, String> RENDERED_SQL = Map.ofEntries(
            entry(SqlMethod.INSERT_ONE, "<script>\nINSERT INTO a b VALUES c\n</script>"),
            entry(SqlMethod.UPSERT_ONE, "<script>\nUPSERT INTO a b VALUES c\n</script>"),
            entry(SqlMethod.DELETE_BY_ID, "DELETE FROM a WHERE b=#{c}"),
            entry(SqlMethod.DELETE, "<script>\nDELETE FROM a b c\n</script>"),
            entry(SqlMethod.DELETE_BY_IDS, "<script>\nDELETE FROM a WHERE b IN (c)\n</script>"),
            entry(SqlMethod.LOGIC_DELETE_BY_ID, "<script>\nUPDATE a b WHERE c=#{d} e\n</script>"),
            entry(SqlMethod.LOGIC_DELETE_BY_MAP, "<script>\nUPDATE a b c\n</script>"),
            entry(SqlMethod.LOGIC_DELETE, "<script>\nUPDATE a b c d\n</script>"),
            entry(SqlMethod.LOGIC_DELETE_BY_IDS, "<script>\nUPDATE a b WHERE c IN (d) e\n</script>"),
            entry(SqlMethod.UPDATE_BY_ID, "<script>\nUPDATE a b WHERE c=#{d} e\n</script>"),
            entry(SqlMethod.UPDATE, "<script>\nUPDATE a b c d\n</script>"),
            entry(SqlMethod.LOGIC_UPDATE_BY_ID, "<script>\nUPDATE a b WHERE c=#{d} e\n</script>"),
            entry(SqlMethod.SELECT_BY_ID, "SELECT a FROM b WHERE c=#{d} e"),
            entry(SqlMethod.SELECT_BY_IDS, "<script>SELECT a FROM b WHERE c IN (d) e </script>"),
            entry(SqlMethod.SELECT_COUNT, "<script>a SELECT COUNT(b) AS total FROM c d e\n</script>"),
            entry(SqlMethod.SELECT_LIST, "<script>a SELECT b FROM c d e f\n</script>"),
            entry(SqlMethod.SELECT_MAPS, "<script>a SELECT b FROM c d e f\n</script>"),
            entry(SqlMethod.SELECT_OBJS, "<script>a SELECT b FROM c d e f\n</script>"));

    @Test
    void methodNamesStayAsRegistered() {
        Map<SqlMethod, String> actual = new EnumMap<>(SqlMethod.class);
        for (SqlMethod sqlMethod : SqlMethod.values()) {
            actual.put(sqlMethod, sqlMethod.getMethod());
        }
        assertThat(actual).as("新增 SqlMethod 常量必须登记到 METHOD_NAMES").isEqualTo(METHOD_NAMES);
    }

    @Test
    void sqlTemplatesRenderAsRegistered() {
        Map<SqlMethod, String> actual = new EnumMap<>(SqlMethod.class);
        for (SqlMethod sqlMethod : SqlMethod.values()) {
            actual.put(sqlMethod, render(sqlMethod));
        }
        assertThat(actual).as("改 SQL 模板必须同步 RENDERED_SQL，新增常量必须登记").isEqualTo(RENDERED_SQL);
    }

    private static String render(SqlMethod sqlMethod) {
        return switch (sqlMethod) {
            case INSERT_ONE, UPSERT_ONE, DELETE_BY_ID, DELETE, DELETE_BY_IDS, LOGIC_DELETE_BY_MAP ->
                sqlMethod.format("a", "b", "c");
            case LOGIC_DELETE, UPDATE -> sqlMethod.format("a", "b", "c", "d");
            case LOGIC_DELETE_BY_ID, LOGIC_DELETE_BY_IDS, UPDATE_BY_ID, LOGIC_UPDATE_BY_ID, SELECT_BY_ID, SELECT_BY_IDS,
                    SELECT_COUNT ->
                sqlMethod.format("a", "b", "c", "d", "e");
            case SELECT_LIST, SELECT_MAPS, SELECT_OBJS -> sqlMethod.format("a", "b", "c", "d", "e", "f");
        };
    }
}
