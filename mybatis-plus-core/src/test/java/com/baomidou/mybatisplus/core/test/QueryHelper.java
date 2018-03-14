package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.test.Query.Property;
import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.InvocationExpression;
import com.trigersoft.jaque.expression.LambdaExpression;
import com.trigersoft.jaque.expression.MemberExpression;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryHelper {

    public static <T> Method method(Property<T, ?> fun) {
        LambdaExpression parsed = LambdaExpression.parse(fun);
        Expression body = parsed.getBody();
        return (Method) ((MemberExpression) ((InvocationExpression) body).getTarget()).getMember();
    }

    public static Map<Method,String> introspector(Class clazz) {
        Map<Method,String>  map = new LinkedHashMap<>();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz, Object.class);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (Field field : clazz.getDeclaredFields()) {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getName().equals(field.getName())) {
                    TableField tableField = field.getDeclaredAnnotation(TableField.class);
                    if(tableField==null){
                    map.put(propertyDescriptor.getReadMethod(),field.getName());
                    }else {
                        String value = tableField.value();
                        if(value==null||value.isEmpty()){
                            map.put(propertyDescriptor.getReadMethod(),field.getName());
                        }else{
                        map.put(propertyDescriptor.getReadMethod(), value);
                        }
                    }
                }
            }
        }
        return map;
    }
}
