package com.baomidou.mybatisplus.core.test;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import org.junit.Assert;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        query();
    }

    public static void query() {
        Query query = new Query(User.class)
            .select(User::getId, User::getName)
            .eq(User::getId, 1)
            .eq(User::getName, "张三")
            .orderBy(User::getName);
        System.out.println(query);
    }

    public static void columnPrefix() {
        TableInfo tableInfo = TableInfoHelper.initTableInfo(null, Test.class);
        Assert.assertEquals("test_id", tableInfo.getKeyColumn());
        tableInfo.getFieldList().forEach(i-> System.out.println(i.getColumn()));
    }


    public static class Test {
        private Long id;
        private String name;
        private String age;
        private LocalDate localDate;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }
    }
}
