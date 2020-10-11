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
public class MapperBuilder extends BaseBuilder {

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superClass = ConstVal.SUPER_MAPPER_CLASS;

    public MapperBuilder(StrategyConfig strategyConfig) {
        super(strategyConfig);
    }

    /**
     * 父类Mapper
     *
     * @param superClass 类名
     * @return this
     */
    public MapperBuilder superClass(String superClass) {
        this.superClass = superClass;
        return this;
    }

    /**
     * 父类Mapper
     *
     * @param superClass 类
     * @return this
     */
    public MapperBuilder superClass(Class<?> superClass) {
        return superClass(superClass.getName());
    }

}
