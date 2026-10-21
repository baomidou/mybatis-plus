package com.baomidou.mybatisplus.core.incrementer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * 构造 {@link ImadcnIdentifierGenerator} 就要连 ZooKeeper 注册 workId，本机没有 2181 直接抛异常， 只能等 external-db 分层后再启用。<br>
 * 断言还是空的，跑起来之前得先补上。
 *
 * @author miemie
 * @since 2020-08-11
 */
@Disabled("需要本机 ZooKeeper localhost:2181，尚未进入 external-db 分层")
class ImadcnIdentifierGeneratorTest {

    private static ImadcnIdentifierGenerator generator;

    @BeforeAll
    static void init() {
        generator = new ImadcnIdentifierGenerator("localhost:2181");
    }

    @AfterAll
    static void close() throws Exception {
        generator.close();
    }

    @Test
    void nextId() {
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.nextId(null));
            System.out.println(generator.nextUUID(null));
        }
    }
}
