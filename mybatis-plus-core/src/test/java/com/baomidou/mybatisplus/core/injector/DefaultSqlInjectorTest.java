package com.baomidou.mybatisplus.core.injector;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DefaultSqlInjector} 是 {@code BaseMapper} 那套 CRUD 的唯一来源，此前仓库里没有一条直接针对它的用例：
 * 注入清单、语句命令类型、逻辑删除改写、重复注入的幂等性全靠评审兜底。本类把这几张契约钉成集合相等断言 —— 多注入或漏注入一个 {@code xxById}、把某个 SELECT 改成
 * UPDATE、逻辑删除分支漏改写，都会红。真库执行结果由 h2 那套集成测试负责。
 */
@Tag("unit")
class DefaultSqlInjectorTest {

    /**
     * 有主键时注入的 13 条语句，与 {@code BaseMapper} 里 {@code xxById} 系列一一对应。
     */
    private static final Set<String> INJECTED_WITH_PRIMARY_KEY = Set.of(
            "insert", "delete", "update", "selectCount", "selectMaps", "selectObjs", "selectList", "selectWithCursor",
            "deleteById", "deleteByIds", "updateById", "selectById", "selectByIds");

    /**
     * 无主键时只剩这 8 条：5 个按主键操作的方法不注入，运行期拿不到 {@code xxById}。
     */
    private static final Set<String> INJECTED_WITHOUT_PRIMARY_KEY = Set.of(
            "insert", "delete", "update", "selectCount", "selectMaps", "selectObjs", "selectList", "selectWithCursor");

    private static final Map<String, SqlCommandType> COMMAND_TYPES = Map.ofEntries(
            Map.entry("insert", SqlCommandType.INSERT), Map.entry("delete", SqlCommandType.DELETE),
            Map.entry("deleteById", SqlCommandType.DELETE), Map.entry("deleteByIds", SqlCommandType.DELETE),
            Map.entry("update", SqlCommandType.UPDATE), Map.entry("updateById", SqlCommandType.UPDATE),
            Map.entry("selectCount", SqlCommandType.SELECT), Map.entry("selectById", SqlCommandType.SELECT),
            Map.entry("selectByIds", SqlCommandType.SELECT), Map.entry("selectList", SqlCommandType.SELECT),
            Map.entry("selectMaps", SqlCommandType.SELECT), Map.entry("selectObjs", SqlCommandType.SELECT),
            Map.entry("selectWithCursor", SqlCommandType.SELECT));

    /**
     * 逻辑删除只换命令类型、不换注入清单：3 个 {@code delete*} 改为 UPDATE 打标记列，其余沿用 {@link #COMMAND_TYPES}。
     */
    private static final Map<String, SqlCommandType> LOGIC_DELETE_COMMAND_TYPES = logicDeleteTypes();

    @TableName("zz_default_injector_user")
    static class InjectorUser {

        @TableId(type = IdType.AUTO)
        private Long id;
        private String name;
    }

    @TableName("zz_default_injector_nopk")
    static class InjectorNoPkUser {

        private String name;
    }

    @TableName("zz_default_injector_logic_user")
    static class InjectorLogicUser {

        @TableId(type = IdType.AUTO)
        private Long id;
        private String name;
        @TableLogic
        @TableField("deleted")
        private Integer deleted;
    }

    @Mapper
    interface InjectorUserMapper extends BaseMapper<InjectorUser> {
    }

    @Mapper
    interface InjectorUserMapperCopy extends BaseMapper<InjectorUser> {
    }

    @Mapper
    interface InjectorNoPkUserMapper extends BaseMapper<InjectorNoPkUser> {
    }

    @Mapper
    interface InjectorLogicUserMapper extends BaseMapper<InjectorLogicUser> {
    }

    @Test
    void injectsThirteenStatementsWhenPrimaryKeyExists() {
        MybatisConfiguration configuration = inject(InjectorUserMapper.class);
        assertThat(injectedIds(configuration, InjectorUserMapper.class)).isEqualTo(INJECTED_WITH_PRIMARY_KEY);
    }

    @Test
    void skipsIdStatementsWhenPrimaryKeyMissing() {
        MybatisConfiguration configuration = inject(InjectorNoPkUserMapper.class);
        Set<String> injected = injectedIds(configuration, InjectorNoPkUserMapper.class);
        assertThat(injected).isEqualTo(INJECTED_WITHOUT_PRIMARY_KEY);
        assertThat(injected).doesNotContain("selectById", "selectByIds", "deleteById", "deleteByIds", "updateById");
    }

    @Test
    void everyInjectedStatementHasItsOwnCommandType() {
        MybatisConfiguration configuration = inject(InjectorUserMapper.class);
        assertThat(commandTypes(configuration, InjectorUserMapper.class)).isEqualTo(COMMAND_TYPES);
    }

    @Test
    void everyAbstractBaseMapperMethodGetsAStatement() {
        MybatisConfiguration configuration = inject(InjectorUserMapper.class);
        Set<String> abstractNames = abstractMethodNames(BaseMapper.class);
        assertThat(abstractNames).contains("insert", "selectById", "updateById", "selectWithCursor");
        assertThat(injectedIds(configuration, InjectorUserMapper.class)).containsAll(abstractNames);
    }

    @Test
    void logicDeleteRewritesDeleteStatementsToUpdate() {
        MybatisConfiguration configuration = inject(InjectorLogicUserMapper.class);
        Map<String, Object> param = new HashMap<>();
        param.put("ew", new QueryWrapper<>());
        param.put("coll", List.of(1L, 2L));
        param.put("id", 1L);
        assertThat(commandTypes(configuration, InjectorLogicUserMapper.class)).isEqualTo(LOGIC_DELETE_COMMAND_TYPES);
        assertThat(sql(configuration, InjectorLogicUserMapper.class, "deleteById", param))
                .isEqualTo("UPDATE zz_default_injector_logic_user SET deleted=1 WHERE id=? AND deleted=0");
        assertThat(sql(configuration, InjectorLogicUserMapper.class, "deleteByIds", param))
                .contains("SET deleted=1 WHERE id IN", "AND deleted=0");
        assertThat(sql(configuration, InjectorLogicUserMapper.class, "delete", param))
                .startsWith("UPDATE zz_default_injector_logic_user SET deleted=1").endsWith("WHERE deleted=0");
        assertThat(sql(configuration, InjectorLogicUserMapper.class, "selectById", param))
                .isEqualTo("SELECT id,name,deleted FROM zz_default_injector_logic_user WHERE id=? AND deleted=0");
    }

    @Test
    void deleteWithoutLogicDeleteStaysADeleteStatement() {
        MybatisConfiguration configuration = inject(InjectorUserMapper.class);
        assertThat(statement(configuration, InjectorUserMapper.class, "deleteById").getSqlCommandType())
                .isEqualTo(SqlCommandType.DELETE);
        assertThat(sql(configuration, InjectorUserMapper.class, "deleteById", 1L))
                .isEqualTo("DELETE FROM zz_default_injector_user WHERE id=?");
    }

    @Test
    void injectedIdsStayRegisteredWhenTheSameMapperIsInjectedTwice() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = assistant(configuration, InjectorUserMapper.class);
        new DefaultSqlInjector().inspectInject(assistant, InjectorUserMapper.class);
        assertThat(GlobalConfigUtils.getMapperRegistryCache(configuration))
                .contains(InjectorUserMapper.class.toString());
        new DefaultSqlInjector().inspectInject(assistant, InjectorUserMapper.class);
        assertThat(injectedIds(configuration, InjectorUserMapper.class)).isEqualTo(INJECTED_WITH_PRIMARY_KEY);
    }

    @Test
    void secondMapperOverTheSameEntityStillGetsItsOwnStatements() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        new DefaultSqlInjector()
                .inspectInject(assistant(configuration, InjectorUserMapper.class), InjectorUserMapper.class);
        new DefaultSqlInjector()
                .inspectInject(assistant(configuration, InjectorUserMapperCopy.class), InjectorUserMapperCopy.class);
        assertThat(injectedIds(configuration, InjectorUserMapperCopy.class)).isEqualTo(INJECTED_WITH_PRIMARY_KEY);
    }

    private static MybatisConfiguration inject(Class<?> mapperClass) {
        MybatisConfiguration configuration = new MybatisConfiguration();
        new DefaultSqlInjector().inspectInject(assistant(configuration, mapperClass), mapperClass);
        return configuration;
    }

    private static MapperBuilderAssistant assistant(MybatisConfiguration configuration, Class<?> mapperClass) {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        assistant.setCurrentNamespace(mapperClass.getName());
        TableInfoHelper.initMapperTableInfo(assistant, mapperClass);
        return assistant;
    }

    private static MappedStatement statement(MybatisConfiguration configuration, Class<?> mapperClass, String id) {
        return configuration.getMappedStatement(mapperClass.getName() + '.' + id, false);
    }

    private static String sql(MybatisConfiguration configuration, Class<?> mapperClass, String id, Object param) {
        return statement(configuration, mapperClass, id).getBoundSql(param).getSql().replace('\n', ' ');
    }

    private static Map<String, SqlCommandType> commandTypes(MybatisConfiguration configuration, Class<?> mapperClass) {
        Map<String, SqlCommandType> types = new TreeMap<>();
        injectedIds(configuration, mapperClass)
                .forEach(id -> types.put(id, statement(configuration, mapperClass, id).getSqlCommandType()));
        return types;
    }

    private static Map<String, SqlCommandType> logicDeleteTypes() {
        Map<String, SqlCommandType> types = new TreeMap<>(COMMAND_TYPES);
        types.put("delete", SqlCommandType.UPDATE);
        types.put("deleteById", SqlCommandType.UPDATE);
        types.put("deleteByIds", SqlCommandType.UPDATE);
        return types;
    }

    private static Set<String> injectedIds(MybatisConfiguration configuration, Class<?> mapperClass) {
        String prefix = mapperClass.getName() + '.';
        Set<String> ids = new TreeSet<>();
        for (Object mappedStatement : configuration.getMappedStatements()) {
            if (mappedStatement instanceof MappedStatement) {
                String id = ((MappedStatement) mappedStatement).getId();
                if (id.startsWith(prefix) && !id.contains("..")) {
                    ids.add(id.substring(prefix.length()));
                }
            }
        }
        return ids;
    }

    private static Set<String> abstractMethodNames(Class<?> mapperClass) {
        Set<String> names = new TreeSet<>();
        for (Method method : mapperClass.getDeclaredMethods()) {
            if (method.isSynthetic() || method.isDefault() || Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            names.add(method.getName());
        }
        return names;
    }
}
