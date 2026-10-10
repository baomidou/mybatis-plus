package com.baomidou.mybatisplus;

import com.baomidou.mybatisplus.base.OverwriteRunner;
import com.baomidou.mybatisplus.spring.SqlSessionFactoryBean;

import java.nio.file.Path;
import java.util.List;

/**
 * @author miemie
 * @since 2025/9/22
 */
public class SpringRunner {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "请传入 MyBatis Spring sources.jar 路径，或运行 :mybatis-plus-up:runSpringRunner");
        }
        new OverwriteRunner("mybatis-plus-spring", Path.of(args[0]), List.of(new SqlSessionFactoryBean())).run();
    }
}
