package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class OptimisticLockerInnerInterceptorTest {

    private static final Pattern WRAPPER_PARAMETER = Pattern.compile("#\\{ew\\.paramNameValuePairs\\.([^}]+)}");

    private final OptimisticLockerInnerInterceptor interceptor = new OptimisticLockerInnerInterceptor(true);

    @BeforeAll
    static void setUp() throws ReflectiveOperationException {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Entity.class);
        entityClassCache().put("optimisticLockerWrapperTest", Entity.class);
    }

    @AfterAll
    static void tearDown() throws ReflectiveOperationException {
        entityClassCache().remove("optimisticLockerWrapperTest");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Class<?>> entityClassCache() throws ReflectiveOperationException {
        Field field = OptimisticLockerInnerInterceptor.class.getDeclaredField("ENTITY_CLASS_CACHE");
        field.setAccessible(true);
        return (Map<String, Class<?>>) field.get(null);
    }

    @Test
    void shouldNotUseAnotherColumnsEqualityAfterVersionInequality() {
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().ne("version", 0).eq("id", 7);
        assertSql(wrapper, false, "(version <> ? AND id = ?)", 0, 7);
        assertSql(wrapper, true, "(version <> ? AND id = ?)", 0, 7);
        assertThat(wrapper.getSqlSet()).isNull();
    }

    @Test
    void shouldNotUseAnotherColumnsEqualityAfterVersionRange() {
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().gt("version", 0).eq("age", 40);
        assertSql(wrapper, false, "(version > ? AND age = ?)", 0, 40);
        assertSql(wrapper, true, "(version > ? AND age = ?)", 0, 40);
        assertThat(wrapper.getSqlSet()).isNull();
    }

    @Test
    void shouldNotCarryVersionMatchIntoNestedCondition() {
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().ge("version", 0).and(nested -> nested.eq("id", 7));
        assertSql(wrapper, false, "(version >= ? AND (id = ?))", 0, 7);
        assertSql(wrapper, true, "(version >= ? AND (id = ?))", 0, 7);
        assertThat(wrapper.getSqlSet()).isNull();
    }

    @Test
    void shouldStillFindVersionEqualityAfterOtherPredicates() {
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().ne("version", 0).eq("age", 40).eq("version", 5);
        assertSql(wrapper, false, "(version <> ? AND age = ? AND version = ?)", 0, 40, 5);
        assertSql(wrapper, true, "(version <> ? AND age = ? AND version = ?)", 0, 40, 5);
        assertSql(wrapper.getSqlSet(), wrapper, "version = ?", 6);
    }

    @Test
    void shouldStillFindNestedVersionEquality() {
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().eq("id", 7).and(nested -> nested.eq("version", 5));
        assertSql(wrapper, false, "(id = ? AND (version = ?))", 7, 5);
        assertSql(wrapper, true, "(id = ? AND (version = ?))", 7, 5);
        assertSql(wrapper.getSqlSet(), wrapper, "version = ?", 6);
    }

    @Test
    void shouldIncrementVersionFromWrapperPredicate() {
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().eq("id", 7).eq("version", 4);
        assertSql(wrapper, false, "(id = ? AND version = ?)", 7, 4);
        assertSql(wrapper, true, "(id = ? AND version = ?)", 7, 4);
        assertSql(wrapper.getSqlSet(), wrapper, "version = ?", 5);
    }

    @Test
    void updateByIdSavesOldVersionInParams() {
        Entity entity = new Entity(5);
        Map<String, Object> params = new HashMap<>(Map.of(Constants.ENTITY, entity));
        interceptor.doOptimisticLocker(params, "updateById");
        assertThat(entity.version).isEqualTo(6);
        assertThat(params).containsEntry(Constants.MP_OPTLOCK_VERSION_ORIGINAL, 5).doesNotContainKey(Constants.WRAPPER);
    }

    @Test
    void updateWithNullWrapperCreatesOldVersionCondition() {
        Entity entity = new Entity(5);
        Map<String, Object> params = new HashMap<>(Map.of(Constants.ENTITY, entity));
        params.put(Constants.WRAPPER, null);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertThat(params.get(Constants.WRAPPER)).isInstanceOfSatisfying(UpdateWrapper.class, wrapper -> {
            assertSql(wrapper.getSqlSegment(), wrapper, "(version = ?)", 5);
            assertThat(wrapper.getSqlSet()).isNull();
        });
    }

    @Test
    void updateWithWrapperAppendsOldVersionCondition() {
        Entity entity = new Entity(5);
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().eq("id", 7);
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ?)", 7);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? AND version = ?)", 7, 5);
        assertThat(wrapper.getSqlSet()).isNull();
    }

    private void assertSql(UpdateWrapper<Entity> wrapper, boolean applyOptimisticLocker, String expectedSql,
            Object... expectedParams) {
        if (applyOptimisticLocker) {
            interceptor.doOptimisticLocker(Map.of(Constants.WRAPPER, wrapper), "optimisticLockerWrapperTest");
        }
        assertSql(wrapper.getSqlSegment(), wrapper, expectedSql, expectedParams);
    }

    private static void assertSql(String sql, UpdateWrapper<?> wrapper, String expectedSql, Object... expectedParams) {
        assertThat(sql).isNotNull();
        List<Object> params = new ArrayList<>();
        String sqlWithPlaceholders = WRAPPER_PARAMETER.matcher(sql).replaceAll(match -> {
            params.add(wrapper.getParamNameValuePairs().get(match.group(1)));
            return "?";
        });
        assertThat(sqlWithPlaceholders).isEqualTo(expectedSql);
        assertThat(params).containsExactly(expectedParams);
    }

    @TableName("zz_locker_predicate")
    static class Entity {

        @Version
        private Integer version;

        Entity(int version) {
            this.version = version;
        }
    }
}
