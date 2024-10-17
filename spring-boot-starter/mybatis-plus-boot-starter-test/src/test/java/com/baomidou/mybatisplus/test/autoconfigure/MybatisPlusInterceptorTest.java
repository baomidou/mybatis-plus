package com.baomidou.mybatisplus.test.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.baomidou.mybatisplus.test.autoconfigure.entity.TestEntity;
import com.baomidou.mybatisplus.test.autoconfigure.mapper.TestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jam804
 * @since 2024-08-22
 */
//@MybatisPlusTest(properties = "spring.datasource.schema=classpath:schema.sql")
@MybatisPlusTest
public class MybatisPlusInterceptorTest {

    @Autowired
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @Autowired
    private TestMapper testMapper;

    @BeforeEach
    public void addInterceptor() {
        DataChangeRecorderInnerInterceptor dataChangeRecorderInnerInterceptor = new DataChangeRecorderInnerInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(dataChangeRecorderInnerInterceptor);
    }

    @Test
    void testDataChangeRecorderInnerInterceptor() {

        List<TestEntity> testList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            TestEntity test = new TestEntity();
            test.setId((long) i);
            test.setName("name" + i);
            testList.add(test);
        }

        Exception ex = null;
        try {
            testMapper.updateById(testList);
            testMapper.insert(testList);
            testMapper.deleteBatchIds(testList);
        } catch (Exception e) {
            ex = e;
        }
        assertThat(ex).isNull();
    }
}
