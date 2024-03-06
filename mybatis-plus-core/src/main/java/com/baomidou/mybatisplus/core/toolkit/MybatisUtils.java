package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.handlers.IJsonTypeHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;

/**
 * @author nieqiurong
 * @since 3.5.6
 */
@Slf4j
@UtilityClass
public class MybatisUtils {

    /**
     * 实例化Json类型处理器
     * <p>
     * 1.子类需要包含构造(Class,Field)
     * 2.如果无上述构造或者无属性字段,则使用默认构造(Class)进行实例化
     * </p>
     *
     * @param typeHandler   类型处理器 {@link IJsonTypeHandler}
     * @param javaTypeClass java类型信息
     * @param field         属性字段
     * @return 实例化类型处理器
     */
    public static TypeHandler<?> newJsonTypeHandler(Class<? extends TypeHandler<?>> typeHandler, Class<?> javaTypeClass, Field field) {
        TypeHandler<?> result = null;
        if (IJsonTypeHandler.class.isAssignableFrom(typeHandler)) {
            if (field != null) {
                try {
                    result = typeHandler.getConstructor(Class.class, Field.class).newInstance(javaTypeClass, field);
                } catch (ReflectiveOperationException e) {
                    // ignore
                }
            }
            if (result == null) {
                try {
                    result = typeHandler.getConstructor(Class.class).newInstance(javaTypeClass);
                } catch (ReflectiveOperationException ex) {
                    throw new TypeException("Failed invoking constructor for handler " + typeHandler, ex);
                }
            }
        }
        return result;
    }

}
