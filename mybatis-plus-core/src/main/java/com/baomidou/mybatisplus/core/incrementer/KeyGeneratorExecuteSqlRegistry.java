package com.baomidou.mybatisplus.core.incrementer;

import java.util.EnumMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * 数据库序列SQL模板注册
 *
 * @author micuncang
 * @date 2021/1/9
 */
public class KeyGeneratorExecuteSqlRegistry {

    private static final Map<DbType, String> KEY_GENERATOR_SQL_MAP = new EnumMap<>(DbType.class);

    static {
        // postgresql and children
        KEY_GENERATOR_SQL_MAP.put(DbType.POSTGRE_SQL, "select nextval('%s')");
        KEY_GENERATOR_SQL_MAP.put(DbType.H2, "select %s.nextval");
        KEY_GENERATOR_SQL_MAP.put(DbType.KINGBASE_ES, "select nextval('%s')");
        // oracle and children
        KEY_GENERATOR_SQL_MAP.put(DbType.ORACLE, "SELECT %s.NEXTVAL FROM DUAL");
        // other
        KEY_GENERATOR_SQL_MAP.put(DbType.DB2, "values nextval for %s");
    }

    /**
     * 按需注册数据库对应的序列SQL模板
     *
     * @param dbType     数据库类型
     * @param executeSql 数据库序列SQL模板
     */
    public static void regist(DbType dbType, String executeSql) {
        KEY_GENERATOR_SQL_MAP.put(dbType, executeSql);
    }

    /**
     * 根据数据库类型获取序列SQL模板
     *
     * @param dbType 数据库类型
     * @return 序列SQL模板
     */
    public static String getExecuteSql(DbType dbType) {
        return KEY_GENERATOR_SQL_MAP.get(dbType);
    }

}
