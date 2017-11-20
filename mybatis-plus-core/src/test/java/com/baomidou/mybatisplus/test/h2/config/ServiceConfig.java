package com.baomidou.mybatisplus.test.h2.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/15
 */
@Configuration
@Import(value = {DBConfig.class, MybatisPlusConfig.class})
@ComponentScan("com.baomidou.mybatisplus.test.h2.service")
public class ServiceConfig {


}
