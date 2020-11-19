package com.baomidou.mybatisplus.core.incrementer;

import com.imadcn.framework.idworker.config.ApplicationConfiguration;
import com.imadcn.framework.idworker.config.ZookeeperConfiguration;
import com.imadcn.framework.idworker.generator.CompressUUIDGenerator;
import com.imadcn.framework.idworker.generator.SnowflakeGenerator;
import com.imadcn.framework.idworker.register.zookeeper.ZookeeperWorkerRegister;
import com.imadcn.framework.idworker.registry.zookeeper.ZookeeperRegistryCenter;

import java.io.Closeable;
import java.io.IOException;

/**
 * 用 https://github.com/imadcn/idworker 的实现
 *
 * @author miemie
 * @since 3.4.0
 * @since 2020-08-11
 */
public class ImadcnIdentifierGenerator implements IdentifierGenerator, Closeable {

    private final SnowflakeGenerator idGenerator;
    private final CompressUUIDGenerator uuidGenerator = new CompressUUIDGenerator();

    public ImadcnIdentifierGenerator(String serverLists) {
        this(configuration(serverLists));
    }

    public ImadcnIdentifierGenerator(ZookeeperConfiguration zookeeperConfiguration) {
        this(zookeeperConfiguration, new ApplicationConfiguration());
    }

    public ImadcnIdentifierGenerator(ZookeeperConfiguration zookeeperConfiguration,
                                     ApplicationConfiguration applicationConfiguration) {
        ZookeeperRegistryCenter center = new ZookeeperRegistryCenter(zookeeperConfiguration);
        ZookeeperWorkerRegister register = new ZookeeperWorkerRegister(center, applicationConfiguration);
        idGenerator = new SnowflakeGenerator(register);
        idGenerator.init();
    }

    private static ZookeeperConfiguration configuration(String serverLists) {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration();
        zookeeperConfiguration.setServerLists(serverLists);
        return zookeeperConfiguration;
    }

    @Override
    public Number nextId(Object entity) {
        return idGenerator.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        return uuidGenerator.nextStringId();
    }

    @Override
    public void close() throws IOException {
        idGenerator.close();
    }
}
