/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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

import com.baomidou.mybatisplus.core.toolkit.Sequence;

import java.net.InetAddress;

/**
 * 默认生成器
 *
 * @author  nieqiuqiu
 * @since 2019-10-15
 * @since 3.3.0
 */
public class DefaultIdentifierGenerator implements IdentifierGenerator {

    private final Sequence sequence;

    /**
     * @see #getInstance()
     * @deprecated 3.5.3.2 共享默认单例
     */
    @Deprecated
    public DefaultIdentifierGenerator() {
        this.sequence = new Sequence(null);
    }

    public DefaultIdentifierGenerator(InetAddress inetAddress) {
        this.sequence = new Sequence(inetAddress);
    }

    public DefaultIdentifierGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public DefaultIdentifierGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long nextId(Object entity) {
        return sequence.nextId();
    }

    public static DefaultIdentifierGenerator getInstance() {
        return DefaultInstance.INSTANCE;
    }

    private static class DefaultInstance {

        public static final DefaultIdentifierGenerator INSTANCE = new DefaultIdentifierGenerator();

    }

}
