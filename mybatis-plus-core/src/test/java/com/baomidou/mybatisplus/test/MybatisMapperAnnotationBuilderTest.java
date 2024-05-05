package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author miemie
 * @since 2019-03-22
 */
@SuppressWarnings("all")
class MybatisMapperAnnotationBuilderTest {

    Class<?> type = Mapper.class;

    @Test
    void test() {
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            Class<?> returnType = getReturnType(method);
            System.out.println(method.getName() + "        ====================       " + returnType.getSimpleName());
        }
    }

    private Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
            // gcode issue #508
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        } else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue #443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        // (gcode issue #525) support List<byte[]>
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            } else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
                // (gcode issue 504) Do not look into Maps if there is not MapKey annotation
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    Type returnTypeParameter = actualTypeArguments[1];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue 443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            } else if (Optional.class.equals(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type returnTypeParameter = actualTypeArguments[0];
                if (returnTypeParameter instanceof Class<?>) {
                    returnType = (Class<?>) returnTypeParameter;
                }
            }
            // TODO 下面是支援 IPage 及其子类作为返回值的
            else if (IPage.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Type returnTypeParameter = actualTypeArguments[0];
                if (returnTypeParameter instanceof Class<?>) {
                    returnType = (Class<?>) returnTypeParameter;
                } else if (returnTypeParameter instanceof ParameterizedType) {
                    returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                }
            }
            // TODO 上面是支援 IPage 及其子类作为返回值的
        }

        return returnType;
    }

    interface Mapper {

        Xxx one();

        IPage<Xxx> xxxPage();

        IPage<Map<String, Object>> mapPage();

        Map<String, Object> selectMap();
    }

    class Xxx {

    }
}
