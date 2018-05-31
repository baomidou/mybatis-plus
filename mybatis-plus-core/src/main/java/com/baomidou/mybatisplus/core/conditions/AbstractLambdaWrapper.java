package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.MapUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.Property;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import java.util.Map;

/**
 * <p>
 * Lambda 语法使用 Wrapper
 * 统一处理解析 lambda 获取 column
 * </p>
 *
 * @author hubin miemie HCL
 * @since 2017-05-26
 */
public abstract class AbstractLambdaWrapper<T, This extends AbstractLambdaWrapper<T, This>> extends AbstractWrapper<T, Property<T, ?>, This> {
    private Map<String, String> columnMap = null;
    private boolean initColumnMap = false;

    @Override
    public String columnToString(Property<T, ?> column) {
        return getColumn(LambdaUtils.resolve(column));
    }

    private String getColumn(SerializedLambda lambda) {
        if (!initColumnMap) {
            String entityClassName = lambda.getImplClass().replace("/", ".");
            columnMap = LambdaUtils.getColumnMap(entityClassName);
            if (MapUtils.isEmpty(columnMap)) {
                throw new MybatisPlusException("该模式不能应用于非 baseMapper 的泛型 entity 之外的 entity");
            }
            initColumnMap = true;
        }
        String column = columnMap.get(StringUtils.resolveFieldName(lambda.getImplMethodName()));
        if (column == null) {
            throw new MybatisPlusException("该模式不能应用于非数据库字段");
        }
        return column;
    }
}
