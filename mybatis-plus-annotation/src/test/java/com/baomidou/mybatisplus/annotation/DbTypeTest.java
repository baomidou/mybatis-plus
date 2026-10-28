package com.baomidou.mybatisplus.annotation;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DbType} 是对外配置用的公共枚举：配置文件与 jdbcUrl 识别都会落到它的名字和 db 字符串上，<br>
 * 所以这里用全量快照当漂移闸门，新增、删除、改名都要显式登记。<br>
 * 分组谓词（mysqlSameType / oracleSameType / postgresqlSameType）决定 DDL 生成器和方言走哪条分支，<br>
 * 一个类型同时进两个组会让分支无法确定，因此成员清单与互斥关系都钉住。
 *
 * @author taoyu
 */
@Tag("unit")
class DbTypeTest {

    private static final List<String> ALL_NAMES = List.of(
            "MYSQL", "MARIADB", "ORACLE", "POLARDB", "ORACLE_12C", "DB2", "H2", "HSQL", "SQLITE", "POSTGRE_SQL",
            "SQL_SERVER2005", "SQL_SERVER", "DM", "XU_GU", "KINGBASE_ES", "PHOENIX", "GAUSS", "GAUSS_DB", "CLICK_HOUSE",
            "GBASE", "GBASE_8S", "GBASEDBT", "GBASE_INFORMIX", "GBASE8S_PG", "GBASE_8C", "SINODB", "OSCAR", "SYBASE",
            "OCEAN_BASE", "FIREBIRD", "HIGH_GO", "CUBRID", "SUNDB", "SAP_HANA", "IMPALA", "VERTICA", "XCloud",
            "REDSHIFT", "OPENGAUSS", "TDENGINE", "INFORMIX", "UXDB", "LEALONE", "TRINO", "PRESTO", "DERBY", "VASTBASE",
            "GOLDENDB", "DUCKDB", "YASDB", "HIVE2", "OTHER");

    private static final Set<DbType> MYSQL_FAMILY = EnumSet.of(
            DbType.MYSQL, DbType.MARIADB, DbType.GBASE, DbType.OSCAR, DbType.XU_GU, DbType.CLICK_HOUSE,
            DbType.OCEAN_BASE, DbType.CUBRID, DbType.SUNDB, DbType.GOLDENDB, DbType.YASDB);

    private static final Set<DbType> ORACLE_FAMILY = EnumSet.of(DbType.ORACLE, DbType.POLARDB, DbType.DM, DbType.GAUSS);

    private static final Set<DbType> POSTGRESQL_FAMILY = EnumSet.of(
            DbType.POSTGRE_SQL, DbType.H2, DbType.LEALONE, DbType.SQLITE, DbType.HSQL, DbType.KINGBASE_ES,
            DbType.PHOENIX, DbType.SAP_HANA, DbType.IMPALA, DbType.HIGH_GO, DbType.VERTICA, DbType.REDSHIFT,
            DbType.OPENGAUSS, DbType.TDENGINE, DbType.UXDB, DbType.GBASE8S_PG, DbType.GBASE_8C, DbType.VASTBASE,
            DbType.DUCKDB);

    @Test
    void constantsStayExactlyTheDocumentedList() {
        assertThat(names()).as("新增或删除数据库类型属于改公共契约，必须在这里登记").isEqualTo(ALL_NAMES);
    }

    @Test
    void everyTypeResolvesBackFromItsOwnDbName() {
        for (DbType type : DbType.values()) {
            assertThat(DbType.getDbType(type.getDb())).as("db 字符串 %s 应认回 %s", type.getDb(), type).isSameAs(type);
        }
    }

    @Test
    void dbNamesAreUniqueRegardlessOfCase() {
        List<String> lowerCased = Arrays.stream(DbType.values()).map(DbType::getDb).map(String::toLowerCase).toList();
        assertThat(lowerCased).as("getDbType 忽略大小写，只靠大小写区分的两个 db 会让识别结果取决于声明顺序").doesNotHaveDuplicates();
    }

    @Test
    void unknownOrBlankDbNameFallsBackToOther() {
        assertThat(DbType.getDbType("not-a-database")).isSameAs(DbType.OTHER);
        assertThat(DbType.getDbType("")).isSameAs(DbType.OTHER);
        assertThat(DbType.getDbType(null)).as("配置缺项时传 null 不该抛异常").isSameAs(DbType.OTHER);
        assertThat(DbType.getDbType("GAUSS_DB")).as("枚举名不是 db 字符串，认不出来只能兜底").isSameAs(DbType.OTHER);
    }

    @Test
    void dbNameIsMatchedIgnoringCase() {
        assertThat(DbType.getDbType("Mysql")).isSameAs(DbType.MYSQL);
        assertThat(DbType.getDbType("Gauss")).isSameAs(DbType.GAUSS);
        assertThat(DbType.getDbType("gaussdb")).isSameAs(DbType.GAUSS_DB);
        assertThat(DbType.getDbType("GaussDB")).isSameAs(DbType.GAUSS_DB);
    }

    @Test
    void familyPredicatesMatchTheRegisteredMembership() {
        assertThat(family(DbType::mysqlSameType)).containsExactlyInAnyOrderElementsOf(MYSQL_FAMILY);
        assertThat(family(DbType::oracleSameType)).containsExactlyInAnyOrderElementsOf(ORACLE_FAMILY);
        assertThat(family(DbType::postgresqlSameType)).containsExactlyInAnyOrderElementsOf(POSTGRESQL_FAMILY);
    }

    /**
     * GBASE_8S 与 GBASE_INFORMIX 是同一个产品的两个枚举，db 字符串只靠 "-" 与 " " 区分： 写 "gbase-8s" 和 "gbase 8s" 会得到不同枚举，而两者的 desc 完全一样。
     */
    @Test
    void gbase8sHasTwoSpellingsThatResolveToDifferentConstants() {
        assertThat(DbType.getDbType("gbase-8s")).isSameAs(DbType.GBASE_8S);
        assertThat(DbType.getDbType("gbase 8s")).isSameAs(DbType.GBASE_INFORMIX);
        assertThat(DbType.GBASE_8S.getDesc()).isEqualTo(DbType.GBASE_INFORMIX.getDesc());
    }

    @Test
    void familiesAreMutuallyExclusive() {
        assertThat(overlap(DbType::mysqlSameType, DbType::oracleSameType)).as("同时属于 mysql 家族和 oracle 家族").isEmpty();
        assertThat(overlap(DbType::mysqlSameType, DbType::postgresqlSameType)).as("同时属于 mysql 家族和 postgresql 家族")
                .isEmpty();
        assertThat(overlap(DbType::oracleSameType, DbType::postgresqlSameType)).as("同时属于 oracle 家族和 postgresql 家族")
                .isEmpty();
    }

    private static List<String> names() {
        return Arrays.stream(DbType.values()).map(Enum::name).toList();
    }

    private static Set<DbType> family(Predicate<DbType> predicate) {
        return Arrays.stream(DbType.values()).filter(predicate)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(DbType.class)));
    }

    private static Set<DbType> overlap(Predicate<DbType> left, Predicate<DbType> right) {
        Set<DbType> both = family(left);
        both.retainAll(family(right));
        return both;
    }

}
