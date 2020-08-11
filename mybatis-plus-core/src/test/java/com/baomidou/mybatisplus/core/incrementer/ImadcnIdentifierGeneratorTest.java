package com.baomidou.mybatisplus.core.incrementer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2020-08-11
 */
public class ImadcnIdentifierGeneratorTest {

    private static ImadcnIdentifierGenerator generator;

    @BeforeAll
    static void init() {
        generator = new ImadcnIdentifierGenerator("localhost:2181");
    }

    @AfterAll
    static void close() throws Exception {
        generator.destroy();
    }

    @Test
    void nextId() {
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.nextId(null));
            System.out.println(generator.nextUUID(null));
        }
    }
}
