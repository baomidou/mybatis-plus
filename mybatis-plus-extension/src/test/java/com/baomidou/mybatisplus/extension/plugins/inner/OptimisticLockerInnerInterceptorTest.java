package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ?) AND version = ?)", 7, 5);
        assertThat(wrapper.getSqlSet()).isNull();
    }

    @Test
    void updateWithQueryWrapperAppliesOldVersionToEveryOrBranch() {
        Entity entity = new Entity(5);
        QueryWrapper<Entity> wrapper = Wrappers.<Entity>query().eq("id", 1).or().eq("id", 2);
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? OR id = ?)", 1, 2);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?) AND version = ?)", 1, 2, 5);
    }

    @Test
    void updateWithUpdateWrapperAppliesOldVersionToEveryOrBranchAndPreservesSet() {
        Entity entity = new Entity(5);
        UpdateWrapper<Entity> wrapper = Wrappers.<Entity>update().eq("id", 1).or().eq("id", 2).set("name", "after");
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? OR id = ?)", 1, 2);
        assertSql(wrapper.getSqlSet(), wrapper, "name=?", "after");
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?) AND version = ?)", 1, 2, 5);
        assertSql(wrapper.getSqlSet(), wrapper, "name=?", "after");
    }

    @Test
    void updateWithLambdaQueryWrapperAppliesOldVersionToEveryOrBranch() {
        Entity entity = new Entity(5);
        LambdaQueryWrapper<Entity> wrapper = Wrappers.<Entity>lambdaQuery().eq(Entity::getId, 1).or()
                .eq(Entity::getId, 2);
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? OR id = ?)", 1, 2);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?) AND version = ?)", 1, 2, 5);
    }

    @Test
    void updateWithLambdaUpdateWrapperAppliesOldVersionToEveryOrBranchAndPreservesSet() {
        Entity entity = new Entity(5);
        LambdaUpdateWrapper<Entity> wrapper = Wrappers.<Entity>lambdaUpdate().eq(Entity::getId, 1).or()
                .eq(Entity::getId, 2).set(Entity::getName, "after");
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? OR id = ?)", 1, 2);
        assertSql(wrapper.getSqlSet(), wrapper, "name=?", "after");
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?) AND version = ?)", 1, 2, 5);
        assertSql(wrapper.getSqlSet(), wrapper, "name=?", "after");
    }

    @Test
    void updateWithApplyAppliesOldVersionToEveryOrBranch() {
        Entity entity = new Entity(5);
        QueryWrapper<Entity> wrapper = Wrappers.<Entity>query().apply("id = {0} OR id = {1}", 1, 2);
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? OR id = ?)", 1, 2);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?) AND version = ?)", 1, 2, 5);
    }

    @Test
    void updateWithNestedWrapperPreservesGroupingBeforeOldVersionCondition() {
        Entity entity = new Entity(5);
        QueryWrapper<Entity> wrapper = Wrappers.<Entity>query().nested(nested -> nested.eq("id", 1).or().eq("id", 2));
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?))", 1, 2);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "(((id = ? OR id = ?)) AND version = ?)", 1, 2, 5);
    }

    @Test
    void updateWithEmptyWrapperAppendsOnlyOldVersionCondition() {
        Entity entity = new Entity(5);
        QueryWrapper<Entity> wrapper = Wrappers.query();
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "");
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "(version = ?)", 5);
    }

    @Test
    void updateWithRenderedWrapperPreservesCachedParameters() {
        Entity entity = new Entity(5);
        QueryWrapper<Entity> wrapper = Wrappers.<Entity>query().eq("id", 1).or().eq("id", 2);
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        String originalSql = wrapper.getSqlSegment();
        assertSql(originalSql, wrapper, "(id = ? OR id = ?)", 1, 2);
        Map<String, Object> originalParams = new HashMap<>(wrapper.getParamNameValuePairs());
        assertThat(wrapper.getSqlSegment()).isEqualTo(originalSql);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(wrapper.getSqlSegment(), wrapper, "((id = ? OR id = ?) AND version = ?)", 1, 2, 5);
        assertThat(wrapper.getParamNameValuePairs()).containsAllEntriesOf(originalParams)
                .hasSize(originalParams.size() + 1);
    }

    @Test
    void updateWithOrWrapperPreservesOrderByAndTailSql() {
        Entity entity = new Entity(5);
        QueryWrapper<Entity> wrapper = Wrappers.<Entity>query().eq("id", 1).or().eq("id", 2).orderByAsc("id")
                .last("/* retain tail */");
        Map<String, Object> params = Map.of(Constants.ENTITY, entity, Constants.WRAPPER, wrapper);
        assertSql(wrapper.getSqlSegment(), wrapper, "(id = ? OR id = ?) ORDER BY id ASC /* retain tail */", 1, 2);
        interceptor.doOptimisticLocker(params, "update");
        assertThat(entity.version).isEqualTo(6);
        assertSql(
                wrapper.getSqlSegment(), wrapper,
                "((id = ? OR id = ?) AND version = ?) ORDER BY id ASC /* retain tail */", 1, 2, 5);
    }

    private void assertSql(UpdateWrapper<Entity> wrapper, boolean applyOptimisticLocker, String expectedSql,
            Object... expectedParams) {
        if (applyOptimisticLocker) {
            interceptor.doOptimisticLocker(Map.of(Constants.WRAPPER, wrapper), "optimisticLockerWrapperTest");
        }
        assertSql(wrapper.getSqlSegment(), wrapper, expectedSql, expectedParams);
    }

    private static void assertSql(String sql, AbstractWrapper<?, ?, ?> wrapper, String expectedSql,
            Object... expectedParams) {
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

        private Integer id;

        private String name;

        @Version
        private Integer version;

        Entity(int version) {
            this.version = version;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
