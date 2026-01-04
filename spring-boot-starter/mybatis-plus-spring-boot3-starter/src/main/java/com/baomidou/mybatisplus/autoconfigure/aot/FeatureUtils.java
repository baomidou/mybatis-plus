package com.baomidou.mybatisplus.autoconfigure.aot;

import org.graalvm.nativeimage.RuntimeOptions;
import org.graalvm.nativeimage.hosted.*;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

/**
 * 简化注册，仅限在{@link Feature}中使用
 *
 * @author xiaochen
 * @since 2025/8/22
 */
class FeatureUtils extends CollectUtils {

    private final ClassLoader classLoader;

    public static FeatureUtils newInstance(ClassLoader classLoader) {
        return new FeatureUtils(classLoader);
    }

    public FeatureUtils(ClassLoader classLoader) {
        super(classLoader);
        this.classLoader = classLoader;
    }

    public ClassLoader classLoader() {
        return classLoader;
    }

    public boolean isPresent(String className) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Class<?> loadClass(String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void registerReflection(Class<?>... classes) {
        for (Class<?> c : classes) {
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredConstructors()));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredMethods()));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredFields()));
            if (debug) System.out.println("registering reflect " + c.getName());
        }
    }

    public void registerReflection(Method... methods) {
        runWithIgnoreLinkageError(() -> RuntimeReflection.register(methods));
        if (!debug) return;
        StringBuilder s = new StringBuilder();
        for (int i = 0, methodsLength = methods.length; i < methodsLength; i++) {
            Method method = methods[i];
            s.append(method.toString());
            if (i != methodsLength - 1) s.append(",");
        }
        System.out.println("registering reflect method " + s);
    }

    public void registerReflection(Constructor<?>... constructors) {
        runWithIgnoreLinkageError(() -> RuntimeReflection.register(constructors));
        if (!debug) return;
        StringBuilder s = new StringBuilder();
        for (int i = 0, constructorsLength = constructors.length; i < constructorsLength; i++) {
            Constructor<?> constructor = constructors[i];
            s.append(constructor.toString());
            if (i != constructorsLength - 1) s.append(",");
        }
        System.out.println("registering reflect constructor " + s);
    }

    public void registerReflection(Field... fields) {
        runWithIgnoreLinkageError(() -> RuntimeReflection.register(fields));
        if (!debug) return;
        StringBuilder s = new StringBuilder();
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++) {
            Field field = fields[i];
            s.append(field.toString());
            if (i != fieldsLength - 1) s.append(",");
        }
        System.out.println("registering reflect field " + s);
    }

    public void registerReflectionConstructors(Class<?>... classes) {
        for (Class<?> c : classes) {
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredConstructors()));
            if (debug) System.out.println("registering reflect constructors " + c.getName());
        }
    }

    public void registerReflectionIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredConstructors()));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredMethods()));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredFields()));
            if (debug) System.out.println("registering reflect " + c.getName());
        }
    }

    public void registerReflectionConstructorsIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c));
            runWithIgnoreLinkageError(() -> RuntimeReflection.register(c.getDeclaredConstructors()));
            if (debug) System.out.println("registering reflect constructors " + c.getName());
        }
    }

    public void registerReflectionMethods(Class<?> c, String... methods) {
        runWithIgnoreLinkageError(() -> RuntimeReflection.register(c));
        registerReflection(collectMethods(c, methods));
        if (!debug) return;
        System.out.println("registering reflect " + c.getName());
    }

    public void registerReflectionFields(Class<?> c, String... fields) {
        runWithIgnoreLinkageError(() -> RuntimeReflection.register(c));
        registerReflection(collectFields(c, fields));
        if (!debug) return;
        System.out.println("registering reflect " + c.getName());
    }

    public void registerReflectionMethods(String className, String... methods) {
        Class<?> c = loadClass(className);
        if (c == null) return;
        registerReflectionMethods(c, methods);
    }

    public void registerReflectionFields(String className, String... fields) {
        Class<?> c = loadClass(className);
        if (c == null) return;
        registerReflectionFields(c, fields);
    }

    public void registerJni(Class<?>... classes) {
        for (Class<?> c : classes) {
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c));
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c.getDeclaredConstructors()));
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c.getDeclaredMethods()));
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c.getDeclaredFields()));
            if (debug) System.out.println("registering jni " + c.getName());
        }
    }

    public void registerJni(Method... methods) {
        runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(methods));
        if (!debug) return;
        StringBuilder s = new StringBuilder();
        for (int i = 0, methodsLength = methods.length; i < methodsLength; i++) {
            Method method = methods[i];
            s.append(method.toString());
            if (i != methodsLength - 1) s.append(",");
        }
        System.out.println("registering jni method " + s);
    }

    public void registerJni(Constructor<?>... constructors) {
        runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(constructors));
        if (!debug) return;
        StringBuilder s = new StringBuilder();
        for (int i = 0, constructorsLength = constructors.length; i < constructorsLength; i++) {
            Constructor<?> constructor = constructors[i];
            s.append(constructor.toString());
            if (i != constructorsLength - 1) s.append(",");
        }
        System.out.println("registering jni constructor " + s);
    }

    public void registerJni(Field... fields) {
        runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(fields));
        if (!debug) return;
        StringBuilder s = new StringBuilder();
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++) {
            Field field = fields[i];
            s.append(field.toString());
            if (i != fieldsLength - 1) s.append(",");
        }
        System.out.println("registering jni field " + s);
    }

    public void registerJniMethods(Class<?> c, String... methods) {
        runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c));
        registerJni(collectMethods(c, methods));
        if (!debug) return;
        System.out.println("registering jni " + c.getName());
    }

    public void registerJniFields(Class<?> c, String... fields) {
        runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c));
        registerJni(collectFields(c, fields));
        if (!debug) return;
        System.out.println("registering jni " + c.getName());
    }

    public void registerJniMethods(String className, String... methods) {
        Class<?> c = loadClass(className);
        if (c == null) return;
        registerJniMethods(c, methods);
    }

    public void registerJniFields(String className, String... fields) {
        Class<?> c = loadClass(className);
        if (c == null) return;
        registerJniFields(c, fields);
    }

    public void registerJniIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c));
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c.getDeclaredConstructors()));
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c.getDeclaredMethods()));
            runWithIgnoreLinkageError(() -> RuntimeJNIAccess.register(c.getDeclaredFields()));
            if (debug) System.out.println("registering jni " + c.getName());
        }
    }

    public void registerSystemProperty(String key, String value) {
        RuntimeSystemProperties.register(key, value);
        if (debug) System.out.println("set system properties " + key + "=" + value);
    }

    public void setOption(String optionName, String value) {
        RuntimeOptions.set(optionName, value);
        System.out.println("set options " + optionName + "=" + value);
    }

    public void registerResource(Class<?> c, String... resources) {
        Module module = c.getModule();
        registerResource(module, resources);
    }

    public void registerResource(Module module, String... resources) {
        for (String resource : resources) {
            RuntimeResourceAccess.addResource(module, resource);
            if (debug) System.out.println("registering module " + module.getName() + " resource " + resource);
        }
    }

    public void registerResourceBundle(Class<?> c, String beanName, Locale... locales) {
        Module module = c.getModule();
        RuntimeResourceAccess.addResourceBundle(module, beanName, locales);
        if (debug)
            System.out.println("registering resourceBundle module " + module.getName() + " beanName " + beanName + " locales " + Arrays.toString(locales));
    }

//    /**
//     * 需要{@code --add-exports org.graalvm.nativeimage.impl}才能使用
//     * <p>详见：<a href="https://github.com/oracle/graal/issues/5013">我们不希望将该 API 设为公共，因为它违反了本机映像配置元数据中资源包含的可组合性</a></p>
//     * @param resources
//     */
//    public void ignoreResources(String... resources) {
//        for (String resource : resources) {
//            RuntimeResourceSupport.singleton().ignoreResources(ConfigurationCondition.alwaysTrue(), resource);
//            if (debug) System.out.println("ignore resource " + resource);
//        }
//    }

//    /**
//     * 需要{@code --add-exports org.graalvm.nativeimage.impl}才能使用
//     * <p>详见：<a href="https://github.com/oracle/graal/issues/5013">我们不希望将该 API 设为公共，因为它违反了本机映像配置元数据中资源包含的可组合性</a></p>
//     * @param resources
//     */
//    public void ignoreResources(ConfigurationCondition condition,String... resources) {
//        for (String resource : resources) {
//            RuntimeResourceSupport.singleton().ignoreResources(condition, resource);
//        }
//    }

    public void registerSerialization(Class<?>... classes) {
        for (Class<?> c : classes) {
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeSerialization.register(c);
            if (debug) System.out.println("registering serializable " + c.getName());
        }
    }

    public void registerSerializationIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeSerialization.register(c);
            if (debug) System.out.println("registering serializable " + c.getName());
        }
    }

    public void registerSerializationLambdaCapturingClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            for (Method declaredMethod : c.getDeclaredMethods()) {
                if (declaredMethod.getName().contains("$deserializeLambda$")) {
                    RuntimeSerialization.registerLambdaCapturingClass(c);
                    if (debug) System.out.println("registering serializationLambdaCapturing " + c.getName());
                    break;
                }
            }
        }
    }

    public void registerSerializationIncludingAssociatedClasses(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerIncludingAssociatedClasses(c);
            if (debug) System.out.println("registering serializationIncludingAssociated " + c.getName());
        }
    }

    public void registerSerializationProxyClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerProxyClass(c);
            if (debug) System.out.println("registering serializationProxy " + c.getName());
        }
    }

    public void registerProxyIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeProxyCreation.register(c);
            if (debug) System.out.println("registering proxy " + c.getName());
        }
    }

}
