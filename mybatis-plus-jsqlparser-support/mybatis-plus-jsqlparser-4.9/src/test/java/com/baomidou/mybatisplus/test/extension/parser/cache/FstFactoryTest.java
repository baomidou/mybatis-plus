package com.baomidou.mybatisplus.test.extension.parser.cache;

import com.baomidou.mybatisplus.extension.parser.cache.FstFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Test;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzNameRegistry;
import org.nustaq.serialization.FSTConfiguration;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author miemie
 * @since 2023-08-06
 */
class FstFactoryTest {

    @Test
    void shouldRegisterAllSerializableClasses() {
        Set<String> expected;
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages("net.sf.jsqlparser").scan()) {
            expected = scanResult.getAllClasses().stream()
                .filter(classInfo -> !classInfo.isInterface() && classInfo.implementsInterface(Serializable.class))
                .sorted(Comparator.comparing(ClassInfo::isAbstract).thenComparing(ClassInfo::getName))
                .map(ClassInfo::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        assertFalse(expected.isEmpty(), "No serializable JSqlParser classes found");

        // Inspect a fresh factory before serialization can dynamically register any classes.
        FSTConfiguration conf = new FstFactory().getConfig();
        FSTClazzNameRegistry registry = conf.getClassRegistry();
        Set<String> actual = new HashSet<>();
        for (int id = FSTClazzNameRegistry.LOWEST_CLZ_ID; ; id++) {
            FSTClazzInfo classInfo = registry.getClazzFromId(id);
            if (classInfo == null) {
                break;
            }
            String name = classInfo.getClazz().getName();
            // FST also registers built-in classes and arrays; only compare JSqlParser classes.
            if (name.startsWith("net.sf.jsqlparser.")) {
                actual.add(name);
            }
        }

        // Sorting only makes the generated replacement list stable; registration order is not checked.
        if (!expected.equals(actual)) {
            String registrations = expected.stream()
                .map(name -> "conf.registerClass(" + name.replace('$', '.') + ".class);")
                .collect(Collectors.joining("\n"));
            System.out.println("Complete constructor registrations:\n" + registrations);
        }
        assertTrue(expected.equals(actual), () -> "FstFactory registrations differ from the JSqlParser dependency"
            + "\nMissing: " + expected.stream().filter(name -> !actual.contains(name)).collect(Collectors.toList())
            + "\nUnexpected: " + actual.stream().filter(name -> !expected.contains(name)).sorted().collect(Collectors.toList()));
    }
}
