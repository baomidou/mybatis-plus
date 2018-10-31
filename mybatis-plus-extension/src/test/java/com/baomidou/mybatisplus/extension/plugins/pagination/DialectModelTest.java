package com.baomidou.mybatisplus.extension.plugins.pagination;

import org.apache.ibatis.mapping.ParameterMapping;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miemie
 * @since 2018-10-31
 */
public class DialectModelTest {

    private static ParameterMapping testMapping = null;

    @Test
    public void m01() {
        List<ParameterMapping> list = new ArrayList<>();
        DialectModel model = model();
        model.getOffsetConsumer().accept(list);
        model.getLimitConsumer().accept(list);
        System.out.println(list.size());
    }

    private DialectModel model() {
        DialectModel model = new DialectModel();
        model.setOffsetConsumer(i -> i.add(testMapping));
        model.setLimitConsumer(i -> i.add(i.size() - 1, testMapping));
        return model;
    }
}
