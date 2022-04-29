package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 更新全字段，包括值为null的字段
 *
 * @author yinzhijun
 * @date 2022/4/29 14:40
 */
public class UpdateAllColumn extends AbstractMethod {
    public UpdateAllColumn() {
        super(SqlMethod.UPDATE_ALL_COLUMN.getMethod());
    }

    public UpdateAllColumn(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.UPDATE_ALL_COLUMN;
        String sqlSet = sqlSet(true, true, tableInfo, false, ENTITY, ENTITY_DOT, false);
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet,
            sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource);
    }
}
