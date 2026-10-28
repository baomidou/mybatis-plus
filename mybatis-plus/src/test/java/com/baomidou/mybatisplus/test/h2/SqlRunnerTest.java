package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * SqlRunner测试
 *
 * @author nieqiurong 2018/8/25 11:05.
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
@Tag("integration")
class SqlRunnerTest {

    @Autowired
    private IH2StudentService studentService;

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
    void shouldSelectCountWithSqlRunner() {
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
    void shouldInsertWithSqlRunner() {
        assertThat(SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )", "测试学生", 2))
                .isTrue();
        assertThat(
                SqlRunner.db(H2Student.class)
                        .insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )", "测试学生2", 3))
                .isTrue();
    }

    @Test
    void shouldRollbackWithSqlRunnerTransactional() {
        // SqlRunner 的插入也要挂在服务方法的事务上：异常抛出后两行 sqlRunnerTx 必须一起回滚
        assertThatThrownBy(() -> studentService.shouldRunSqlRunnerTransactional())
                .isInstanceOf(MybatisPlusException.class);
        assertThat(studentService.list(new QueryWrapper<H2Student>().like("name", "sqlRunnerTx")))
                .as("回滚后不该留下 name 含 sqlRunnerTx 的行").isEmpty();
    }

    @Test
    void shouldSelectPageWithSqlRunner() {
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
    void shouldInsertByDisorderParameter() {
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
    void shouldHandleSpecialParameters() {
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
    void shouldQueryByMap() {
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
    void shouldQueryByEntity() {
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
    void shouldQueryByDto() {
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
    void shouldQueryByArray() {
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
    void shouldQueryByList() {
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
    void shouldQueryByRecord() {
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
