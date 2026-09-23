package com.baomidou.mybatisplus.core.incrementer;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 默认主键生成器单元测试
 *
 * @author hebulin
 * @since 2026-09-23
 */
class DefaultIdentifierGeneratorTest {

    /**
     * 自动推导机器标识的生成器不允许生成重复 ID
     * <p>对应 issues/7135: IdWorker 默认生成器与 SqlSessionFactory 构建时自动推导出的生成器
     * 都按本机网卡计算 workerId 与 datacenterId, 机器标识完全相同却各自持有独立的序列计数器,
     * 交错生成时必然出现重复 ID.</p>
     */
    @Test
    void autoDerivedGeneratorsMustNotGenerateDuplicateIds() throws Exception {
        // IdWorker 默认使用的生成器
        DefaultIdentifierGenerator idWorkerGenerator = DefaultIdentifierGenerator.getInstance();
        // SqlSessionFactory 构建时按本机网卡自动推导出的生成器
        DefaultIdentifierGenerator sessionFactoryGenerator = new DefaultIdentifierGenerator(InetAddress.getLocalHost());
        assertThat(collectIds(idWorkerGenerator, sessionFactoryGenerator)).hasSize(200_000);
    }

    /**
     * 显式指定相同机器标识的生成器不允许生成重复 ID
     */
    @Test
    void explicitSameMachineIdGeneratorsMustNotGenerateDuplicateIds() {
        DefaultIdentifierGenerator first = new DefaultIdentifierGenerator(1, 1);
        DefaultIdentifierGenerator second = new DefaultIdentifierGenerator(1, 1);
        assertThat(collectIds(first, second)).hasSize(200_000);
    }

    /**
     * 机器标识不同的生成器保持相互独立, 生成的 ID 不会重叠
     */
    @Test
    void differentMachineIdGeneratorsMustStayIndependent() {
        Set<Long> workerIdOne = collectIds(new DefaultIdentifierGenerator(1, 1));
        Set<Long> workerIdTwo = collectIds(new DefaultIdentifierGenerator(2, 2));
        assertThat(workerIdOne).hasSize(100_000);
        assertThat(workerIdTwo).hasSize(100_000);
        assertThat(workerIdOne).doesNotContainAnyElementsOf(workerIdTwo);
    }

    /**
     * 交错采集多个生成器生成的 ID
     *
     * @param generators 主键生成器
     * @return ID 集合
     */
    private static Set<Long> collectIds(DefaultIdentifierGenerator... generators) {
        final int count = 100_000;
        Set<Long> ids = new HashSet<>(count * generators.length);
        for (int i = 0; i < count; i++) {
            for (DefaultIdentifierGenerator generator : generators) {
                ids.add(generator.nextId(null));
            }
        }
        return ids;
    }
}
