/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
