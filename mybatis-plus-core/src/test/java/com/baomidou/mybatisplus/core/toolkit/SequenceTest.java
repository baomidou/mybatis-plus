package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2022-10-17
 */
class SequenceTest {

    @Test
    void nextId() {
        Sequence sequence = new Sequence(null);
        long id = sequence.nextId();
        LocalDateTime now = LocalDateTime.now();
        System.out.println(sequence.nextId() + "---" + now);

        long timestamp = Sequence.parseIdTimestamp(id);
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime time = LocalDateTime.ofInstant(instant, zone);
        System.out.println(timestamp + "---" + time);
        assertThat(now).isAfter(time);
    }
}
