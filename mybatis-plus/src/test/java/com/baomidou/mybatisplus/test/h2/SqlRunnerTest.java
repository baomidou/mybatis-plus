package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(count > 0).isTrue();
        count = SqlRunner.db().selectCount("select count(1) from h2student where id > {0}", 0);
        assertThat(count > 0).isTrue();
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student");
        assertThat(count > 0).isTrue();
        count = SqlRunner.db(H2Student.class).selectCount("select count(1) from h2student where id > {0}", 0);
        assertThat(count > 0).isTrue();
    }

    @Test
    @Transactional
    @Order(1)
    void testInsert() {
        assertThat(SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )", "测试学生", 2))
                .isTrue();
        assertThat(
                SqlRunner.db(H2Student.class)
                        .insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )", "测试学生2", 3))
                .isTrue();
    }

    @Test
    @Order(2)
    void testTransactional() {
        try {
            studentService.testSqlRunnerTransactional();
        } catch (RuntimeException e) {
            List<H2Student> list = studentService.list(new QueryWrapper<H2Student>().like("name", "sqlRunnerTx"));
            assertThat(CollectionUtils.isEmpty(list)).isTrue();
        }
    }

    @Test
    @Order(4)
    void testSelectPage() {
        IPage<Map<String, Object>> page1 = SqlRunner.db().selectPage(new Page<>(1, 3), "select * from h2student");
        assertThat(page1.getRecords().size()).isEqualTo(3);
        IPage<Map<String, Object>> page2 = SqlRunner.db()
                .selectPage(new Page<>(1, 3), "select * from h2student where id >= {0}", 0);
        assertThat(page2.getRecords().size()).isEqualTo(3);
        IPage<Map<String, Object>> page3 = SqlRunner.db()
                .selectPage(new Page<>(1, 3), "select * from h2student where id = {0}", 10086);
        assertThat(page3.getRecords().size()).isEqualTo(0);
    }

    @Test
    @Order(5)
    void testInsertByDisorderParameter() {
        assertThat(
                SqlRunner.db().insert(
                        "INSERT INTO h2student (id, name, age ) VALUES ( {3}, {2}, {1} )", "测试学生", 2, "'六翻了'", 10000))
                .isTrue();
        assertThat(
                SqlRunner.db(H2Student.class)
                        .insert("INSERT INTO h2student ( name, age, id ) VALUES ( {0}, {1}, {2} )", "测试学生2", 3, 10001))
                .isTrue();
        assertThat(SqlRunner.db().selectCount("select count(1) from h2student where (id = 10000 or id = 10001)"))
                .isEqualTo(2);
    }

    @Test
    @Order(6)
    void testSpecialParameters() {
        var name = "`测`的'的'\\//塞'2";
        assertThat(
                SqlRunner.db().insert(
                        "INSERT INTO h2student (id, name, age ) VALUES ( {3}, {0}, {1} )", name, 2, "'六翻了'", 10004))
                .isTrue();
        assertThat(SqlRunner.db().selectObj("select id from h2student where name = {0}", name)).isEqualTo(10004L);
        name = "`测`的'的'\\//塞'2" + "2";
        assertThat(SqlRunner.db().update("update h2student set name = {0} where id = {1}", name, 10004L)).isTrue();
        assertThat(SqlRunner.db().selectObj("select id from h2student where name = {0}", name)).isEqualTo(10004L);
    }

    @Test
    @Order(7)
    void testByMap() {
        var map = Map.of("name", "test", "age", AgeEnum.TWO, "id", 11000L);
        assertThat(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", map))
                .isTrue();
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", map);
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo(map.get("name"));
        assertThat(resultMap.get("AGE")).isEqualTo(AgeEnum.TWO.getValue());
        assertThat(resultMap.get("ID")).isEqualTo(map.get("id"));
        map = new HashMap<>();
        map.put("name", "test");
        map.put("age", AgeEnum.TWO);
        map.put("id", 11100L);
        map.put("size", "测试size");
        assertThat(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {size}, {age} )", map))
                .isTrue();
        resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", map);
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo(map.get("size"));
        assertThat(resultMap.get("AGE")).isEqualTo(AgeEnum.TWO.getValue());
        assertThat(resultMap.get("ID")).isEqualTo(map.get("id"));
    }

    @Test
    @Order(8)
    void testByEntity() {
        var entity = new H2Student();
        entity.setId(11001L);
        entity.setName("test");
        entity.setAge(12);
        assertThat(
                SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", entity))
                .isTrue();
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
        assertThat(
                SqlRunner.db()
                        .insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", studentDto))
                .isTrue();
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", studentDto);
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo(studentDto.getName());
        assertThat(resultMap.get("AGE")).isEqualTo(studentDto.getAge().getValue());
        assertThat(resultMap.get("ID")).isEqualTo(studentDto.getId());
    }

    @Test
    @Order(10)
    void testByArray() {
        var array = new Object[]{11003L, "测试学生", AgeEnum.THREE};
        assertThat(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {0}, {1}, {2} )", array))
                .isTrue();
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {0}", array);
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo("测试学生");
        assertThat(resultMap.get("AGE")).isEqualTo(AgeEnum.THREE.getValue());
        resultMap = SqlRunner.db().selectOne("select * from h2student where id = {0}", new long[]{11003L});
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo("测试学生");
        assertThat(resultMap.get("AGE")).isEqualTo(AgeEnum.THREE.getValue());
        assertThat(resultMap.get("ID")).isEqualTo(11003L);
        assertThat(
                SqlRunner.db().selectOne(
                        "select * from h2student where id = {0} and name= {1}", (Object) new Object[]{11003L, "234"}))
                .isNull();
        assertThat(
                SqlRunner.db()
                        .selectOne("select * from h2student where id = {0} and name= {1}", new Object[]{11003L, "234"}))
                .isNull();
        assertThat(SqlRunner.db().selectOne("select * from h2student where id = {0} and name= {1}", 11003L, "234"))
                .isNull();
    }

    @Test
    @Order(11)
    void testByList() {
        var list = List.of(11004L, "测试学生", AgeEnum.THREE);
        assertThat(SqlRunner.db().insert("INSERT INTO h2student (id, name, age ) VALUES ( {0}, {1}, {2} )", list))
                .isTrue();
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {0}", list);
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo("测试学生");
        assertThat(resultMap.get("AGE")).isEqualTo(AgeEnum.THREE.getValue());
        assertThat(resultMap.get("ID")).isEqualTo(11004L);
    }

    record StudentDtoRecord(Long id, String name, AgeEnum age) {

    }

    @Test
    @Order(12)
    void testByRecord() {
        var studentDto = new StudentDtoRecord(11005L, "测试学生", AgeEnum.THREE);
        assertThat(
                SqlRunner.db()
                        .insert("INSERT INTO h2student (id, name, age ) VALUES ( {id}, {name}, {age} )", studentDto))
                .isTrue();
        Map<String, Object> resultMap = SqlRunner.db().selectOne("select * from h2student where id = {id}", studentDto);
        assertThat(resultMap).isNotNull();
        assertThat(resultMap.get("NAME")).isEqualTo(studentDto.name);
        assertThat(resultMap.get("AGE")).isEqualTo(studentDto.age.getValue());
        assertThat(resultMap.get("ID")).isEqualTo(studentDto.id);
    }

}
