package com.baomidou.mybatisplus.core.test.lambda1;

import lombok.Data;

import java.util.List;

/**
 * @author ming
 * @Date 2018/5/11
 */
public class JustDoIt {

    public static void main(String[] args) {
        BaseMapper<User> mapper = new UserMapper();
        StrWrapper1<User> eq1 = new StrWrapper1<User>().eq("", 1);
        LambdaWrapper1<User> eq2 = new LambdaWrapper1<User>().eq(User::getId, 1);
        mapper.selectList(eq1);
        mapper.selectList(eq2);
    }

    @Data
    private static class User {
        private Integer id;
        private String name;
    }

    private interface BaseMapper<T> {

        List<T> selectList(Wrapper1<T> ew);
    }

    private static class UserMapper implements BaseMapper<User> {

        @Override
        public List<User> selectList(Wrapper1<User> ew) {
            System.out.println(ew.getSqlSegment());
            return null;
        }
    }
}
