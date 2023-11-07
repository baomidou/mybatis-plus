package com.baomidou.mybatisplus.test.h2.issues.repositoryscan;

import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.entity.Demo;
import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.service.IDemoRepositoryService;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author nieqiurong
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AppConfigWithMapperScan.class})
public class RepositoryMapperScanTest {

    @Autowired
    private IDemoRepositoryService demoService;

    @Autowired
    private DataSource dataSource;

    @Test
    void test() throws SQLException {
        new SqlRunner(dataSource.getConnection()).run(
            """
                 CREATE TABLE IF NOT EXISTS demo (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(30) NULL DEFAULT NULL ,
                      PRIMARY KEY (id)
                 );
                """
        );
        demoService.save(new Demo());
        demoService.saveBatch(List.of(new Demo()));
    }

}
