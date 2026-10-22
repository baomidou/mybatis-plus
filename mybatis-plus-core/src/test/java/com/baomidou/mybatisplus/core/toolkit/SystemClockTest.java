/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 钉住 {@link SystemClock} 缓存时钟与系统时间的一致性及 {@link SystemClock#nowDate()} 格式。
 *
 * @author TaoYu
 */
class SystemClockTest {

    private static final long TOLERANCE_MILLIS = 5_000;

    @Test
    void nowStaysCloseToSystemTime() {
        long before = System.currentTimeMillis();
        long now = SystemClock.now();
        long after = System.currentTimeMillis();
        assertThat(now).isBetween(before - TOLERANCE_MILLIS, after + TOLERANCE_MILLIS);
    }

    @Test
    void nowDateRendersATimestampThatRoundTrips() {
        String nowDate = SystemClock.nowDate();
        assertThat(nowDate).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1,9}");
        long parsed = Timestamp.valueOf(nowDate).getTime();
        assertThat(Duration.between(Instant.ofEpochMilli(parsed), Instant.now()).abs().toMillis())
                .isLessThan(TOLERANCE_MILLIS);
    }
}
