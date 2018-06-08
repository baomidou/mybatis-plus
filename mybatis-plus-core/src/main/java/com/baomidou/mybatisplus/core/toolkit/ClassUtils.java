package com.baomidou.mybatisplus.core.toolkit;


import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;

/**
 * ClassUtils
 *
 * @author Caratacus
 * @since 2017/07/08
 */
public class ClassUtils {

    /**
     * 判断是否为代理对象
     *
     * @param clazz
     * @return
     */
    public static boolean isProxy(Class<?> clazz) {
        if (clazz != null) {
            for (Class<?> cls : clazz.getInterfaces()) {
                String interfaceName = cls.getName();
                if ("net.sf.cglib.proxy.Factory".equals(interfaceName) //cglib
                    || "org.springframework.cglib.proxy.Factory".equals(interfaceName)
                    || "javassist.util.proxy.ProxyObject".equals(interfaceName) //javassist
                    || "org.apache.ibatis.javassist.util.proxy.ProxyObject".equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前对象的class
     *
     * @param clazz
     * @return
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        return isProxy(clazz) ? clazz.getSuperclass() : clazz;
    }

    /**
     * 获取当前对象的class
     *
     * @param object
     * @return
     */
    public static Class<?> getUserClass(Object object) {
        if (object == null) {
            throw new MybatisPlusException("Error: Instance must not be null");
        }
        return getUserClass(object.getClass());
    }

}
