package com.baomidou.mybatisplus.core.test.query.predicate;

import com.baomidou.mybatisplus.core.test.query.Keywords;
import com.baomidou.mybatisplus.core.test.query.ISqlSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.test.query.Keywords.AND;
import static com.baomidou.mybatisplus.core.test.query.Keywords.OR;


public class Condition implements ISqlSegment {

    List<ISqlSegment> expression = new ArrayList<>();

    public Condition apply(String condition) {
        expression.add(() -> condition);
        return this;
    }

    public Condition and(String condition) {
        expression.add(AND);
        expression.add(() -> condition);
        return this;
    }

    public Condition and(Function<Condition, Condition> condition) {
        return addNestedCondition(condition, AND);
    }

    public Condition or(String condition) {
        expression.add(OR);
        expression.add(() -> condition);
        return this;
    }

    public Condition or(Function<Condition, Condition> condition) {
        return addNestedCondition(condition, OR);
    }

    private Condition addNestedCondition(Function<Condition, Condition> condition, Keywords keyword) {
        expression.add(keyword);
        expression.add(() -> "(");
        expression.add(condition.apply(new Condition()));
        expression.add(() -> ")");
        return this;
    }

    @Override
    public String sqlSegment() {
        return String.join(" ", expression.stream()
            .map(ISqlSegment::sqlSegment)
            .collect(Collectors.toList()));
    }
}
