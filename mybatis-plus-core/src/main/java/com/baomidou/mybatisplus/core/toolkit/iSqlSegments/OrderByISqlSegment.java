package com.baomidou.mybatisplus.core.toolkit.iSqlSegments;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;

/**
 * @author miemie
 * @since 2018-06-27
 */
public class OrderByISqlSegment extends ArrayList<ISqlSegment> implements ISqlSegment {

    private static final long serialVersionUID = -294655105160779712L;

    @Override
    public String getSqlSegment() {
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(","));
    }
}
