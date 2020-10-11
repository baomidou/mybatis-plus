package com.baomidou.mybatisplus.generator.config.builder;

import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.Getter;

/**
 * 控制器属性配置
 *
 * @author nieqiurong 2020/10/11.
 * @since 3.4.1
 */
@Getter
public class ControllerBuilder extends BaseBuilder {

    /**
     * 生成 <code>@RestController</code> 控制器
     * <pre>
     *      <code>@Controller</code> -> <code>@RestController</code>
     * </pre>
     */
    private boolean restStyle = false;
    /**
     * 驼峰转连字符
     * <pre>
     *      <code>@RequestMapping("/managerUserActionHistory")</code> -> <code>@RequestMapping("/manager-user-action-history")</code>
     * </pre>
     */
    private boolean hyphenStyle = false;

    /**
     * 自定义继承的Controller类全称，带包名
     */
    private String superClass;

    public ControllerBuilder(StrategyConfig strategyConfig) {
        super(strategyConfig);
    }

    /**
     * 父类控制器
     *
     * @param clazz 父类控制器
     * @return this
     */
    public ControllerBuilder superClass(Class<?> clazz) {
        return superClass(clazz.getName());
    }

    /**
     * 父类控制器
     *
     * @param superClass 父类控制器类名
     * @return this
     */
    public ControllerBuilder superClass(String superClass) {
        this.superClass = superClass;
        return this;
    }

    /**
     * 是否驼峰转连字符
     *
     * @return this
     */
    public ControllerBuilder hyphenStyle(boolean hyphenStyle) {
        this.hyphenStyle = hyphenStyle;
        return this;
    }

    /**
     * 生成@RestController控制器
     *
     * @param restStyle 是否生成@RestController控制器
     * @return this
     */
    public ControllerBuilder restStyle(boolean restStyle) {
        this.restStyle = restStyle;
        return this;
    }
}
