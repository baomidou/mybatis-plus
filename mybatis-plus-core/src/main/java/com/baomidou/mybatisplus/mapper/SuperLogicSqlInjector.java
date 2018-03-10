package com.baomidou.mybatisplus.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.toolkit.StringUtils;

/**
 * <p>
 * 逻辑删除，公共父类，抽出来
 * 支持全update、select类型SQL自动添加逻辑删除字段和mp提供的api自动添加的公共方法 <br>
 * </p>
 *
 * @author hubin willenfoo
 * @Date 2018-03-09
 */
public class SuperLogicSqlInjector extends AutoSqlInjector {

    /**
     * 根据 ID 删除
     */
    @Override
    protected void injectDeleteByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        if (table.isLogicDelete()) {
            // 逻辑删除注入
            SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_ID;
            SqlSource sqlSource;
            String idStr = table.getKeyProperty();
            if (batch) {
                sqlMethod = SqlMethod.LOGIC_DELETE_BATCH_BY_IDS;
                StringBuilder ids = new StringBuilder();
                ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"coll\" separator=\",\">");
                ids.append("#{item}");
                ids.append("\n</foreach>");
                idStr = ids.toString();
            }
            String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table),
                table.getKeyColumn(), idStr);
            sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
        } else {
            // 正常删除
            super.injectDeleteByIdSql(batch, mapperClass, modelClass, table);
        }
    }

    /**
     * 根据 SQL 删除
     */
    @Override
    protected void injectDeleteSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        if (table.isLogicDelete()) {
            // 逻辑删除注入
            SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE;
            String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table),
                sqlWhereEntityWrapper(table));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
        } else {
            // 正常删除
            super.injectDeleteSql(mapperClass, modelClass, table);
        }
    }

    /**
     * 根据 MAP 删除
     */
    @Override
    protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
        if (table.isLogicDelete()) {
            // 逻辑删除注入
            SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_MAP;
            String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlLogicSet(table),
                sqlWhereByMap(table));
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
            this.addUpdateMappedStatement(mapperClass, Map.class, sqlMethod.getMethod(), sqlSource);
        } else {
            // 正常删除
            super.injectDeleteByMapSql(mapperClass, table);
        }
    }

    /**
     * <p>
     * SQL 更新 set 语句
     * </p>
     *
     * @param table 表信息
     * @return sql set 片段
     */
    protected String sqlLogicSet(TableInfo table) {
        List<TableFieldInfo> fieldList = table.getFieldList();
        StringBuilder set = new StringBuilder("SET ");
        int i = 0;
        for (TableFieldInfo fieldInfo : fieldList) {
            if (fieldInfo.isLogicDelete()) {
                if (++i > 1) {
                    set.append(",");
                }
                set.append(fieldInfo.getColumn()).append("=");
                if (StringUtils.isCharSequence(fieldInfo.getPropertyType())) {
                    set.append("'").append(fieldInfo.getLogicDeleteValue()).append("'");
                } else {
                    set.append(fieldInfo.getLogicDeleteValue());
                }
            }
        }
        return set.toString();
    }
}
