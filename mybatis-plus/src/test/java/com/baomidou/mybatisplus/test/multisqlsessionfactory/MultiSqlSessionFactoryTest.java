package com.baomidou.mybatisplus.test.multisqlsessionfactory;

import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.entity.AEntity;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.service.AEntityService;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.entity.BEntity;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.service.BEntityService;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author nieqiurong
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfig.class})
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
    void test() throws Exception {
        new SqlRunner(dataSourceA.getConnection()).run(
            """
                   CREATE TABLE IF NOT EXISTS  t_entity_a (
                                           	id BIGINT NOT NULL AUTO_INCREMENT,
                                           	name VARCHAR(30) NULL DEFAULT NULL ,
                                           	PRIMARY KEY (id)
                                           );
                """
        );

        new SqlRunner(dataSourceB.getConnection()).run(
            """
                   CREATE TABLE IF NOT EXISTS  t_entity_b (
                                           	id BIGINT NOT NULL AUTO_INCREMENT,
                                           	name VARCHAR(30) NULL DEFAULT NULL ,
                                           	PRIMARY KEY (id)
                                           );
                """
        );
        Assertions.assertEquals(0L, aEntityService.count());
        Assertions.assertEquals(0L, bEntityService.count());
        aEntityService.testSaveBath(List.of(new AEntity("test1"), new AEntity("test2")));
        Assertions.assertEquals(2L, aEntityService.count());
        bEntityService.testSaveBath(List.of(new BEntity("test1"), new BEntity("test2"), new BEntity("test3")));
        Assertions.assertEquals(3L, bEntityService.count());

    }

}
