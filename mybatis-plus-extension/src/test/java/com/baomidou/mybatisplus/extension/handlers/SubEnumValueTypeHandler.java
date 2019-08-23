package com.baomidou.mybatisplus.extension.handlers;


import java.lang.reflect.Method;

/**
 * 自定义枚举属性转换器
 *
 * @author hubin
 * @since 2017-10-11
 */
public class SubEnumValueTypeHandler<E extends Enum<?>> extends MybatisEnumTypeHandler<E> {

    public SubEnumValueTypeHandler(Class<E> type) {
        super(type);
    }

    @Override
    protected Method getMethod(Class<E> type) {
        if (SubEnum.class.isAssignableFrom(type)) {
            try {
                return type.getMethod("anyName");
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(String.format("NoSuchMethod getValue() in Class: %s.", type.getName()));
            }
        } else {
            return super.getMethod(type);
        }
    }
}
