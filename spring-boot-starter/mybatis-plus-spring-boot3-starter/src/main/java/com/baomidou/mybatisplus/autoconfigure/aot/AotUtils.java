package com.baomidou.mybatisplus.autoconfigure.aot;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;

import java.io.Serializable;
import java.util.Collection;

/**
 * 基于spring的aot处理操作简化工具
 *
 * @author xiaochen
 * @since 2025/5/27
 */
class AotUtils extends CollectUtils {

    private final RuntimeHints hints;

    private final ClassLoader classLoader;

    public static AotUtils newInstance(RuntimeHints hints, ClassLoader classLoader) {
        return new AotUtils(hints, classLoader);
    }

    public AotUtils(RuntimeHints hints, ClassLoader classLoader) {
        super(classLoader);
        this.hints = hints;
        this.classLoader = classLoader;
    }

    public RuntimeHints hints() {
        return hints;
    }

    public ClassLoader classLoader() {
        return classLoader;
    }

    public static final MemberCategory[] defaultMemberCategory = new MemberCategory[]{MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.DECLARED_CLASSES, MemberCategory.UNSAFE_ALLOCATED};

    public void registerPattern(String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(resource);
            if (debug) System.out.println("include resource " + resource);
        }
    }

    public void registerPattern(TypeReference typeReference, String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(builder -> builder.includes(typeReference, resource));
            if (debug) System.out.println("include reachableType " + typeReference.getName() + " resource " + resource);
        }
    }

    public void excludePattern(String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(builder -> builder.excludes(resource));
            if (debug) System.out.println("exclude resource " + resource);
        }
    }

    public void excludePattern(TypeReference typeReference, String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(builder -> builder.excludes(typeReference, resource));
            if (debug) System.out.println("exclude reachableType " + typeReference.getName() + " resource " + resource);
        }
    }

    public void registerReflectionIfPresent(MemberCategory[] memberCategories, String... classes) {
        for (String clazz : classes) {
            try {
                if (isPresent(clazz)) {
                    hints.reflection().registerType(classLoader.loadClass(clazz), memberCategories);
                    if (debug) System.out.println("registering reflect " + clazz);
                }
            } catch (LinkageError | ClassNotFoundException ignored) {
            }
        }
    }

    public void registerReflectionIfPresent(String... classes) {
        registerReflectionIfPresent(defaultMemberCategory, classes);
    }

    public void registerReflection(MemberCategory[] memberCategories, Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            try {
                hints.reflection().registerType(clazz, memberCategories);
                if (debug) System.out.println("registering reflect " + clazz.getName());
            } catch (LinkageError e) {
                if (debug)
                    System.err.println("Unable to load class: " + clazz.getName() + ", error: " + e.getMessage());
            }
        }
    }

    public void registerReflection(MemberCategory[] memberCategories, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            try {
                hints.reflection().registerType(clazz, memberCategories);
                if (debug) System.out.println("registering reflect " + clazz.getName());
            } catch (LinkageError e) {
                if (debug)
                    System.err.println("Unable to load class: " + clazz.getName() + ", error: " + e.getMessage());
            }
        }
    }

    public void registerReflection(Class<?>... classes) {
        registerReflection(defaultMemberCategory, classes);
    }

    public void registerReflection(Collection<Class<?>> classes) {
        registerReflection(defaultMemberCategory, classes);
    }

    public void registerReflection(Collection<Class<?>> classes, MemberCategory memberCategory) {
        registerReflection(new MemberCategory[]{memberCategory}, classes);
    }

    public void registerJni(MemberCategory[] memberCategories, Collection<Class<?>> classes) {
        for (Class<?> c : classes) {
            hints.jni().registerType(c, memberCategories);
            if (debug) System.out.println("registering jni " + c.getName());
        }
    }

    public void registerJni(MemberCategory[] memberCategories, Class<?>... classes) {
        for (Class<?> c : classes) {
            hints.jni().registerType(c, memberCategories);
            if (debug) System.out.println("registering jni " + c.getName());
        }
    }

    public void registerJni(Class<?>... classes) {
        registerJni(defaultMemberCategory, classes);
    }

    public void registerJni(Collection<Class<?>> classes) {
        registerJni(defaultMemberCategory, classes);
    }

    public void registerJniIfPresent(String... classes) {
        registerJniIfPresent(defaultMemberCategory, classes);
    }

    public void registerJniIfPresent(MemberCategory[] memberCategory, String... classes) {
        for (String c : classes) {
            hints.jni().registerTypeIfPresent(classLoader, c, memberCategory);
            if (debug) System.out.println("registering jni " + c);
        }
    }

    @SafeVarargs
    public final void registerSerializable(Class<? extends Serializable>... classes) {
        for (Class<? extends Serializable> c : classes) {
            if (c.getCanonicalName() == null) continue;
            hints.serialization().registerType(c);
            if (debug) System.out.println("registering serializable " + c.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public void registerSerializable(Collection<Class<?>> classes) {
        for (Class<?> c : classes) {
            if (!Serializable.class.isAssignableFrom(c) || c.getCanonicalName() == null) continue;
            hints.serialization().registerType((Class<? extends Serializable>) c);
            if (debug) System.out.println("registering serializable " + c.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public void registerSerializableIfPresent(String... classes) {
        for (String cs : classes) {
            try {
                Class<?> c = classLoader.loadClass(cs);
                if (!Serializable.class.isAssignableFrom(c) || c.getCanonicalName() == null) continue;
                hints.serialization().registerType((Class<? extends Serializable>) c);
                if (debug) System.out.println("registering serializable " + c);
            } catch (ClassNotFoundException ignored) {
            }
        }
    }

}
