package com.baomidou.mybatisplus.generator.config.rules;

/**
 * <p>
 * 获取实体类字段 属性信息
 * </p>
 *
 * @author miemie
 * @since 2018-08-22
 */
public interface PropertyInfo {

    /**
     * 获取字段类型
     *
     * @return 字段类型
     */
    String getType();

    /**
     * 获取字段类型完整名
     *
     * @return 字段类型完整名
     */
    String getPkg();
}
