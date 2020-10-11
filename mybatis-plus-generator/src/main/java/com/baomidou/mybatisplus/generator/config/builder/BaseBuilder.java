package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * 基础属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.4.1
 */
public class BaseBuilder {

    @Getter(AccessLevel.NONE)
    private final StrategyConfig strategyConfig;

    public BaseBuilder(StrategyConfig strategyConfig) {
        this.strategyConfig = strategyConfig;
    }

    public EntityBuilder entity() {
        return strategyConfig.entity();
    }

    public ControllerBuilder controller() {
        return strategyConfig.controller();
    }

    public MapperBuilder mapper() {
        return strategyConfig.mapper();
    }

    public ServiceBuilder service() {
        return strategyConfig.service();
    }

    public StrategyConfig build() {
        return this.strategyConfig;
    }
}
