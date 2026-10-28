package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Jdbc 工具类测试
 *
 * @author hubin
 * @since 2020-09-15
 */
@Tag("unit")
public class JdbcUtilsTest {

    /**
     * 连接地址识别不出来的 DbType：ORACLE_12C 只用于方言选择，GBASEDBT 与 GBASE_INFORMIX 在 if 链里没有对应分支。
     * <p>
     * JdbcUtils 补上对应分支时，这里与 {@link #earlierBranchWinsOverLaterOne()} 必须同步调整。
     */
    private static final EnumSet<DbType> unreachableDbTypes = EnumSet
            .of(DbType.ORACLE_12C, DbType.GBASEDBT, DbType.GBASE_INFORMIX);

    @Test
    public void shouldMatchJdbcUrlPattern() {
        String regex = ":dm\\d*:";
        assertThat(JdbcUtils.regexFind(regex, ":dm:")).isTrue();
        assertThat(JdbcUtils.regexFind(regex, ":dm8:")).isTrue();
        assertThat(JdbcUtils.regexFind(regex, "123:dm6:abc")).isTrue();
        assertThat(JdbcUtils.regexFind(regex, ":dm7:abc")).isTrue();
        assertThat(JdbcUtils.regexFind(regex, "a12ds:dm71:")).isTrue();
        assertThat(JdbcUtils.regexFind(regex, "a12ds:dmc1:abc")).isFalse();
    }

    /**
     * 每个可达 DbType 都要有真实连接地址能识别出来，除 {@link #unreachableDbTypes} 之外的枚举值都必须在这里登记。
     * <p>
     * 新增数据库类型时由 {@link #jdbcUrlTableCoversEveryDbType()} 强制补行。
     */
    private static Stream<Arguments> jdbcUrlRouting() {
        return Stream.of(
                Arguments.of("jdbc:mysql://127.0.0.1:3306/test", DbType.MYSQL),
                Arguments.of("jdbc:cobar://127.0.0.1:8066/test", DbType.MYSQL),
                Arguments.of("jdbc:mariadb://127.0.0.1:3306/test", DbType.MARIADB),
                Arguments.of("jdbc:oracle:thin:@127.0.0.1:1521:ORCL", DbType.ORACLE),
                Arguments.of("jdbc:polardb://127.0.0.1:1999/test", DbType.POLARDB),
                Arguments.of("jdbc:polardb2://127.0.0.1:1999/test", DbType.POLARDB),
                Arguments.of("jdbc:sqlserver://127.0.0.1:1433;databaseName=test", DbType.SQL_SERVER),
                Arguments.of("jdbc:microsoft:sqlserver://127.0.0.1:1433", DbType.SQL_SERVER),
                Arguments.of("jdbc:jtds:sqlserver://127.0.0.1:1433/test", DbType.SQL_SERVER2005),
                Arguments.of("jdbc:postgresql://127.0.0.1:5432/test", DbType.POSTGRE_SQL),
                Arguments.of("jdbc:hsqldb:mem:test", DbType.HSQL),
                Arguments.of("jdbc:db2://127.0.0.1:50000/test", DbType.DB2),
                Arguments.of("jdbc:sqlite:test.db", DbType.SQLITE),
                Arguments.of("jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1", DbType.H2),
                Arguments.of("jdbc:lealone:tcp://127.0.0.1:8088/test", DbType.LEALONE),
                Arguments.of("jdbc:dm://127.0.0.1:5236/test", DbType.DM),
                Arguments.of("jdbc:dm8://127.0.0.1:5236/test", DbType.DM),
                Arguments.of("jdbc:xugu://127.0.0.1:5858/test", DbType.XU_GU),
                Arguments.of("jdbc:kingbase8://127.0.0.1:54321/test", DbType.KINGBASE_ES),
                Arguments.of("jdbc:phoenix:127.0.0.1:2181:/baomidou", DbType.PHOENIX),
                // zenith 为第三方驱动，非官方标准驱动
                Arguments.of("jdbc:zenith://127.0.0.1:8000/baomidou", DbType.GAUSS),
                Arguments.of("jdbc:gaussdb://127.0.0.1:8000/baomidou", DbType.GAUSS_DB),
                Arguments.of("jdbc:gbase://127.0.0.1:5678/test", DbType.GBASE),
                Arguments.of("jdbc:gbasedbt-sqli://127.0.0.1:9088/test:INFORMIXSERVER=ol_gbasedbt", DbType.GBASE_8S),
                Arguments.of("jdbc:gbase8s-pg://127.0.0.1:15000/test", DbType.GBASE8S_PG),
                Arguments.of("jdbc:gbase8c://127.0.0.1:5432/test", DbType.GBASE_8C),
                Arguments.of("jdbc:clickhouse://127.0.0.1:8123/test", DbType.CLICK_HOUSE),
                Arguments.of("jdbc:ch://127.0.0.1:8123/test", DbType.CLICK_HOUSE),
                Arguments.of("jdbc:oscar://127.0.0.1:2003/test", DbType.OSCAR),
                Arguments.of("jdbc:sybase:Tds:127.0.0.1:5000/test", DbType.SYBASE),
                Arguments.of("jdbc:oceanbase://127.0.0.1:2881/test", DbType.OCEAN_BASE),
                Arguments.of("jdbc:highgo://127.0.0.1:5866/test", DbType.HIGH_GO),
                Arguments.of("jdbc:cubrid:127.0.0.1:33000/test::", DbType.CUBRID),
                Arguments.of("jdbc:sundb://127.0.0.1:8888/test", DbType.SUNDB),
                Arguments.of("jdbc:sap://127.0.0.1:39015?databaseName=test", DbType.SAP_HANA),
                Arguments.of("jdbc:impala://127.0.0.1:21050/test", DbType.IMPALA),
                Arguments.of("jdbc:vertica://127.0.0.1:5433/test", DbType.VERTICA),
                Arguments.of("jdbc:xcloud://127.0.0.1:5866/test", DbType.XCloud),
                Arguments.of("jdbc:firebirdsql://127.0.0.1:3050/test", DbType.FIREBIRD),
                Arguments.of("jdbc:redshift://127.0.0.1:5439/test", DbType.REDSHIFT),
                Arguments.of("jdbc:opengauss://127.0.0.1:5432/test", DbType.OPENGAUSS),
                Arguments.of("jdbc:TAOS://127.0.0.1:6030/test", DbType.TDENGINE),
                Arguments.of("jdbc:taos-ws://127.0.0.1:6041/test", DbType.TDENGINE),
                Arguments.of("jdbc:informix://127.0.0.1:1526/test", DbType.INFORMIX),
                Arguments.of("jdbc:sinodb://127.0.0.1:5888/test", DbType.SINODB),
                Arguments.of("jdbc:uxdb://127.0.0.1:5432/test", DbType.UXDB),
                Arguments.of("jdbc:trino://127.0.0.1:8080/default", DbType.TRINO),
                Arguments.of("jdbc:presto://127.0.0.1:8080/default", DbType.PRESTO),
                Arguments.of("jdbc:derby:test;create=true", DbType.DERBY),
                Arguments.of("jdbc:vastbase://127.0.0.1:5432/test", DbType.VASTBASE),
                Arguments.of("jdbc:goldendb://127.0.0.1:8888/test", DbType.GOLDENDB),
                Arguments.of("jdbc:duckdb:/tmp/test.db", DbType.DUCKDB),
                Arguments.of("jdbc:yasdb://127.0.0.1:7700/test", DbType.YASDB),
                Arguments.of("jdbc:hive2://127.0.0.1:10000/default", DbType.HIVE2),
                Arguments.of("jdbc:inceptor2://127.0.0.1:10000/default", DbType.HIVE2));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("jdbcUrlRouting")
    void jdbcUrlResolvesToExpectedDbType(String jdbcUrl, DbType expected) {
        assertThat(JdbcUtils.getDbType(jdbcUrl)).isEqualTo(expected);
    }

    @Test
    void unknownJdbcUrlFallsBackToOther() {
        assertThat(JdbcUtils.getDbType("jdbc:unknowndb://127.0.0.1:3306/test")).isEqualTo(DbType.OTHER);
    }

    /**
     * 特征化断言：识别按 if 链顺序做子串匹配，靠前的分支会截走直觉上该归后面的类型的地址。
     * <p>
     * 这里钉住当前行为，改动 if 链顺序时本测试必须同步，且需要确认是否真的修好了误判。
     */
    @Test
    void earlierBranchWinsOverLaterOne() {
        // informix 官方地址是 jdbc:informix-sqli://，被 gbase 8s 分支截走，识别不到 INFORMIX
        assertThat(JdbcUtils.getDbType("jdbc:informix-sqli://127.0.0.1:1526/test:INFORMIXSERVER=ol_informix"))
                .isEqualTo(DbType.GBASE_8S);
        // 同理 gbasedbt 官方地址归到 GBASE_8S，识别不到 GBASEDBT
        assertThat(JdbcUtils.getDbType("jdbc:gbasedbt-sqli://127.0.0.1:9088/test")).isEqualTo(DbType.GBASE_8S);
        // 地址里带 oracle 段时归 ORACLE，识别不到 ORACLE_12C，12c 只能靠显式配置 DbType
        assertThat(JdbcUtils.getDbType("jdbc:oracle:thin:@//127.0.0.1:1521/ORCLCDB")).isEqualTo(DbType.ORACLE);
        // h2 以 MySQL 兼容模式运行时不会被 ":mysql:" 分支误判
        assertThat(JdbcUtils.getDbType("jdbc:h2:mem:test;MODE=mysql")).isEqualTo(DbType.H2);
    }

    @Test
    void jdbcUrlTableCoversEveryDbType() {
        Set<DbType> recognized = jdbcUrlRouting().map(arguments -> (DbType) arguments.get()[1])
                .collect(Collectors.toCollection(TreeSet::new));
        recognized.add(DbType.OTHER);
        assertThat(recognized).hasSize(DbType.values().length - unreachableDbTypes.size())
                .containsExactlyInAnyOrderElementsOf(EnumSet.complementOf(unreachableDbTypes));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void blankJdbcUrlThrows(String jdbcUrl) {
        assertThatThrownBy(() -> JdbcUtils.getDbType(jdbcUrl)).isInstanceOf(MybatisPlusException.class)
                .hasMessage("Error: The jdbcUrl is Null, Cannot read database type");
    }

}
