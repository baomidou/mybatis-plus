package com.baomidou.mybatisplus.test.extension.parser.cache;

import com.baomidou.mybatisplus.extension.parser.cache.ForyFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.resolver.ClassResolver;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2026-10-09
 */
@Tag("unit")
class ForyFactoryTest {

    @Test
    void shouldRegisterAllSerializableClassesAndEnums() throws ReflectiveOperationException {
        Set<String> expected;
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages("net.sf.jsqlparser").scan()) {
            expected = scanResult.getAllClasses().stream()
                    // Match the FST scan and include enums inheriting Serializable from
                    // java.lang.Enum.
                    .filter(
                            classInfo -> !classInfo.isInterface()
                                    && (classInfo.isEnum() || classInfo.implementsInterface(Serializable.class)))
                    .map(ClassInfo::getName).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        assertThat(expected).as("No serializable JSqlParser classes found").isNotEmpty();

        // Inspect a fresh factory without exposing its serializer in the production
        // API.
        Field field = ForyFactory.class.getDeclaredField("FORY");
        field.setAccessible(true);
        ThreadSafeFory fory = (ThreadSafeFory) field.get(new ForyFactory());
        Set<String> actual = fory.execute(
                instance -> ((ClassResolver) instance.getTypeResolver()).getRegisteredClasses().stream()
                        .map(Class::getName)
                        // Fory also registers built-in classes; only compare JSqlParser classes.
                        .filter(name -> name.startsWith("net.sf.jsqlparser.")).collect(Collectors.toSet()));

        // Generate registrations in scan order; the assertion checks only class
        // membership.
        assertThat(actual).overridingErrorMessage(
                () -> "ForyFactory registrations differ from the JSqlParser dependency" + "\nMissing: "
                        + expected.stream().filter(name -> !actual.contains(name)).collect(Collectors.toList())
                        + "\nUnexpected: " + actual.stream().filter(name -> !expected.contains(name)).sorted()
                                .collect(Collectors.toList()))
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SELECT TRUE FROM t", "SELECT sum(id) OVER (PARTITION BY name) FROM t",
            "DELETE FROM t WHERE id = 1", "FROM t |> SELECT id"})
    void shouldRoundTripStatements(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        ForyFactory factory = new ForyFactory();
        Object restored = factory.deserialize(factory.serialize(statement));
        assertThat(restored.getClass()).isEqualTo(statement.getClass());
        assertThat(restored.toString()).isEqualTo(statement.toString());
    }

}
