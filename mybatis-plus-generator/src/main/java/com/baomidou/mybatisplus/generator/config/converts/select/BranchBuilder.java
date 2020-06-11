package com.baomidou.mybatisplus.generator.config.converts.select;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 分支构建者
 *
 * @author hanchunlin
 * Created at 2020/6/11 17:22
 */
public interface BranchBuilder<P, T> {

    Branch<P, T> then(Function<P, T> factory);

    default Branch<P, T> then(T value) {
        return then(p -> value);
    }

    static <P, T> BranchBuilder<P, T> of(Predicate<P> tester) {
        return factory -> Branch.of(tester, factory);
    }

}
