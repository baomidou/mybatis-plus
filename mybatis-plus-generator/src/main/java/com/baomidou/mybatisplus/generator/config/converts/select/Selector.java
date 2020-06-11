package com.baomidou.mybatisplus.generator.config.converts.select;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分支结果选择器
 *
 * @author hanchunlin
 * Created at 2020/6/11 16:55
 */
public class Selector<P, T> {
    private boolean success = false;
    private Function<P, T> factory;
    private final P param;

    public Selector(P param) {
        this.param = param;
    }

    /**
     * 使用指定的参数创建选择器
     *
     * @param param 参数
     * @param <P>   参数类型
     * @param <T>   返回值类型
     * @return 返回选择器自身
     */
    public static <P, T> Selector<P, T> param(P param) {
        return new Selector<>(param);
    }

    /**
     * 传入一个新的分支，如果这个分支满足条件
     *
     * @param branch 则当前选择器将接受当前分支的结果并完成
     * @return 选择器自身
     */
    public Selector<P, T> test(Branch<P, T> branch) {
        if (!success) {
            boolean pass = branch.tester().test(param);
            if (pass) {
                success = true;
                factory = branch.factory();
            }
        }
        return this;
    }

    public T or(Supplier<T> supplier) {
        return success ? this.factory.apply(param) : supplier.get();
    }

    public T or(T t) {
        return or(() -> t);
    }

}
