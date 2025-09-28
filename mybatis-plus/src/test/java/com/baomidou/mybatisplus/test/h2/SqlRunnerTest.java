package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlRunner测试
 *
 * @author nieqiurong 2018/8/25 11:05.
 */
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class SqlRunnerTest {

    @Autowired
    private IH2StudentService studentService;

    @Test
    @Order(3)
    void testSelectCount() {
        long count = SqlRunner.db().selectCount("select count(1) from h2student");
        Assertions.assertTrue(count > 0);
        count = SqlRunner.db().selectCount("select count(1) from h2student where id > {0}", 0);
        Assertions.assertTrue(count > 0);
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student");
        Assertions.assertTrue(count > 0);
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student where id > {0}", 0);
        Assertions.assertTrue(count > 0);
    }

    @Test
    @Transactional
    @Order(1)
    void testInsert() {
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )", "测试学生", 2));
        Assertions.assertTrue(SqlRunner.db(H2Student.class).insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )", "测试学生2", 3));
    }

    @Test
    @Order(2)
    void testTransactional() {
        try {
            studentService.testSqlRunnerTransactional();
        } catch (RuntimeException e) {
            List<H2Student> list = studentService.list(new QueryWrapper<H2Student>().like("name", "sqlRunnerTx"));
            Assertions.assertTrue(CollectionUtils.isEmpty(list));
        }
    }

    @Test
    @Order(4)
    void testSelectPage() {
        IPage<Map<String,Object>> page1 = SqlRunner.db().selectPage(new Page<>(1, 3), "select * from h2student");
        Assertions.assertEquals(3, page1.getRecords().size());
        IPage<Map<String,Object>> page2 = SqlRunner.db().selectPage(new Page<>(1, 3), "select * from h2student where id >= {0}", 0);
        Assertions.assertEquals(3, page2.getRecords().size());
        IPage<Map<String,Object>> page3 = SqlRunner.db().selectPage(new Page<>(1, 3), "select * from h2student where id = {0}", 10086);
        Assertions.assertEquals(0, page3.getRecords().size());
    }

    @Test
    @Order(5)
    void testInsertByDisorderParameter() {
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {3}, {2}, {1} )", "测试学生", 2, "'六翻了'", 10000));
        Assertions.assertTrue(SqlRunner.db(H2Student.class).insert("INSERT INTO h2student ( name, age, id ) VALUES ( {0}, {1}, {2} )", "测试学生2", 3, 10001));
        Assertions.assertEquals(2, SqlRunner.db().selectCount("select count(1) from h2student where (id = 10000 or id = 10001)"));
    }

    @Test
    @Order(6)
    void testSpecialParameters() {
        var name = "`测`的'的'\\//塞'2";
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {3}, {0}, {1} )", name, 2, "'六翻了'", 10004));
        Assertions.assertEquals(10004L, SqlRunner.db().selectObj("select id from h2student where name = {0}", name));
        name = "`测`的'的'\\//塞'2" + "2";
        Assertions.assertTrue(SqlRunner.db().update("update h2student set name = {0} where id = {1}", name, 10004L));
        Assertions.assertEquals(10004L, SqlRunner.db().selectObj("select id from h2student where name = {0}", name));
    }

    @Test
    @Order(7)
    void testByMap() {
        var map = Map.of("name", "test", "age", AgeEnum.TWO, "id", 11000L);
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", map));
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", map);
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals(map.get("name"), resultMap.get("NAME"));
        Assertions.assertEquals(AgeEnum.TWO.getValue(), resultMap.get("AGE"));
        Assertions.assertEquals(map.get("id"), resultMap.get("ID"));
        map = new HashMap<>();
        map.put("name","test");
        map.put("age", AgeEnum.TWO);
        map.put("id", 11100L);
        map.put("size", "测试size");
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {size}, {age} )", map));
        resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", map);
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals(map.get("size"), resultMap.get("NAME"));
        Assertions.assertEquals(AgeEnum.TWO.getValue(), resultMap.get("AGE"));
        Assertions.assertEquals(map.get("id"), resultMap.get("ID"));
    }

    @Test
    @Order(8)
    void testByEntity() {
        var entity = new H2Student();
        entity.setId(11001L);
        entity.setName("test");
        entity.setAge(12);
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", entity));
    }

    @Data
    @AllArgsConstructor
    static class StudentDto {

        private Long id;

        private String name;

        private AgeEnum age;
    }

    @Test
    @Order(9)
    void testByDto() {
        var studentDto = new StudentDto(11002L, "测试学生", AgeEnum.THREE);
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", studentDto));
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", studentDto);
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals(studentDto.getName(), resultMap.get("NAME"));
        Assertions.assertEquals(studentDto.getAge().getValue(), resultMap.get("AGE"));
        Assertions.assertEquals(studentDto.getId(), resultMap.get("ID"));
    }

    @Test
    @Order(10)
    void testByArray() {
        var array = new Object[]{11003L, "测试学生", AgeEnum.THREE};
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {0}, {1}, {2} )", array));
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {0}", array);
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals("测试学生", resultMap.get("NAME"));
        Assertions.assertEquals(AgeEnum.THREE.getValue(), resultMap.get("AGE"));
        resultMap = SqlRunner.db().selectOne("select * from h2student where id = {0}", new long[]{11003L});
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals("测试学生", resultMap.get("NAME"));
        Assertions.assertEquals(AgeEnum.THREE.getValue(), resultMap.get("AGE"));
        Assertions.assertEquals(11003L, resultMap.get("ID"));
        Assertions.assertNull(SqlRunner.db().selectOne("select * from h2student where id = {0} and name= {1}", (Object) new Object[]{11003L, "234"}));
        Assertions.assertNull(SqlRunner.db().selectOne("select * from h2student where id = {0} and name= {1}", new Object[]{11003L, "234"}));
        Assertions.assertNull(SqlRunner.db().selectOne("select * from h2student where id = {0} and name= {1}", 11003L, "234"));
    }

    @Test
    @Order(11)
    void testByList() {
        var list = List.of(11004L, "测试学生", AgeEnum.THREE);
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {0}, {1}, {2} )", list));
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {0}", list);
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals("测试学生", resultMap.get("NAME"));
        Assertions.assertEquals(AgeEnum.THREE.getValue(), resultMap.get("AGE"));
        Assertions.assertEquals(11004L, resultMap.get("ID"));
    }

    record StudentDtoRecord(Long id, String name, AgeEnum age) {

    }

    @Test
    @Order(12)
    void testByRecord() {
        var studentDto = new StudentDtoRecord(11005L, "测试学生", AgeEnum.THREE);
        Assertions.assertTrue(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", studentDto));
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", studentDto);
        Assertions.assertNotNull(resultMap);
        Assertions.assertEquals(studentDto.name, resultMap.get("NAME"));
        Assertions.assertEquals(studentDto.age.getValue(), resultMap.get("AGE"));
        Assertions.assertEquals(studentDto.id, resultMap.get("ID"));
    }

}
