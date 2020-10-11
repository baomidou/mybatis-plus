package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.Getter;

/**
 * 控制器属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.4.1
 */
@Getter
public class Mapper {

    private Mapper() {
    }

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superClass = ConstVal.SUPER_MAPPER_CLASS;

    public static class Builder extends BaseBuilder {

        private final Mapper mapper = new Mapper();

        public Builder(StrategyConfig strategyConfig) {
            super(strategyConfig);
        }

        /**
         * 父类Mapper
         *
         * @param superClass 类名
         * @return this
         */
        public Builder superClass(String superClass) {
            this.mapper.superClass = superClass;
            return this;
        }

        /**
         * 父类Mapper
         *
         * @param superClass 类
         * @return this
         * @since 3.4.1
         */
        public Builder superClass(Class<?> superClass) {
            return superClass(superClass.getName());
        }

        public Mapper get() {
            return this.mapper;
        }
    }
}
