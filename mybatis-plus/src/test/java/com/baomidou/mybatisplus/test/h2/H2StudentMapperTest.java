package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.enums.GenderEnum;
import com.baomidou.mybatisplus.test.h2.enums.GradeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
@Tag("integration")
class H2StudentMapperTest extends H2TestSupport {

    @Autowired
    protected H2StudentMapper studentMapper;

    /**
     * h2student 是全套件共用的内存表，别的类（比如整表删除的用例）会把它清空， 而本类的用例要更新种子行 id=15。所以用例进出都重放一次种子脚本： 进来时保证自己要的行在，出去时不把残行留给别人。
     */
    @BeforeEach
    @AfterEach
    void restoreSeed() {
        ConnectionCallback<Void> restore = connection -> {
            new ResourceDatabasePopulator(new ClassPathResource("h2/student.insert.sql")).populate(connection);
            return null;
        };
        jdbcTemplate.execute(restore);
    }

    @Test
    void crudTest() {
        //        H2Student stu = new H2Student();
        //        stu.setGrade(GradeEnum.HIGH);
        //        studentMapper.update(stu, null);
        H2Student student = new H2Student();
        Long id = 10086L;
        student.setId(id);
        student.setAge(188);
        student.setGender(GenderEnum.MALE);
        student.setGrade(GradeEnum.PRIMARY);
        studentMapper.insert(student);

        List<H2Student> list = studentMapper.selectList(new QueryWrapper<>());
        for (H2Student s : list) {
            if (Objects.equals(s.getId(), id)) {
                Assert.notNull(s.getGrade(), "id=" + id + " should have grade");
                Assert.notNull(s.getGender(), "id=" + id + " should have gender");
            }
        }
        H2Student updateStu = new H2Student();
        updateStu.setId(15L);
        updateStu.setGrade(GradeEnum.HIGH);
        updateStu.setGender(GenderEnum.FEMALE);
        Assert.isTrue(studentMapper.updateById(updateStu) == 1, "should update success");
        updateStu = studentMapper.selectById(15L);
        Assert.notNull(updateStu.getGrade(), "grade should updated");
        Assert.notNull(updateStu.getGender(), "gender should updated");

    }

    /**
     * 分页条件命中不到数据时，total/pages/records 一律归零，不会留下翻页残值。
     */
    @Test
    void pageCountZeroTest() {
        Page<H2Student> page = new Page<>();
        IPage<H2Student> result = studentMapper.selectPage(page, Wrappers.<H2Student>query().eq("name", "无"));
        // 分页结果原地写回传入的 page
        assertThat(result).isSameAs(page);
        assertThat(result.getTotal()).isZero();
        assertThat(result.getPages()).isZero();
        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getCurrent()).isEqualTo(1L);
        assertThat(result.getSize()).isEqualTo(10L);
    }

    /**
     * group 或者 order 测试
     */
    @Test
    void groupByOrderBy() {
        LambdaQueryWrapper<H2Student> wrapper = Wrappers.<H2Student>lambdaQuery().groupBy(H2Student::getAge);
        LambdaQueryWrapper<H2Student> wrapper2 = Wrappers.<H2Student>lambdaQuery().orderByAsc(H2Student::getAge);
        assertThat(wrapper.getSqlSegment()).isEqualTo(" GROUP BY age");
        assertThat(wrapper2.getSqlSegment()).isEqualTo(" ORDER BY age ASC");
    }

    @Test
    void shouldDeleteByIdWithEntity() {
        H2Student h2Student = new H2Student(111L, "测试根据实体删除", 12);
        studentMapper.insert(h2Student);
        assertThat(studentMapper.deleteById(h2Student)).isEqualTo(1);
    }

    /**
     * in 条件按集合元素个数生成占位符，命中不到的值不会混进结果；空集合 MP 不拦截，直接拼出非法的 IN ()。
     */
    @Test
    void shouldGenerateInClauseWithCorrectPlaceholders() {
        LambdaQueryWrapper<H2Student> wrapper = Wrappers.<H2Student>lambdaQuery()
                .in(H2Student::getName, Arrays.asList("zzInTestA", "zzInTestB"));
        assertThat(wrapper.getTargetSql()).isEqualTo("(name IN (?,?))");
        long hitId = 910001L;
        studentMapper.insert(new H2Student(hitId, "zzInTestA", 1));
        studentMapper.insert(new H2Student(910002L, "zzInTestC", 1));
        List<H2Student> hit = studentMapper.selectList(wrapper);
        assertThat(hit).hasSize(1);
        assertThat(hit.get(0).getId()).isEqualTo(hitId);
        assertThat(hit.get(0).getName()).isEqualTo("zzInTestA");
        assertThat(Wrappers.<H2Student>lambdaQuery().in(H2Student::getName, List.of("zzInTestA")).getTargetSql())
                .isEqualTo("(name IN (?))");
        assertThat(Wrappers.<H2Student>lambdaQuery().in(H2Student::getName, List.of()).getTargetSql())
                .isEqualTo("(name IN ())");
    }

}
