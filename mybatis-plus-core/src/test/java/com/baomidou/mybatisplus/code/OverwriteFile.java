package com.baomidou.mybatisplus.code;

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

    private List<Overwrite> steps = new ArrayList<>();

    public void addStep(Function<Overwrite.OverwriteBuilder, Overwrite.OverwriteBuilder> function) {
        this.steps.add(function.apply(Overwrite.builder()).build());
    }
}
