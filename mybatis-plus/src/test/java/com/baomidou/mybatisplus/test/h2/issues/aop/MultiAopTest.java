package com.baomidou.mybatisplus.test.h2.issues.aop;

import com.baomidou.mybatisplus.test.h2.issues.aop.entity.Demo;
import com.baomidou.mybatisplus.test.h2.issues.aop.service.IDemoService;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class, AopConfig1.class, AopConfig2.class})
public class MultiAopTest {

    @Autowired
    private IDemoService demoService;

    @Autowired
    private DataSource dataSource;

    @Test
    void test() throws SQLException {
        new SqlRunner(dataSource.getConnection()).run("""
                 CREATE TABLE IF NOT EXISTS demo (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(30) NULL DEFAULT NULL ,
                      PRIMARY KEY (id)
                 );
                """);
        // 两个切面叠加:mapper 依旧只是被代理,不影响调用
        assertThat(AopUtils.isAopProxy(demoService.getBaseMapper())).isTrue();
        long before = demoService.count();
        assertThat(demoService.save(new Demo())).isTrue();
        assertThat(demoService.saveBatch(List.of(new Demo(), new Demo()))).isTrue();
        List<BatchResult> batchResults = demoService.getBaseMapper()
                .insert(List.of(new Demo(), new Demo(), new Demo()));
        assertThat(batchResults).hasSize(1);
        assertThat(batchResults.getFirst().getUpdateCounts()).containsExactly(1, 1, 1);
        // 代理层层叠加也不能影响批量方法落库
        assertThat(demoService.count()).isEqualTo(before + 6);
    }

}
