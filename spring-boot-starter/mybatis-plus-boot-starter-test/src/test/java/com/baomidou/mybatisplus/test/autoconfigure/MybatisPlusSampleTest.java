package com.baomidou.mybatisplus.test.autoconfigure;

import com.baomidou.mybatisplus.test.autoconfigure.entity.Sample;
import com.baomidou.mybatisplus.test.autoconfigure.mapper.SampleMapper;
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
}
