package com.baomidou.mybatisplus.core.toolkit;

/**
 * 泛型类工具（用于隔离Spring的代码）
 *
 * @author noear
 * @since 2021-09-03
 */
public class GenericTypeUtils {
    private static GenericTypeHelper genericTypeHelper;

    /**
     * 获取泛型工具助手
     * */
    public static GenericTypeHelper getGenericTypeHelper() {
        if(genericTypeHelper == null){
            genericTypeHelper = new GenericTypeHelperDefault();
        }

        return genericTypeHelper;
    }

    /**
     * 设置泛型工具助手。如果不想使用Spring封装，可以使用前替换掉
     * */
    public static void setGenericTypeHelper(GenericTypeHelper genericTypeHelper) {
        GenericTypeUtils.genericTypeHelper = genericTypeHelper;
    }
}
