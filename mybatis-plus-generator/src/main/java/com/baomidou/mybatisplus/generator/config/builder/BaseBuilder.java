package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * 配置构建
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

    public Entity.Builder entityBuilder() {
        return strategyConfig.entityBuilder();
    }

    public Controller.Builder controllerBuilder() {
        return strategyConfig.controllerBuilder();
    }

    public Mapper.Builder mapperBuilder() {
        return strategyConfig.mapperBuilder();
    }

    public Service.Builder serviceBuilder() {
        return strategyConfig.serviceBuilder();
    }

    public StrategyConfig build() {
        return this.strategyConfig;
    }
}
