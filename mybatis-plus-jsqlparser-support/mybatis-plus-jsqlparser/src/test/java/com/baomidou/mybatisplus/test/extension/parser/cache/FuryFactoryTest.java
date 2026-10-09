package com.baomidou.mybatisplus.test.extension.parser.cache;

import com.baomidou.mybatisplus.extension.parser.cache.FuryFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.fury.ThreadSafeFury;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuryFactoryTest {

    @Test
    void shouldRegisterAllSerializableClassesAndEnums() throws ReflectiveOperationException {
        Set<String> expected;
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages("net.sf.jsqlparser").scan()) {
            expected = scanResult.getAllClasses().stream()
                // Match the FST scan and include enums inheriting Serializable from java.lang.Enum.
                .filter(classInfo -> !classInfo.isInterface()
                    && (classInfo.isEnum() || classInfo.implementsInterface(Serializable.class)))
                .sorted(Comparator.comparing(ClassInfo::isAbstract).thenComparing(ClassInfo::getName))
                .map(ClassInfo::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        assertFalse(expected.isEmpty(), "No serializable JSqlParser classes found");

        // Inspect a fresh factory without exposing its serializer in the production API.
        Field field = FuryFactory.class.getDeclaredField("FURY");
        field.setAccessible(true);
        ThreadSafeFury fury = (ThreadSafeFury) field.get(new FuryFactory());
        Set<String> actual = fury.execute(instance -> instance.getClassResolver().getRegisteredClasses().stream()
            .map(Class::getName)
            // Fury also registers built-in classes; only compare JSqlParser classes.
            .filter(name -> name.startsWith("net.sf.jsqlparser."))
            .collect(Collectors.toSet()));

        // Sorting only makes the generated replacement list stable; registration order is not checked.
        if (!expected.equals(actual)) {
            String registrations = expected.stream()
                .map(name -> "FURY.register(" + name.replace('$', '.') + ".class);")
                .collect(Collectors.joining("\n"));
            System.out.println("Complete constructor registrations:\n" + registrations);
        }
        assertTrue(expected.equals(actual), () -> "FuryFactory registrations differ from the JSqlParser dependency"
            + "\nMissing: " + expected.stream().filter(name -> !actual.contains(name)).collect(Collectors.toList())
            + "\nUnexpected: " + actual.stream().filter(name -> !expected.contains(name)).sorted().collect(Collectors.toList()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SELECT TRUE FROM t",
        "SELECT sum(id) OVER (PARTITION BY name) FROM t",
        "DELETE FROM t WHERE id = 1",
        "FROM t |> SELECT id"
    })
    void shouldRoundTripStatements(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        FuryFactory factory = new FuryFactory();
        Object restored = factory.deserialize(factory.serialize(statement));
        assertEquals(statement.getClass(), restored.getClass());
        assertEquals(statement.toString(), restored.toString());
    }
}
