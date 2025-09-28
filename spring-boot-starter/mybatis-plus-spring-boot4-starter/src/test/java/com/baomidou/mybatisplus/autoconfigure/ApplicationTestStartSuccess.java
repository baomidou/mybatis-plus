package com.baomidou.mybatisplus.autoconfigure;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2022-11-29
 */
@SpringBootApplication
public class ApplicationTestStartSuccess {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ApplicationTestStartSuccess.class, args);
        SqlSessionFactory bean = context.getBean(SqlSessionFactory.class);
        Configuration configuration = bean.getConfiguration();
        GlobalConfig config = GlobalConfigUtils.getGlobalConfig(configuration);
        assertThat(config.getIdentifierGenerator()).isNotNull();
    }
}
