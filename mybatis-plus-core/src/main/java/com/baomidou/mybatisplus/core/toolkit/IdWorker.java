/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 高效GUID产生算法(sequence),基于Snowflake实现64位自增ID算法。
 * <p>优化开源项目 https://gitee.com/yu120/sequence</p>
 *
 * @author hubin
 * @since 2016-08-01
 */
public class IdWorker {

    /**
     * 主机和进程的机器码
     */
    private static IdGenerator ID_GENERATOR = new DefaultIdGenerator();

    /**
     * 毫秒格式化时间
     */
    public static final DateTimeFormatter MILLISECOND = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static long getId() {
        return ID_GENERATOR.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(ID_GENERATOR.nextId());
    }

    /**
     * 格式化的毫秒时间
     */
    public static String getMillisecond() {
        return LocalDateTime.now().format(MILLISECOND);
    }

    /**
     * 时间 ID = Time + ID
     * <p>例如：可用于商品订单 ID</p>
     */
    public static String getTimeId() {
        return getMillisecond() + getId();
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param dataCenterId 序列号
     */
    public static void initSequence(long workerId, long dataCenterId) {
        ID_GENERATOR = new DefaultIdGenerator(workerId, dataCenterId);
    }

    /**
     * 自定义id 生成方式
     *
     * @param idGenerator id 生成器
     */
    public static void setIdGenerator(IdGenerator idGenerator) {
        ID_GENERATOR = idGenerator;
    }

    /**
     * 使用ThreadLocalRandom获取UUID获取更优的效果 去掉"-"
     */
    public static String get32UUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace(StringPool.DASH, StringPool.EMPTY);
    }

}
