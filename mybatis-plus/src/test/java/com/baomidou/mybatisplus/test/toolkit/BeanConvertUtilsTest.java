package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.BeanConvertUtils;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean转换工具类测试类
 *
 * @author hll
 * @since 2024-06-21
 */
class BeanConvertUtilsTest {

    @Test
    void testConvert() {
        List<Student> studentList = new ArrayList<>();
        Student student = new Student();
        student.setAge(17);
        student.setName("hll");
        student.setGender(true);
        student.setGrade((byte) 99);
        studentList.add(student);
        List<Teacher> teacherList = BeanConvertUtils.convert(studentList, Teacher.class);
        for (int i = 0; i < teacherList.size(); i++) {
            Teacher t = teacherList.get(i);
            Student s = studentList.get(i);
            Assertions.assertEquals(s.getAge(), t.getAge());
            Assertions.assertEquals(s.getName(), t.getName());
            Assertions.assertEquals(s.isGender(), t.isGender());
        }
    }

    @Data
    public static class Man {

        private String home;

    }

    @Data
    public static class Student {

        private static final Integer no = 12;

        private byte grade;

        private int age;

        private String name;

        private boolean gender;

        private Integer salary;
    }

    @Data
    public static class Teacher {

        private Integer age;

        private String name;

        private boolean gender;

        private String clazz;

        private int salary;
    }
}
