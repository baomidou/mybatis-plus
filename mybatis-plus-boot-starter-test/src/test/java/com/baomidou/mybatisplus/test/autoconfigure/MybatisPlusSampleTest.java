package com.baomidou.mybatisplus.test.autoconfigure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
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
        sample.setId(1L);
        sample.setName("aaa");
        sampleMapper.insert(sample);
        assertThat(sample.getId()).isNotNull();
    }

    @Test
    void testLike() {
        testInsert();
        List<Sample> sampleList = sampleMapper.selectList(
            new LambdaQueryWrapper<Sample>().like(Sample::getName, "a_a"));
        assertThat(sampleList).isNotEmpty();
    }
}
