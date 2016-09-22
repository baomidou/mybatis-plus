/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>
 * 反射工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-09-22
 */
public class ReflectionKit {

    private static Logger logger = LoggerFactory.getLogger(ReflectionKit.class);

    /**
     * <p>
     * 反射 method 方法名，例如 getId
     * </p>
     *
     * @param str 属性字符串内容
     * @return
     */
    public static String getMethodCapitalize(final String str) {
        return StringUtils.concatCapitalize("get", str);
    }

    /**
     * 获取 public get方法的值
     *
     * @param cls
     * @param entity 实体
     * @param str    属性字符串内容
     * @return Object
     */
    public static Object getMethodValue(Class cls, Object entity, String str) {
        Object obj = null;
        try {
            Method method = cls.getMethod(getMethodCapitalize(str));
            obj = method.invoke(entity);
        } catch (NoSuchMethodException e) {
            logger.warn("Warn: No such method. in " + cls);
        } catch (IllegalAccessException e) {
            logger.warn("Warn: Cannot execute a private method. in " + cls);
        } catch (InvocationTargetException e) {
            logger.warn("Warn: Unexpected exception on getMethodValue.  Cause:" + e);
        }
        return obj;
    }

    /**
     * 获取 public get方法的值
     *
     * @param entity 实体
     * @param str    属性字符串内容
     * @return Object
     */
    public static Object getMethodValue(Object entity, String str) {
        return getMethodValue(entity.getClass(), entity, str);
    }

    /**
     * 调用对象的get方法检查对象所有属性是否为null
     *
     * @param bean 检查对象
     * @return boolean true对象所有属性不为null,false对象所有属性为null
     */
    public static boolean checkFieldValueNotNull(Object bean) {
        if (null == bean) {
            return false;
        }
        Class<?> cls = bean.getClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
        if (null == tableInfo) {
            logger.warn("Warn: Could not find @TableId.");
            return false;
        }
        boolean result = false;
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        for (TableFieldInfo tableFieldInfo : fieldList) {
            Object val = getMethodValue(cls, bean, tableFieldInfo.getProperty());
            if (null != val) {
                result = true;
                break;
            }
        }
        return result;
    }

}