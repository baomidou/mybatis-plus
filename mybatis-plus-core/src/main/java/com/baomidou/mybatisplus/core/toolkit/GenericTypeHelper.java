package com.baomidou.mybatisplus.core.toolkit;

/**
 * 泛型类助手（用于隔离Spring的代码）
 *
 * @author noear
 * @since 2021-09-03
 */
public interface GenericTypeHelper {
    Class<?>[] resolveTypeArguments(final Class<?> clazz, final Class<?> genericIfc);
}
