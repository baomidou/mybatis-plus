package com.baomidou.mybatisplus.test.extension.parser.cache;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author miemie
 * @since 2023-08-06
 */
class FstFactoryTest {

    @Test
    void clazz() {
        List<ClassInfo> list = new ArrayList<>();
        List<ClassInfo> absList = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages("net.sf.jsqlparser").scan()) {
            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                if (!classInfo.isInterface() && classInfo.implementsInterface(Serializable.class)) {
                    if (classInfo.isAbstract()) {
                        absList.add(classInfo);
                        continue;
                    }
                    list.add(classInfo);
                }
            }
        }
        list.forEach(i -> System.out.printf("conf.registerClass(%s.class);%n", i.getName().replace("$", ".")));
        System.out.println("↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓");
        absList.forEach(i -> System.out.printf("conf.registerClass(%s.class);%n", i.getName().replace("$", ".")));
    }
}
