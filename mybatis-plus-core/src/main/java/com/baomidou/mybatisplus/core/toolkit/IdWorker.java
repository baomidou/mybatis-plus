/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 高效GUID产生算法(sequence),基于Snowflake实现64位自增ID算法。 <br>
 * 优化开源项目 http://git.oschina.net/yu120/sequence
 * </p>
 *
 * @author hubin
 * @since 2016-08-01
 */
public class IdWorker {

    /**
     * 主机和进程的机器码
     */
    private static Sequence WORKER = new Sequence();

    /**
     * 毫秒格式化时间
     */
    public static final DateTimeFormatter MILLISECOND = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static long getId() {
        return WORKER.nextId();
    }

    public static String getIdStr() {
        return String.valueOf(WORKER.nextId());
    }

    /**
     * <p>
     * 格式化的毫秒时间
     * </p>
     */
    public static String getMillisecond() {
        return LocalDateTime.now().format(MILLISECOND);
    }

    /**
     * <p>
     * 时间 ID = Time + ID
     * </p>
     * <p>
     * 例如：可用于商品订单 ID
     * </p>
     */
    public static String getTimeId() {
        return getMillisecond() + getId();
    }

    /**
     * <p>
     * 有参构造器
     * </p>
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public static void initSequence(long workerId, long datacenterId) {
        WORKER = new Sequence(workerId, datacenterId);
    }

    /**
     * <p>
     * 使用ThreadLocalRandom获取UUID获取更优的效果 去掉"-"
     * </p>
     */
    public static String get32UUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString().replace(StringPool.DASH, StringPool.EMPTY);
    }

}
