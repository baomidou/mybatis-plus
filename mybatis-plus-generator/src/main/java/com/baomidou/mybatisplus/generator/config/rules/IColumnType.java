package com.baomidou.mybatisplus.generator.config.rules;

/**
 * <p>
 * 获取实体类字段属性类信息接口
 * </p>
 *
 * @author miemie
 * @since 2018-08-22
 */
public interface IColumnType {

    /**
     * <p>
     * 获取字段类型
     * </p>
     *
     * @return 字段类型
     */
    String getType();

    /**
     * <p>
     * 获取字段类型完整名
     * </p>
     *
     * @return 字段类型完整名
     */
    String getPkg();
}
