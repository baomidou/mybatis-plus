package com.baomidou.mybatisplus.autoconfigure.aot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类和资源文件的扫描搜集工具类
 *
 * @author xiaochen
 * @since 2025/9/17
 */
class CollectUtils {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static final String EMPTY_STRING = "";

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

    public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

    public static final boolean debug = Boolean.getBoolean("aot.debug");

    private final ClassLoader classLoader;

    public CollectUtils(ClassLoader classLoader) {
        this.classLoader = classLoader;
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

    public Set<Class<?>> collectClass(String... packages) {
        return collectClass(null, packages);
    }

    public Set<Class<?>> collectClass(Collection<String> packages) {
        return collectClass(null, packages.toArray(EMPTY_STRING_ARRAY));
    }

    public Set<Class<?>> collectClass(Predicate<Class<?>> predicate, String... packages) {
        Set<Class<?>> classes = new HashSet<>();
        for (String basePackage : packages) {
            try {
                // 过滤掉Spring相关的生成类
                // 详见：org.springframework.aot.generate.ClassNameGenerator
                Set<String> classNames = findClassNames(basePackage, name -> !name.contains("__"));
                for (String className : classNames) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (predicate == null || predicate.test(clazz)) classes.add(clazz);
                    } catch (ClassNotFoundException | LinkageError e) {
                        // 忽略无法加载的类
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    public Set<String> collectClassNames(String... packages) {
        Set<String> classNames = new HashSet<>();
        for (String basePackage : packages) {
            try {
                Set<String> names = findClassNames(basePackage);
                for (String className : names) {
                    // 过滤掉Spring相关的生成类
                    // 详见：org.springframework.aot.generate.ClassNameGenerator
                    if (className.contains("__")) continue;
                    classNames.add(className);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classNames;
    }

    public Set<Class<?>> findSpringBootApplicationClasses() throws IOException {
        return findClasses(c -> {
            for (Annotation annotation : c.getAnnotations()) {
                if (annotation.annotationType().getName().equals("org.springframework.boot.autoconfigure.SpringBootApplication")) {
                    return true;
                }
            }
            return false;
        });
    }

    public Set<Class<?>> findClasses(Predicate<Class<?>> predicate) throws IOException {
        Set<Class<?>> result = new HashSet<>();
        Set<String> allClassNames = findClassNames();

        for (String className : allClassNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (predicate.test(clazz)) {
                    result.add(clazz);
                }
            } catch (ClassNotFoundException | LinkageError e) {
                // 忽略无法加载的类
            }
        }
        return result;
    }

    public void findClassesInDirectory(File directory, String packageName, Set<String> classNames) {
        findClassesInDirectory(directory, packageName, classNames, name -> true);
    }

    public void findClassesInDirectory(File directory, String packageName, Set<String> classNames, Predicate<String> filter) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String filename = file.getName();
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? filename : (packageName + "." + filename);
                findClassesInDirectory(file, newPackage, classNames, filter);
            } else if (filename.endsWith(".class")) {
                String className = filename.substring(0, filename.length() - 6);
                String fullClassName = packageName.isEmpty() ? className : packageName + '.' + className;
                if (filter.test(fullClassName)) classNames.add(fullClassName);
            }
        }
    }

    public void findResourcesInDirectory(File directory, String parentPath, Set<String> resources) {
        findResourcesInDirectory(directory, parentPath, resources, name -> true);
    }

    public void findResourcesInDirectory(File directory, String parentPath, Set<String> resources, Predicate<String> filter) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            String currentPath = parentPath.isEmpty() ? fileName : parentPath + "/" + fileName;

            if (file.isDirectory()) {
                findResourcesInDirectory(file, currentPath, resources, filter);
            } else {
                if (!fileName.endsWith(".class")) {
                    if (filter.test(currentPath)) resources.add(currentPath);
                }
            }
        }
    }

    /**
     * 查找指定包下的类
     *
     * @param packageName
     * @return
     * @throws IOException
     */
    public Set<String> findClassNames(String packageName) throws IOException {
        return findClassNames(packageName, name -> true);
    }

    /**
     * 查找指定包下的类
     *
     * @param packageName
     * @return
     * @throws IOException
     */
    public Set<String> findClassNames(String packageName, Predicate<String> filter) throws IOException {
        Set<String> classNames = new HashSet<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if ("file".equals(protocol)) {
                try {
                    File directory = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                    if (directory.exists()) {
                        findClassesInDirectory(directory, packageName, classNames, filter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("jar".equals(protocol)) {
                String jarPath = resource.getFile().substring(5, resource.getFile().indexOf('!'));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.endsWith(".class") && !entry.isDirectory()) {
                            String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                            if (filter.test(className)) classNames.add(className);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return classNames;
    }

    /**
     * 查找根目录的类
     *
     * @return
     * @throws IOException
     */
    public Set<String> findClassNames() throws IOException {
        return findClassNames("");
    }

    /**
     * 查找指定包下的资源
     *
     * @param packageName
     * @return
     * @throws IOException
     */
    public Set<String> findResources(String packageName) throws IOException {
        return findResources(packageName, (name) -> true);
    }

    /**
     * 查找指定包下的资源
     *
     * @param packageName
     * @return
     * @throws IOException
     */
    public Set<String> findResources(String packageName, Predicate<String> filter) throws IOException {
        Set<String> resources = new HashSet<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> roots = classLoader.getResources(path);
        while (roots.hasMoreElements()) {
            URL root = roots.nextElement();
            String protocol = root.getProtocol();

            if ("file".equals(protocol)) {
                try {
                    String decodedPath = URLDecoder.decode(root.getFile(), StandardCharsets.UTF_8);
                    File rootDir = new File(decodedPath);

                    if (rootDir.exists() && rootDir.isDirectory()) {
                        findResourcesInDirectory(rootDir, "", resources, filter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("jar".equals(protocol)) {
                String jarPath = root.getFile().substring(5, root.getFile().indexOf('!'));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (!entryName.endsWith(".class") && !entry.isDirectory()) {
                            if (filter.test(entryName)) resources.add(entryName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resources;
    }

    /**
     * 查找根目录的资源（用户的resources资源目录）
     *
     * @return
     * @throws IOException
     */
    public Set<String> findResources() throws IOException {
        return findResources("");
    }

    /**
     * 是否是启动类
     *
     * @param c
     * @return
     */
    public boolean isMainClass(Class<?> c) {
        for (Method method : c.getMethods()) {
            if (method.getName().equals("main") && method.getReturnType() == void.class && Modifier.isStatic(method.getModifiers())) {
                return true;
            }
        }
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals("main") && m.getReturnType() == void.class && !Modifier.isPrivate(m.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取启动类
     *
     * @return
     * @throws IOException
     */
    public Set<Class<?>> findMainClasses() throws IOException {
        Set<Class<?>> classes = findClasses(this::isMainClass);
        classes.addAll(findMainClassesFromNativeImageProperties());
        return classes;
    }

    /**
     * 从native-image.properties中获取启动类
     *
     * @return
     * @throws IOException
     */
    public Set<Class<?>> findMainClassesFromNativeImageProperties() throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        for (String resource : findResources()) {
            if (resource.endsWith("native-image.properties")) {
                BufferedInputStream inputStream = (BufferedInputStream) classLoader.getResource(resource).getContent();
                String content = new String(inputStream.readAllBytes());
                String className = parseHClassValue(content);
                if (className != null) {
                    Class<?> c = loadClass(className);
                    if (c != null && isMainClass(c)) classes.add(c);
                }
            }
        }
        return classes;
    }

    private static final Pattern H_CLASS_PATTERN = Pattern.compile("-H:Class\\s*=\\s*([\\w.]+)");

    /**
     * 从参数字符串中解析出-H:Class的值
     *
     * @param argsStr 包含各种参数的字符串
     * @return 解析到的类名，如果未找到则返回null
     */
    private String parseHClassValue(String argsStr) {
        if (argsStr == null || argsStr.isBlank()) return null;
        Matcher matcher = H_CLASS_PATTERN.matcher(argsStr);
        if (matcher.find()) return matcher.group(1);
        return null;
    }


    /**
     * 获取启动类所在包名
     *
     * @return
     * @throws IOException
     */
    public Set<String> findMainPackages() throws IOException {
        Set<String> set = new HashSet<>();
        for (Class<?> aClass : findMainClasses()) {
            String packageName = aClass.getPackageName();
            set.add(packageName);
        }
        return set;
    }

    public Method[] collectMethods(Class<?> c, String... name) {
        return collectMethods(c.getDeclaredMethods(), name);
    }

    public Method[] collectMethods(Method[] m, String... name) {
        if (name == null || name.length == 0) return m;
        return collectMethods(m, method -> {
            for (String s : name) {
                if (method.getName().equals(s)) {
                    return true;
                }
            }
            return false;
        });
    }

    public Method[] collectMethods(Method[] m, Predicate<Method> predicate) {
        if (predicate == null) return m;
        Set<Method> methods = new HashSet<>();
        for (Method method : m) {
            if (predicate.test(method)) methods.add(method);
        }
        return methods.toArray(EMPTY_METHOD_ARRAY);
    }

    public Field[] collectFields(Class<?> c, String... name) {
        return collectFields(c.getDeclaredFields(), name);
    }

    public Field[] collectFields(Field[] f, String... name) {
        if (name == null || name.length == 0) return f;
        List<String> names = new ArrayList<>(Arrays.asList(name));
        return collectFields(f, field -> {
            for (Iterator<String> iterator = names.iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                if (field.getName().equals(s)) {
                    iterator.remove();
                    return true;
                }
            }
            return false;
        });
    }

    public Field[] collectFields(Field[] f, Predicate<Field> predicate) {
        if (predicate == null) return f;
        Set<Field> fields = new HashSet<>();
        for (Field field : f) {
            if (predicate.test(field)) fields.add(field);
        }
        return fields.toArray(EMPTY_FIELD_ARRAY);
    }

    public void runWithIgnoreLinkageError(Runnable runnable) {
        try {
            runnable.run();
        } catch (LinkageError ignored) {
        }
    }

}
