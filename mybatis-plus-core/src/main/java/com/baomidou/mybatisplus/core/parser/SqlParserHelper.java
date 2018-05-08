package com.baomidou.mybatisplus.core.parser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.annotation.SqlParser;

public class SqlParserHelper {

    public static final String DELEGATE_MAPPED_STATEMENT = "delegate.mappedStatement";
    /**
     * SQL 解析缓存
     */
    private static final Map<String, SqlParserInfo> SQL_PARSER_INFO_CACHE = new ConcurrentHashMap<>();


    /**
     * <p>
     * 初始化缓存 SqlParser 注解信息
     * </p>
     *
     * @param mapperClass Mapper Class
     */
    public synchronized static void initSqlParserInfoCache(Class<?> mapperClass) {
        Method[] methods = mapperClass.getDeclaredMethods();
        for (Method method : methods) {
            SqlParser sqlParser = method.getAnnotation(SqlParser.class);
            if (null != sqlParser) {
                StringBuilder sid = new StringBuilder();
                sid.append(mapperClass.getName()).append(".").append(method.getName());
                SQL_PARSER_INFO_CACHE.put(sid.toString(), new SqlParserInfo(sqlParser));
            }
        }
    }


    /**
     * <p>
     * 获取 SqlParser 注解信息
     * </p>
     *
     * @param metaObject 元数据对象
     * @return
     */
    public static SqlParserInfo getSqlParserInfo(MetaObject metaObject) {
        return SQL_PARSER_INFO_CACHE.get(getMappedStatement(metaObject).getId());
    }


    /**
     * <p>
     * 获取当前执行 MappedStatement
     * </p>
     *
     * @param metaObject 元对象
     * @return
     */
    public static MappedStatement getMappedStatement(MetaObject metaObject) {
        return (MappedStatement) metaObject.getValue(DELEGATE_MAPPED_STATEMENT);
    }
}
