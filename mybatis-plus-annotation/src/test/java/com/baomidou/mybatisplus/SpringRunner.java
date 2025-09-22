package com.baomidou.mybatisplus;

import com.baomidou.mybatisplus.base.OverwriteRunner;
import com.baomidou.mybatisplus.spring.SqlSessionFactoryBean;

import java.util.List;

/**
 * @author miemie
 * @since 2025/9/22
 */
public class SpringRunner {

    public static void main(String[] args) throws Exception {
        new OverwriteRunner("mybatis-plus-spring", "mybatisSpring", "mybatis-spring", List.of(
            new SqlSessionFactoryBean()
        )).setOnlyFile(true).run();
    }
}
