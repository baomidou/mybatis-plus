package com.baomidou.mybatisplus.core.enums;

/**
 * sql method 顶级抽象结构
 *
 * @author 张治保
 * @since 2023/12/29
 */
public interface ISqlMethod {

    /**
     * 获取方法名
     *
     * @return 方法名
     */
    String getMethod();

    /**
     * 获取方法描述
     *
     * @return 方法描述
     */
    String getDesc();

    /**
     * 获取sql
     *
     * @return sql
     */
    String getSql();
}
