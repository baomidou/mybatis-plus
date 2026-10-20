package com.baomidou.mybatisplus.test.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.DB2Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.GBase8sDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.GaussDBDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.Hive2Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.InformixDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.Oracle12cDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.OracleDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.PostgreDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SQLServer2005Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.SybaseDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.TrinoDialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.XCloudDialect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author nieqiurong
 */
public class DialectFactoryTest {

    /**
     * 每个 {@link DbType} 的分页方言归属，除 OTHER 外必须逐一对应，新增数据库类型时由 {@link #routingTableCoversEveryDbType()} 强制在这里登记。
     */
    private static Stream<Arguments> dialectRouting() {
        return Stream.of(
                // mysqlSameType
                Arguments.of(DbType.MYSQL, MySqlDialect.class), Arguments.of(DbType.MARIADB, MySqlDialect.class),
                Arguments.of(DbType.GBASE, MySqlDialect.class), Arguments.of(DbType.OSCAR, MySqlDialect.class),
                Arguments.of(DbType.XU_GU, MySqlDialect.class), Arguments.of(DbType.CLICK_HOUSE, MySqlDialect.class),
                Arguments.of(DbType.OCEAN_BASE, MySqlDialect.class), Arguments.of(DbType.CUBRID, MySqlDialect.class),
                Arguments.of(DbType.SUNDB, MySqlDialect.class), Arguments.of(DbType.GOLDENDB, MySqlDialect.class),
                Arguments.of(DbType.YASDB, MySqlDialect.class),
                // oracleSameType
                Arguments.of(DbType.DM, OracleDialect.class), Arguments.of(DbType.ORACLE, OracleDialect.class),
                Arguments.of(DbType.POLARDB, OracleDialect.class), Arguments.of(DbType.GAUSS, OracleDialect.class),
                // postgresqlSameType
                Arguments.of(DbType.POSTGRE_SQL, PostgreDialect.class), Arguments.of(DbType.H2, PostgreDialect.class),
                Arguments.of(DbType.LEALONE, PostgreDialect.class), Arguments.of(DbType.SQLITE, PostgreDialect.class),
                Arguments.of(DbType.HSQL, PostgreDialect.class), Arguments.of(DbType.KINGBASE_ES, PostgreDialect.class),
                Arguments.of(DbType.PHOENIX, PostgreDialect.class), Arguments.of(DbType.SAP_HANA, PostgreDialect.class),
                Arguments.of(DbType.IMPALA, PostgreDialect.class), Arguments.of(DbType.HIGH_GO, PostgreDialect.class),
                Arguments.of(DbType.VERTICA, PostgreDialect.class), Arguments.of(DbType.REDSHIFT, PostgreDialect.class),
                Arguments.of(DbType.OPENGAUSS, PostgreDialect.class),
                Arguments.of(DbType.TDENGINE, PostgreDialect.class), Arguments.of(DbType.UXDB, PostgreDialect.class),
                Arguments.of(DbType.GBASE8S_PG, PostgreDialect.class),
                Arguments.of(DbType.GBASE_8C, PostgreDialect.class),
                Arguments.of(DbType.VASTBASE, PostgreDialect.class), Arguments.of(DbType.DUCKDB, PostgreDialect.class),
                // 独立方言
                Arguments.of(DbType.GAUSS_DB, GaussDBDialect.class),
                Arguments.of(DbType.ORACLE_12C, Oracle12cDialect.class),
                Arguments.of(DbType.FIREBIRD, Oracle12cDialect.class),
                Arguments.of(DbType.SQL_SERVER, Oracle12cDialect.class),
                Arguments.of(DbType.DERBY, Oracle12cDialect.class), Arguments.of(DbType.DB2, DB2Dialect.class),
                Arguments.of(DbType.SQL_SERVER2005, SQLServer2005Dialect.class),
                Arguments.of(DbType.SYBASE, SybaseDialect.class), Arguments.of(DbType.XCloud, XCloudDialect.class),
                Arguments.of(DbType.GBASE_8S, GBase8sDialect.class),
                Arguments.of(DbType.GBASEDBT, GBase8sDialect.class),
                Arguments.of(DbType.GBASE_INFORMIX, GBase8sDialect.class),
                Arguments.of(DbType.SINODB, GBase8sDialect.class), Arguments.of(DbType.INFORMIX, InformixDialect.class),
                Arguments.of(DbType.TRINO, TrinoDialect.class), Arguments.of(DbType.PRESTO, TrinoDialect.class),
                Arguments.of(DbType.HIVE2, Hive2Dialect.class));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("dialectRouting")
    void dbTypeRoutesToExpectedDialect(DbType dbType, Class<? extends IDialect> dialectClass) {
        assertThat(DialectFactory.getDialect(dbType)).isExactlyInstanceOf(dialectClass);
    }

    @Test
    void otherDbTypeIsNotSupported() {
        assertThatThrownBy(() -> DialectFactory.getDialect(DbType.OTHER)).isInstanceOf(MybatisPlusException.class)
                .hasMessage("other database not supported.");
    }

    @Test
    void routingTableCoversEveryDbType() {
        Set<DbType> routed = dialectRouting().map(arguments -> (DbType) arguments.get()[0])
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
        assertThat(routed).containsExactlyInAnyOrderElementsOf(EnumSet.complementOf(EnumSet.of(DbType.OTHER)));
    }

}
