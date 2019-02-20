package com.baomidou.mybatisplus.test.generator;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.test.generator.entity.BaseEntity;
import com.baomidou.mybatisplus.test.generator.entity.SuperEntity;

/**
 * <p>
 * 策略测试
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
public class StrategyConfigTest {

    @Test
    public void baseEntity() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class);
        String[] columns = strategyConfig.getSuperEntityColumns();
        Arrays.stream(columns).forEach(column -> System.out.println(column));
        Assertions.assertEquals(columns.length, 3);
    }

    @Test
    public void baseEntityNaming() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(BaseEntity.class, NamingStrategy.underline_to_camel);
        String[] columns = strategyConfig.getSuperEntityColumns();
        Arrays.stream(columns).forEach(column -> System.out.println(column));
        Assertions.assertEquals(columns.length, 3);
    }

    @Test
    public void superEntity() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setSuperEntityClass(SuperEntity.class);
        String[] columns = strategyConfig.getSuperEntityColumns();
        Arrays.stream(columns).forEach(column -> System.out.println(column));
        Assertions.assertEquals(columns.length, 2);
    }
}
