package com.baomidou.mybatisplus.test.h2.issues.repositoryscan;

import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.entity.Demo;
import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.service.IDemoRepositoryService;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@ContextConfiguration(classes = {AppConfig.class})
@Tag("integration")
public class RepositoryDefaultScanTest {

    @Autowired
    private IDemoRepositoryService demoService;

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldScanRepositoryByDefault() throws SQLException {
        new SqlRunner(dataSource.getConnection()).run("""
                 CREATE TABLE IF NOT EXISTS demo (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      name VARCHAR(30) NULL DEFAULT NULL ,
                      PRIMARY KEY (id)
                 );
                """);
        long before = demoService.count();
        Demo demo = new Demo();
        assertThat(demoService.save(demo)).isTrue();
        // 主键由 MP 生成,不依赖数据库自增
        assertThat(demo.getId()).isNotNull();
        assertThat(demoService.getById(demo.getId())).isNotNull();
        assertThat(demoService.saveBatch(List.of(new Demo(), new Demo()))).isTrue();
        assertThat(demoService.count()).isEqualTo(before + 3);
        // 没有 @MapperScan,mapper 仍由容器交给 MP 解析
        assertThat(demoService.getBaseMapper()).isNotNull();
    }

}
