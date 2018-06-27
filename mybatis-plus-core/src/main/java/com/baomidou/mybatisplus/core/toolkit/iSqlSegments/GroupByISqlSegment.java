package com.baomidou.mybatisplus.core.toolkit.iSqlSegments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * @author miemie
 * @since 2018-06-27
 */
public class GroupByISqlSegment extends ArrayList<ISqlSegment> implements ISqlSegment {

    private static final long serialVersionUID = -4135938724727477310L;

    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        if (!isEmpty()) {
            list.remove(0);
        }
        return super.addAll(list);
    }

    @Override
    public String getSqlSegment() {
        return null;
    }
}
