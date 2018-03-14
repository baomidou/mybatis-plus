package com.baomidou.mybatisplus.core.test;

public class Main {
    public static void main(String[] args) {
        Query query = new Query(User.class)
            .select(User::getId, User::getName)
            .eq(User::getId, 1)
            .eq(User::getName,"张三")
            .orderBy(User::getName);
        System.out.println(query);
    }
}
