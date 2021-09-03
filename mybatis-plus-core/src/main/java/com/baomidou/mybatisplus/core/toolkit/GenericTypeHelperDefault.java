package com.baomidou.mybatisplus.core.toolkit;

import org.springframework.core.GenericTypeResolver;

/**
 * 泛型类助手默认实现
 *
 * @author noear
 * @since 2021-09-03
 */
public class GenericTypeHelperDefault implements GenericTypeHelper{
    @Override
    public Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }
}
