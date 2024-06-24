package com.baomidou.mybatisplus.core.toolkit;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bean转换工具类
 *
 * @author hll
 * @since 2024/04/26
 */
@Slf4j
@UtilityClass
public class BeanConvertUtils {

    private static final Map<String, String> BASE_DATA_TYPE;

    private static final String GET = "get";

    private static final String SET = "set";

    private static final String IS = "is";

    private static final String BOOL = "boolean";

    private static final String STATIC = " static ";

    private static final String FINAL = " final ";

    static {
        BASE_DATA_TYPE = new HashMap<>();
        BASE_DATA_TYPE.put("byte", "java.lang.Byte");
        BASE_DATA_TYPE.put("short", "java.lang.Short");
        BASE_DATA_TYPE.put("int", "java.lang.Integer");
        BASE_DATA_TYPE.put("long", "java.lang.Long");
        BASE_DATA_TYPE.put("float", "java.lang.Float");
        BASE_DATA_TYPE.put("double", "java.lang.Double");
        BASE_DATA_TYPE.put(BOOL, "java.lang.Boolean");
        BASE_DATA_TYPE.put("char", "java.lang.Character");

        BASE_DATA_TYPE.put("java.lang.Byte", "byte");
        BASE_DATA_TYPE.put("java.lang.Short", "short");
        BASE_DATA_TYPE.put("java.lang.Integer", "int");
        BASE_DATA_TYPE.put("java.lang.Long", "long");
        BASE_DATA_TYPE.put("java.lang.Float", "float");
        BASE_DATA_TYPE.put("java.lang.Double", "double");
        BASE_DATA_TYPE.put("java.lang.Boolean", BOOL);
        BASE_DATA_TYPE.put("java.lang.Character", "char");
    }

    /**
     * <a href="https://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/">JavaBean命名规范</a>
     *
     * @param sourceList  源类实例对象列表
     * @param targetClass 目标类元
     * @param <S>         源类类型
     * @param <T>         目标类类型
     * @return 目标类实例对象列表
     */
    @SuppressWarnings("unchecked")
    public <S, T> List<T> convert(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        Class<S> sourceClass = (Class<S>) sourceList.get(0).getClass();

        Set<String> sourceFiledKeySet = getBeanFiledKeySet(sourceClass);
        Set<String> targetFiledKeySet = getBeanFiledKeySet(targetClass);
        Map<String, Method> getterMethodMap = getGetterMethod(sourceList.get(0).getClass());
        Map<String, Method> setterMethodMap = getSetterMethod(targetClass);

        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            T target;
            try {
                target = targetClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw ExceptionUtils.mpe("实例化[%s]失败!", ex, targetClass.getName());
            }
            copyField(sourceFiledKeySet, targetFiledKeySet, getterMethodMap, setterMethodMap, source, target);
            targetList.add(target);
        }
        return targetList;
    }

    private <S, T> void copyField(Set<String> sourceFiledKeySet, Set<String> targetFiledKeySet,
                                  Map<String, Method> getterMethodMap, Map<String, Method> setterMethodMap,
                                  S source, T target) {
        for (String targetField : targetFiledKeySet) {
            if (containsField(sourceFiledKeySet, targetField)) {
                String fieldName = StringUtils.capitalize(getBeanFiledName(targetField));
                String getterMethodName = (targetField.startsWith(BOOL) ? IS : GET) + fieldName;
                String setterMethodName = SET + fieldName;
                if (getterMethodMap.containsKey(getterMethodName)
                    && setterMethodMap.containsKey(setterMethodName)) {
                    copyValue(source, setterMethodMap, target, getterMethodMap, getterMethodName, setterMethodName);
                }
            }
        }
    }

    private <S, T> void copyValue(S source, Map<String, Method> setterMethodMap,
                                  T target, Map<String, Method> getterMethodMap,
                                  String getterMethodName, String setterMethodName) {
        Method getterMethod = getterMethodMap.get(getterMethodName);
        try {
            Object value = getterMethod.invoke(source);
            Method setterMethod = setterMethodMap.get(setterMethodName);
            if (value != null) {
                setterMethod.invoke(target, value);
            }
        } catch (Exception ex) {
            throw ExceptionUtils.mpe("字段值拷贝失败! getter: [%s], setter: [%s].",
                ex, getterMethodName, setterMethodName);
        }
    }

    /**
     * @param beanFieldKey 字段的key e.g. "java.lang.Integer age"
     * @return 大写字段名 e.g. "age"
     */
    private String getBeanFiledName(String beanFieldKey) {
        return beanFieldKey.split(StringPool.SPACE)[1];
    }

    /**
     * 源字段集合是否包含目标字段
     *
     * @param sourceFieldKeySet 源字段key集合
     * @param targetFiledKey    目标字段key
     * @return 源字段集合是否包含目标字段
     */
    private boolean containsField(Set<String> sourceFieldKeySet, String targetFiledKey) {
        if (sourceFieldKeySet.contains(targetFiledKey)) {
            return true;
        }
        String type = targetFiledKey.split(StringPool.SPACE)[0];
        String boxType = BASE_DATA_TYPE.get(type);
        if (boxType != null && sourceFieldKeySet.contains(targetFiledKey.replace(type, boxType))) {
            return true;
        }
        boxType = BASE_DATA_TYPE.get(boxType);
        return boxType != null && sourceFieldKeySet.contains(targetFiledKey.replace(type, boxType));
    }

    /**
     * @param clazz 类元
     * @return 字段信息
     */
    private Set<String> getBeanFiledKeySet(Class<?> clazz) {
        return getAllFields(clazz)
            .stream()
            .map(BeanConvertUtils::getFieldKey)
            .collect(Collectors.toSet());
    }

    /**
     * 获得字段的key
     *
     * @param field 字段
     * @return key e.g. "java.lang.Integer age"
     */
    private String getFieldKey(Field field) {
        String fieldToString = field.toString();
        String[] split = fieldToString.split(StringPool.SPACE);
        String fieldName = split[split.length - 1];
        String fieldType = split[split.length - 2];
        fieldName = fieldName.substring(fieldName.lastIndexOf(StringPool.DOT) + 1);
        return fieldType + StringPool.SPACE + fieldName;
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    private List<Field> getAllFields(Class<?> beanClass) throws SecurityException {
        List<Field> allFields = null;
        Class<?> searchType = beanClass;
        List<Field> declaredFields;
        while (searchType != null) {
            declaredFields = Arrays.asList(searchType.getDeclaredFields());
            if (null == allFields) {
                allFields = declaredFields.stream()
                    .filter(x -> !x.toString().contains(STATIC) && !x.toString().contains(FINAL))
                    .collect(Collectors.toList());
            } else {
                allFields.addAll(declaredFields);
            }
            searchType = searchType.getSuperclass();
        }
        return allFields;
    }

    /**
     * 获得类及其父类的所有的以{@code prefixes}里的某个前缀开始的方法名的方法
     *
     * @param clazz 类元
     * @return setter
     */
    private Map<String, Method> filterMethodByPrefix(Class<?> clazz, String... prefixes) {
        Predicate<Method> methodFilter = x -> {
            boolean result = false;
            for (String prefix : prefixes) {
                result = result || x.getName().startsWith(prefix);
            }
            return result;
        };
        return Arrays.stream(clazz.getMethods())
            .filter(methodFilter)
            .collect(Collectors.toMap(Method::getName, x -> x));
    }

    /**
     * 获得类及其父类的所有的getter方法
     *
     * @param clazz 类元
     * @return getter方法的map (k, v) -> (方法名, 方法)
     */
    private Map<String, Method> getGetterMethod(Class<?> clazz) {
        return filterMethodByPrefix(clazz, GET, IS);
    }

    /**
     * 获得类及其父类的所有的setter方法
     *
     * @param clazz 类元
     * @return setter方法的map (k, v) -> (方法名, 方法)
     */
    private Map<String, Method> getSetterMethod(Class<?> clazz) {
        return filterMethodByPrefix(clazz, SET);
    }
}
