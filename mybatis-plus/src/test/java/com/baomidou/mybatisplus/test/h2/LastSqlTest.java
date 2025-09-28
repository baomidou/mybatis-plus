package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

/**
 * OrderBy LastSql 混合测试
 *
 * @author Dervish
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class LastSqlTest {

    @Autowired
    private H2StudentMapper mapper;

    @Test
    @Order(1)
    public void delete() {
        int res = mapper.delete(new LambdaUpdateWrapper<H2Student>().last(" and 1 = 2 ").eq(H2Student::getId, 0));
        Assertions.assertTrue(res <= 0);
    }

    @Test
    @Order(2)
    public void selectCount() {
        long result = mapper.selectCount(new LambdaQueryWrapper<H2Student>().last("where 1 =2"));
        Assertions.assertTrue(result <= 0);
    }

    @Test
    @Order(3)
    public void selectList() {
        List<H2Student> h2Students = mapper.selectList(new LambdaQueryWrapper<H2Student>().last("limit 1"));
        Assertions.assertEquals(1, h2Students.size());
    }

    @Test
    @Order(4)
    public void selectMaps() {
        List<Map<String, Object>> maps = mapper.selectMaps(new LambdaQueryWrapper<H2Student>().last("limit 1"));
        Assertions.assertEquals(1, maps.size());
    }

    @Test
    @Order(5)
    public void selectMapsPage() {
        IPage<Map<String,Object>> page = Page.of(0, 10);
        mapper.selectMapsPage(page, new QueryWrapper<H2Student>().last(" /* testSql */ ").comment("test"));
    }

    @Test
    @Order(6)
    public void selectObjs() {
        List<Object> objects = mapper.selectObjs(new QueryWrapper<H2Student>().last(" limit 1"));
        Assertions.assertEquals(1, objects.size());
    }

    @Test
    @Order(7)
    public void SelectOne() {
        H2Student h2Student = mapper.selectOne(new QueryWrapper<H2Student>().last(" where 1 = 2"));
        Assertions.assertNull(h2Student);
    }

    @Test
    @Order(8)
    public void selectPage() {
        IPage<H2Student> page = Page.of(0, 10);
        mapper.selectPage(page, new QueryWrapper<H2Student>().last(" /* testSql */ "));
    }

    @Test
    @Order(9)
    public void update() {
        int res = mapper.update(null, new UpdateWrapper<H2Student>().set("name", "dog").last(" where 1 =2 "));
        Assertions.assertTrue(res <= 0);
    }

    @Test
    @Order(10)
    public void selectListOrderBy() {
        List<H2Student> h2Students = mapper.selectList(null);
        Assertions.assertEquals(6, h2Students.size());
        Assertions.assertEquals(6, mapper.selectList(new LambdaQueryWrapper<H2Student>()
            .eq(H2Student::getAge, 1)).size());
        Assertions.assertEquals(6, mapper.selectList(new QueryWrapper<H2Student>()
            .orderByAsc("age")).size());
    }

    @Test
    @Order(11)
    public void selectPageOrderBy() {
        mapper.selectPage(Page.of(0, 10), null);
    }

    @Test
    @Order(12)
    public void selectMapsOrderBy() {
        List<Map<String, Object>> maps = mapper.selectMaps(null);
        Assertions.assertEquals(6, maps.size());
    }

    @Test
    @Order(12)
    public void selectMapsPageOrderBy() {
        mapper.selectMapsPage(Page.of(0, 10), null);
    }

    @Test
    @Order(12)
    public void selectObjsOrderBy() {
        List<Object> objs = mapper.selectObjs(null);
        Assertions.assertEquals(6, objs.size());
    }
}
