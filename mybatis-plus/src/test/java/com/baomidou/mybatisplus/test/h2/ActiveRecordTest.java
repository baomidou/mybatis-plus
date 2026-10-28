package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ActiveRecord 测试
 *
 * @author nieqiurong 2018/7/27.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
@Tag("integration")
class ActiveRecordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveRecordTest.class);

    @Autowired
    private IH2StudentService h2StudentService;

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
    @Transactional
    void shouldInsertWithActiveRecord() {
        H2Student student = new H2Student(3L, "测试学生", 2);
        assertThat(student.insert()).isTrue();
        assertThat(student.getId()).isEqualTo(3);
        student.setId(null);
        assertThat(student.insert()).isTrue();
    }

    @Test
    void shouldUpdateWithActiveRecord() {
        H2Student student = new H2Student(1L, "Tom长大了", 2);
        assertThat(student.updateById()).isTrue();
        student.setName("不听话的学生");
        assertThat(student.update(new QueryWrapper<H2Student>().gt("id", 10))).isTrue();
    }

    @Test
    void shouldSelectByIdWithActiveRecord() {
        H2Student student = new H2Student();
        student.setId(1L);
        assertThat(student.selectById()).isNotNull();
        assertThat(student.selectById(1L)).isNotNull();
    }

    @Test
    void shouldSelectListWithActiveRecord() {
        H2Student student = new H2Student();
        List<H2Student> students = student.selectList(new QueryWrapper<>(student));
        students.forEach($this -> LOGGER.info("用户信息:{}", $this));
        assertThat(students.size() > 1).isTrue();
    }

    @Test
    void shouldSelectPageWithActiveRecord() {
        IPage<H2Student> page = new Page<>(1, 10);
        H2Student student = new H2Student();
        page = student.selectPage(page, new QueryWrapper<>(student));
        List<H2Student> records = page.getRecords();
        LOGGER.info("总数:{}", page.getTotal());
        records.forEach($this -> LOGGER.info("用户信息:{}", $this));
        assertThat(page.getTotal() > 1).isTrue();
    }

    @Test
    void shouldSelectCountWithActiveRecord() {
        H2Student student = new H2Student();
        long count = new H2Student().selectCount(new QueryWrapper<>(student));
        LOGGER.info("count:{}", count);
        assertThat(count > 1).isTrue();
    }

    @Test
    void shouldInsertOrUpdateWithActiveRecord() {
        H2Student student = new H2Student(2L, "Jerry也长大了", 2);
        assertThat(student.insertOrUpdate()).isTrue();
        student.setId(null);
        assertThat(student.insertOrUpdate()).isTrue();
    }

    @Test
    void shouldSelectAllWithActiveRecord() {
        H2Student student = new H2Student();
        List<H2Student> students = student.selectAll();
        assertThat(students).isNotNull();
        students.forEach($this -> LOGGER.info("用户信息:{}", $this));
    }

    @Test
    void shouldSelectOneWithActiveRecord() {
        H2Student student = new H2Student();
        assertThat(student.selectOne(new QueryWrapper<>())).isNotNull();
    }

    @Test
    void shouldRollbackTransactionWithActiveRecord() {
        // 服务方法插三条 ActiveRecord 记录后抛异常，@Transactional 的回滚必须把三行一起带走；
        // 用 assertThatThrownBy 代替 try/catch，避免"根本没抛异常"时静默通过。
        assertThatThrownBy(() -> h2StudentService.shouldRunTransactional()).isInstanceOf(MybatisPlusException.class);
        List<H2Student> students = new H2Student()
                .selectList(new QueryWrapper<H2Student>().lambda().like(H2Student::getName, "tx"));
        assertThat(students).as("回滚后不该留下 name 含 tx 的行").isEmpty();
    }

    @Test
    void shouldDeleteWithActiveRecord() {
        H2Student student = new H2Student();
        student.setId(2L);
        assertThat(student.deleteById()).isTrue();
        assertThat(student.deleteById(12L)).isTrue();
        assertThat(student.delete(new QueryWrapper<H2Student>().gt("id", 10))).isTrue();
    }

    @Test
    void sqlCommentTest() {
        String name = "name1", nameNew = "name1New";
        H2Student student = new H2Student().setName(name).setAge(2);
        student.delete(new QueryWrapper<H2Student>().comment("deleteAllStu"));
        assertThat(student.insert()).isTrue();
        boolean updated = new H2Student().setName(nameNew)
                .update(new QueryWrapper<H2Student>().comment("updateStuName1").lambda().eq(H2Student::getName, name));
        assertThat(updated).isTrue();
        H2Student h2Student = student.selectOne(
                new QueryWrapper<H2Student>().lambda().comment("getStuByUniqueName").eq(H2Student::getName, nameNew));
        assertThat(h2Student).isNotNull();
        LambdaQueryWrapper<H2Student> queryWrapper = new QueryWrapper<H2Student>().lambda().ge(H2Student::getAge, 1);
        long userCount = student.selectCount(queryWrapper.comment("getStuCount"));
        assertThat(userCount).isEqualTo(1);
        List<H2Student> h2StudentList = student.selectList(queryWrapper.comment("getStuList"));
        assertThat(h2StudentList.size()).isEqualTo(1);
        IPage<H2Student> h2StudentIPage = student.selectPage(new Page<>(1, 10), queryWrapper.comment("getStuPage"));
        assertThat(h2StudentIPage.getRecords().size()).isEqualTo(1);
    }
}
