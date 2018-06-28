package com.baomidou.mybatisplus.core.conditions.segments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * @author miemie
 * @since 2018-06-28
 */
@SuppressWarnings("serial")
public abstract class AbstractISegmentList extends ArrayList<ISqlSegment> implements ISqlSegment {

    /**
     * 最后一个值
     */
    protected ISqlSegment lastValue = null;
    protected boolean flushLastValue = false;

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        boolean goon = transformList(list, list.get(0));
        if (goon) {
            if (flushLastValue) {
                this.flushLastValue(list);
            }
            return super.addAll(list);
        }
        return false;
    }

    /**
     * <p>
     * 判断是否改变了 list
     * true 改变了,就继续执行
     * false 不再向下执行
     * </P>
     *
     * @param list         传入进来的 ISqlSegment 集合
     * @param firstSegment ISqlSegment 集合里第一个值
     * @return 是否继续向下执行
     */
    protected abstract boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment);

    /**
     * 刷新属性 lastValue
     */
    protected void flushLastValue(List<? extends ISqlSegment> list) {
        lastValue = list.get(list.size() - 1);
    }

    /**
     * 删除元素里最后一个值
     */
    protected void removeLast() {
        remove(size() - 1);
    }
}
