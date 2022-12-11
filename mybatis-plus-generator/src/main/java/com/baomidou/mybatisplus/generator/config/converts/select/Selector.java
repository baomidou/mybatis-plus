package com.baomidou.mybatisplus.generator.config.converts.select;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分支结果选择器
 * <p>
 * 当前选择器会从给定的分支中选择第一个匹配的分支，并返回其结果
 * <p>
 * 一旦结果被选择，其他的分支将不再被调用
 *
 * @author hanchunlin
 * Created at 2020/6/11 16:55
 */
public class Selector<P, T> {
    private boolean selected = false;
    private Function<P, T> factory;

    /**
     * 选择器参数，该参数会在进行条件判断和结果获取时会被当做条件传入
     */
    private final P param;

    /**
     * @param param 参数
     */
    public Selector(P param) {
        this.param = param;
    }

    /**
     * 使用指定的参数创建选择器
     *
     * @param param 参数
     * @param <P>   参数类型
     * @param <T>   返回值类型
     * @return 返回新的选择器
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
        if (!selected) {
            boolean pass = branch.tester().test(param);
            if (pass) {
                selected = true;
                factory = branch.factory();
            }
        }
        return this;
    }

    /**
     * 获取结果，如果当前选择器没有击中任何条件分支，则从给定的提供者中获取结果；
     * 否则将使用当前选择器选中的分支
     *
     * @param supplier 默认值提供者
     * @return 如果有分支被击中，则返回分支值，否则返回参数提供的值
     */
    public T or(Supplier<T> supplier) {
        return selected ? this.factory.apply(param) : supplier.get();
    }

    /**
     * @param t 给定默认值
     * @return 如果有分支被击中，则返回分支值，否则返回参数
     * @see #or(Supplier)
     */
    public T or(T t) {
        return or(() -> t);
    }

    /**
     * 当前选择器是否已经选择分支
     *
     * @return 如果已经存在分支被击中，则返回 true；否则返回 false
     */
    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return String.format("Selector{success=%s}", selected);
    }

}
