package com.baomidou.mybatisplus.extension.injector.methods;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.AbstractLogicMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 * 根据 id 逻辑删除数据,并带字段填充功能
 * 注意入参是 entity !!! , 非逻辑删除勿用 !!!
 * </p>
 *
 * @author miemie
 * @since 2018-09-13
 */
public class LogicDeleteByIdWithFill extends AbstractLogicMethod {
    /**
     * mapper 对应的方法名
     */
    private static final String MAPPER_METHOD = "logicDeleteByIdWithFill";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sqlMethod = "<script>\nUPDATE %s %s WHERE %s=#{%s} %s\n</script>";
        // todo 没写完
        String sql = String.format(sqlMethod, tableInfo.getTableName(), sqlLogicSet(tableInfo),
            tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
            tableInfo.getLogicDeleteSql(true, false));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, MAPPER_METHOD, sqlSource);
    }
}
