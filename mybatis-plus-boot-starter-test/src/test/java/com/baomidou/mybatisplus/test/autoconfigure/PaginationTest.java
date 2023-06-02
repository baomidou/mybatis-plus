package com.baomidou.mybatisplus.test.autoconfigure;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
public class PaginationTest {
    @Autowired
    private SampleMapper sampleMapper;
    private Page<Sample> page = new Page<>(1, 3);

    /**
     * 手动配置的ount查询: sampleList_customCOUNT 方法
     */
    @Test
    void customPage() {
        // 使用
        page.setCountId("sampleList_customCOUNT");
        Page<Sample> samplePage = sampleMapper.sampleList(page,null);
        // 总页数
        assertThat(samplePage.getPages()).isEqualTo(4L);
        // 总行数
        assertThat(samplePage.getTotal()).isEqualTo(10L);

    }

    /**
     * 默认的Count查询: sampleList_COUNT 方法
     *
     */
    @Test
    void rulePage(){
        Page<Sample> samplePage = sampleMapper.sampleList(page,"A");
        // 总页数
        assertThat(samplePage.getPages()).isEqualTo(2L);
        // 总行数
        assertThat(samplePage.getTotal()).isEqualTo(5L);
    }

    @BeforeEach
    public void beforeAll(){
        for (int i = 1; i <= 10; i++) {
            Sample sample;
            if (i % 2  == 0){
                sample = new Sample((long) i,"A");
            }else {
                sample = new Sample((long) i,"B");
            }
            sampleMapper.insert(sample);
        }
    }
}
