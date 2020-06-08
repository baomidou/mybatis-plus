package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujiawei
 * @date 2020/6/8
 */
public class IdentifierGeneratorKit {

    private static final Map<Class<? extends IdentifierGenerator>, IdentifierGenerator> GENERATOR_CACHE = new ConcurrentHashMap<>();

    public static IdentifierGenerator getByClass(Class<? extends IdentifierGenerator> clazz) {
        if (GENERATOR_CACHE.containsKey(clazz)) {
            return GENERATOR_CACHE.get(clazz);
        }

        synchronized (IdentifierGeneratorKit.class) {
            if (GENERATOR_CACHE.containsKey(clazz)) {
                return GENERATOR_CACHE.get(clazz);
            }

            IdentifierGenerator generator;
            try {
                generator = ClassUtils.newInstance(clazz);
            } catch (Exception e) {
                generator = null;
            }
            GENERATOR_CACHE.put(clazz, generator);
            return generator;
        }
    }
}
