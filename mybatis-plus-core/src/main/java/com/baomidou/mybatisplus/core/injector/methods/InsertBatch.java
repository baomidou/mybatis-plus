package com.baomidou.mybatisplus.core.injector.methods;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * SQL注入的格式:
 *
 *  CREATE TABLE `user` (
 *   `id` int NOT NULL AUTO_INCREMENT,
 *   `name` varchar(255) DEFAULT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 *
 *  批量生成的SQL
 *
 *      insert into user (id,name) values
 *      <foreach collection="list", item="i" separator=",">
 *          (#{i.id},#{i.name})
 *      </foreach>
 *
 */
public class InsertBatch extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        SqlMethod sqlMethod = SqlMethod.INSERT_BATCH;
        String columnScript = SqlScriptUtils.convertTrim(tableInfo.getKeyInsertSqlColumn(true) + tableInfo.getFieldList().stream().map(TableFieldInfo::getColumn).collect(joining(NEWLINE)),
            LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String valuesScript = SqlScriptUtils.convertForeach(LEFT_BRACKET +
            SqlScriptUtils.convertTrim(getAllInsertSqlPropertyMaybeIf("i.", tableInfo), null, null, null, COMMA)
            + RIGHT_BRACKET, "list", null, "i", ",");
        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                /** 自增主键 */
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else {
                if (null != tableInfo.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(getMethod(sqlMethod), tableInfo, builderAssistant);
                    keyProperty = tableInfo.getKeyProperty();
                    keyColumn = tableInfo.getKeyColumn();
                }
            }
        }
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource, keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 对属性添加prefix,并且不生成<if>标签
     * @param prefix
     * @param tableInfo
     * @return
     */
    private String getAllInsertSqlPropertyMaybeIf(final String prefix, TableInfo tableInfo) {
        final String newPrefix = prefix == null ? EMPTY : prefix;
        return tableInfo.getKeyInsertSqlProperty(newPrefix, true) +
            tableInfo.getFieldList().stream().map(i ->
                i.getInsertSqlProperty(newPrefix)).filter(Objects::nonNull).collect(joining(NEWLINE));
    }

}

