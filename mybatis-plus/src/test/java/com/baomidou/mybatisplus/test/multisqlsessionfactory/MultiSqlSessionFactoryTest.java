package com.baomidou.mybatisplus.test.multisqlsessionfactory;

import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.entity.AEntity;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.service.AEntityService;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.entity.BEntity;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.service.BEntityService;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
@Tag("integration")
public class MultiSqlSessionFactoryTest {

    @Autowired
    private AEntityService aEntityService;

    @Autowired
    private BEntityService bEntityService;

    @Autowired
    private DataSource dataSourceA;

    @Autowired
    private DataSource dataSourceB;

    @Test
    void shouldOperateWithMultipleDataSources() throws Exception {
        new SqlRunner(dataSourceA.getConnection()).run("""
                   CREATE TABLE IF NOT EXISTS  t_entity_a (
                                           	id BIGINT NOT NULL AUTO_INCREMENT,
                                           	name VARCHAR(30) NULL DEFAULT NULL ,
                                           	PRIMARY KEY (id)
                                           );
                """);

        new SqlRunner(dataSourceB.getConnection()).run("""
                   CREATE TABLE IF NOT EXISTS  t_entity_b (
                                           	id BIGINT NOT NULL AUTO_INCREMENT,
                                           	name VARCHAR(30) NULL DEFAULT NULL ,
                                           	PRIMARY KEY (id)
                                           );
                """);
        assertThat(aEntityService.count()).isEqualTo(0L);
        assertThat(bEntityService.count()).isEqualTo(0L);
        aEntityService.shouldSaveBatch(List.of(new AEntity("test1"), new AEntity("test2")));
        assertThat(aEntityService.count()).isEqualTo(2L);
        bEntityService.shouldSaveBatch(List.of(new BEntity("test1"), new BEntity("test2"), new BEntity("test3")));
        assertThat(bEntityService.count()).isEqualTo(3L);

    }

}
