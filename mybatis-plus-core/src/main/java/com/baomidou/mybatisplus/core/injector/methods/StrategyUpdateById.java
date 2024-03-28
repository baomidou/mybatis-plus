package com.baomidou.mybatisplus.core.injector.methods;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.HashMap;

/**
 * @author 张治保
 * @since 2023/12/29
 */
public class StrategyUpdateById extends AbstractMethod {

    private static final String ALWAYS_EXPRESSION =String.format("%s == null or %s == @com.baomidou.mybatisplus.annotation.FieldStrategy@IGNORED  or %s == @com.baomidou.mybatisplus.annotation.FieldStrategy@ALWAYS",STRATEGY,STRATEGY,STRATEGY);
    private static final String NOT_NULL_EXPRESSION =String.format("%s == @com.baomidou.mybatisplus.annotation.FieldStrategy@NOT_NULL",STRATEGY);
    private static final String NOT_EMPTY_EXPRESSION =String.format("%s == @com.baomidou.mybatisplus.annotation.FieldStrategy@NOT_EMPTY",STRATEGY);

    private static final String ALL_BIND;

    static {
        ALL_BIND = SqlScriptUtils.convertBinds(
            new HashMap<String,String>(){
                {
                    put(ALWAYS_STRATEGY_BINDER,ALWAYS_EXPRESSION);
                    put(NOT_NULL_STRATEGY_BINDER,NOT_NULL_EXPRESSION);
                    put(NOT_EMPTY_STRATEGY_BINDER,NOT_EMPTY_EXPRESSION);
                }
            }
        );
    }
    public StrategyUpdateById(){
        this(SqlMethod.STRATEGY_UPDATE_BY_ID.getMethod());
    }

    public StrategyUpdateById(String methodName) {
        super(methodName);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.STRATEGY_UPDATE_BY_ID;
        final String additional = optlockVersion(tableInfo) + tableInfo.getLogicDeleteSql(true, true);
        String sql = String.format(
            sqlMethod.getSql(),
            ALL_BIND,
            tableInfo.getTableName(),
            sqlSet(tableInfo.isWithLogicDelete(), false, tableInfo, false, ENTITY, ENTITY_DOT),
            tableInfo.getKeyColumn(), ENTITY_DOT + tableInfo.getKeyProperty(), additional);
        SqlSource sqlSource = super.createSqlSource(configuration, sql, modelClass);
        return addUpdateMappedStatement(mapperClass, modelClass, methodName, sqlSource);
    }


    @Override
    protected String getTableAllSqlSet(TableInfo table, boolean logic, String prefix) {
        return table.getAllSqlSet(logic, prefix,true);
    }


}
