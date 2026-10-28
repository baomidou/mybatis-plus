package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

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
class H2Delete1Eq1Test extends H2TestSupport {

    @Autowired
    protected H2UserMapper logicDeleteMapper;
    @Autowired
    protected H2StudentMapper defaultMapper;

    /**
     * h2user、h2student 是整个测试套件的共享内存库，别的类会往里塞数据，所以本用例只查自己插的这批行。
     */
    private static final String OWN = "del1eq1-";

    /**
     * delete() 会把 h2user 全表打上删除标记、把 h2student 整表删空，而这两张表是整个套件共用的内存表。 所以收尾重放两个种子脚本（脚本第一条就是
     * delete，顺带把删除标记也复位），留给下一个类的是一张干净的标准种子表。
     */
    @AfterEach
    void tearDown() {
        ConnectionCallback<Void> restore = connection -> {
            new ResourceDatabasePopulator(new ClassPathResource("h2/user.insert.sql"),
                    new ClassPathResource("h2/student.insert.sql")).populate(connection);
            return null;
        };
        jdbcTemplate.execute(restore);
    }

    /**
     * 特征化条件查询：转义列 {@code `desc`} 能排序，实体非空字段会进 WHERE，实体条件与同名 eq 是 AND 叠加。
     */
    @Test
    void crudTest() {
        for (int i = 0; i < 10; i++) {
            assertThat(logicDeleteMapper.insert(new H2User(OWN + i, AgeEnum.ONE).setDesc(String.valueOf(i))))
                    .as("插入 %s", OWN + i).isEqualTo(1);
        }
        assertThat(logicDeleteMapper.selectList(new QueryWrapper<H2User>().likeRight("name", OWN).orderByAsc("`desc`")))
                .as("按转义后的 desc 列升序").extracting(H2User::getDesc)
                .containsExactly("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        assertThat(
                logicDeleteMapper.selectOne(
                        new QueryWrapper<H2User>().likeRight("name", OWN).orderByAsc("`desc`").last("limit 1")))
                .as("结果集有十行，靠 limit 1 才能走 selectOne").extracting(H2User::getName).isEqualTo(OWN + 0);

        H2User h2User = new H2User();
        h2User.setDesc("1");
        h2User.setName(OWN + 1);
        assertThat(logicDeleteMapper.selectList(new QueryWrapper<>(h2User).orderByAsc("name")))
                .as("实体上的 name 与 desc 都进了 WHERE").extracting(H2User::getDesc).containsExactly("1");
        assertThat(logicDeleteMapper.selectList(new QueryWrapper<>(h2User).eq("name", OWN + 2).orderByAsc("name")))
                .as("实体条件不会被同名 eq 覆盖，两个条件 AND 起来没人满足").isEmpty();

        List<Long> ids = new ArrayList<>();
        for (long i = 30; i < 50L; i++) {
            ids.add(i);
            assertThat(defaultMapper.insert(new H2Student(i, "Tom长大了", 1))).as("插入学生 %d", i).isEqualTo(1);
        }
        List<H2Student> students = defaultMapper
                .selectList(new QueryWrapper<H2Student>().in("id", ids).orderByAsc("id"));
        assertThat(students).extracting(H2Student::getId).containsExactlyElementsOf(ids);
        assertThat(students).extracting(H2Student::getName).containsOnly("Tom长大了");
        assertThat(students).extracting(H2Student::getAge).containsOnly(1);
        assertThat(
                defaultMapper.selectOne(new QueryWrapper<H2Student>().in("id", ids).orderByAsc("id").last("limit 1")))
                .extracting(H2Student::getId).isEqualTo(30L);

        H2Student h2Student = new H2Student();
        h2Student.setId(30L);
        h2Student.setAge(1);
        assertThat(defaultMapper.selectList(new QueryWrapper<>(h2Student))).extracting(H2Student::getName)
                .containsExactly("Tom长大了");
        assertThat(defaultMapper.selectList(new QueryWrapper<>(h2Student.setAge(2)))).as("id 对得上但 age 对不上，AND 之后查不到")
                .isEmpty();
    }

    @Test
    void delete() {
        for (int i = 0; i < 3; i++) {
            logicDeleteMapper.insert(new H2User(OWN + "d" + i, AgeEnum.ONE));
        }
        logicDeleteMapper.delete(new QueryWrapper<>());
        defaultMapper.delete(new QueryWrapper<>());
        assertThat(logicDeleteMapper.selectCount(new QueryWrapper<>())).isEqualTo(0);
        List<H2User> userList = queryByName(null);
        assertThat(userList.size()).isNotEqualTo(0);
    }
}
