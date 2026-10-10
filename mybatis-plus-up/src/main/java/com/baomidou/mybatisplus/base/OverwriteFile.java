package com.baomidou.mybatisplus.base;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author miemie
 * @since 2025/9/1
 */
@Data
public abstract class OverwriteFile {

    /**
     * 覆盖类的全限定名，例如 com.baomidou.mybatisplus.core.override.MybatisMapperMethod。
     * steps 执行后自动改写源码中的包名、类名、构造方法及类自身引用。
     */
    private String overwriteClass;

    private List<Overwrite> steps = new ArrayList<>();

    public void addStep(Function<Overwrite.OverwriteBuilder, Overwrite.OverwriteBuilder> function) {
        this.steps.add(function.apply(Overwrite.builder()).build());
    }
}
