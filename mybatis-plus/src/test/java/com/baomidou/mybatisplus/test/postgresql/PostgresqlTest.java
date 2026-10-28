package com.baomidou.mybatisplus.test.postgresql;

import com.baomidou.mybatisplus.test.postgresql.entity.Pgtable;
import com.baomidou.mybatisplus.test.postgresql.mapper.PgtableMappper;
import com.baomidou.mybatisplus.test.postgresql.service.IPgtableService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:postgresql/spring-test-postgresql.xml"})
@Tag("external-db")
public class PostgresqlTest {

    @Autowired
    private PgtableMappper mapper;
    @Autowired
    private IPgtableService pgtableService;

    @Test
    void shouldInsertAndQueryWithPostgresql() {
        Pgtable pgtable = Pgtable.builder().compName("test").age(10).build();
        assertThat(mapper.insert(pgtable) > 0).isTrue();

        List<Pgtable> pgtableList = mapper.selectList(null);
        assertThat(pgtableList).isNotNull();
        assertThat(pgtableList.size() > 0).isTrue();
    }

    @Test
    void shouldExecuteServiceTask() throws InterruptedException {
        // 测试线程
        pgtableService.shouldRunTask();
        // 主线程休眠
        Thread.sleep(10000);
    }
}
