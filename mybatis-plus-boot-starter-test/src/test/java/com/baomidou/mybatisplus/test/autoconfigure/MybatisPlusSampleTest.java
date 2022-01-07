package com.baomidou.mybatisplus.test.autoconfigure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-05-27
 */
//@MybatisPlusTest(properties = "spring.datasource.schema=classpath:schema.sql")
@MybatisPlusTest
class MybatisPlusSampleTest {

    @Autowired
    private SampleMapper sampleMapper;

    @Test
    void testInsert() {
        Sample sample = new Sample();
        sampleMapper.insert(sample);
        assertThat(sample.getId()).isNotNull();
    }

    @Test
    void testInsertZeroId() {
        Sample sample = new Sample();
        sample.setId(0L);
        sampleMapper.insert(sample);
        Assertions.assertTrue(sample.getId() != 0);
    }
}
