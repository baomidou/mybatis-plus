package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OrderBy LastSql 混合测试
 *
 * @author Dervish
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
@Tag("integration")
public class LastSqlTest {

    @Autowired
    private H2StudentMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM h2student");
        jdbcTemplate.execute("insert into h2student (id, name, age) values (1, 'Tom', 1)");
        jdbcTemplate.execute("insert into h2student (id, name, age) values (2, 'Jerry', 1)");
        jdbcTemplate.execute("insert into h2student (id, name, age) values (12, '要开除的学生', 1)");
        jdbcTemplate.execute("insert into h2student (id, name, age) values (13, 'test1', 1)");
        jdbcTemplate.execute("insert into h2student (id, name, age) values (14, 'test2', 1)");
        jdbcTemplate.execute("insert into h2student (id, name, age) values (15, 'test3', 1)");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM h2student WHERE id > 15");
    }

    @Test
    public void delete() {
        int res = mapper.delete(new LambdaUpdateWrapper<H2Student>().last(" and 1 = 2 ").eq(H2Student::getId, 0));
        assertThat(res <= 0).isTrue();
    }

    @Test
    public void selectCount() {
        long result = mapper.selectCount(new LambdaQueryWrapper<H2Student>().last("where 1 =2"));
        assertThat(result <= 0).isTrue();
    }

    @Test
    public void selectList() {
        List<H2Student> h2Students = mapper.selectList(new LambdaQueryWrapper<H2Student>().last("limit 1"));
        assertThat(h2Students.size()).isEqualTo(1);
    }

    @Test
    public void selectMaps() {
        List<Map<String, Object>> maps = mapper.selectMaps(new LambdaQueryWrapper<H2Student>().last("limit 1"));
        assertThat(maps.size()).isEqualTo(1);
    }

    @Test
    public void selectMapsPage() {
        IPage<Map<String, Object>> page = Page.of(0, 10);
        IPage<Map<String, Object>> result = mapper
                .selectMapsPage(page, new QueryWrapper<H2Student>().last(" /* testSql */ ").comment("test"));
        assertThat(result.getTotal()).isEqualTo(6L);
        assertThat(result.getRecords()).hasSize(6);
    }

    @Test
    public void selectObjs() {
        List<Object> objects = mapper.selectObjs(new QueryWrapper<H2Student>().last(" limit 1"));
        assertThat(objects.size()).isEqualTo(1);
    }

    @Test
    public void SelectOne() {
        H2Student h2Student = mapper.selectOne(new QueryWrapper<H2Student>().last(" where 1 = 2"));
        assertThat(h2Student).isNull();
    }

    @Test
    public void selectPage() {
        IPage<H2Student> page = Page.of(0, 10);
        IPage<H2Student> result = mapper.selectPage(page, new QueryWrapper<H2Student>().last(" /* testSql */ "));
        assertThat(result.getTotal()).isEqualTo(6L);
        assertThat(result.getRecords()).hasSize(6);
    }

    @Test
    public void update() {
        int res = mapper.update(null, new UpdateWrapper<H2Student>().set("name", "dog").last(" where 1 =2 "));
        assertThat(res <= 0).isTrue();
    }

    @Test
    public void selectListOrderBy() {
        List<H2Student> h2Students = mapper.selectList(null);
        assertThat(h2Students.size()).isEqualTo(6);
        assertThat(mapper.selectList(new LambdaQueryWrapper<H2Student>().eq(H2Student::getAge, 1)).size()).isEqualTo(6);
        assertThat(mapper.selectList(new QueryWrapper<H2Student>().orderByAsc("age")).size()).isEqualTo(6);
    }

    @Test
    public void selectPageOrderBy() {
        IPage<H2Student> result = mapper.selectPage(Page.of(0, 10), null);
        assertThat(result.getTotal()).isEqualTo(6L);
        assertThat(result.getRecords()).hasSize(6);
    }

    @Test
    public void selectMapsOrderBy() {
        List<Map<String, Object>> maps = mapper.selectMaps(null);
        assertThat(maps.size()).isEqualTo(6);
    }

    @Test
    public void selectMapsPageOrderBy() {
        IPage<Map<String, Object>> result = mapper.selectMapsPage(Page.of(0, 10), null);
        assertThat(result.getTotal()).isEqualTo(6L);
        assertThat(result.getRecords()).hasSize(6);
    }

    @Test
    public void selectObjsOrderBy() {
        List<Object> objs = mapper.selectObjs(null);
        assertThat(objs.size()).isEqualTo(6);
    }
}
