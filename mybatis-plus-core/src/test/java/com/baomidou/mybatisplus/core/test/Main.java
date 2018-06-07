package com.baomidou.mybatisplus.core.test;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        query();
    }

    public static void query() {
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
