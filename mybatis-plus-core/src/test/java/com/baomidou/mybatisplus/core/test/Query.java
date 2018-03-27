package com.baomidou.mybatisplus.core.test;

import static com.baomidou.mybatisplus.core.test.QueryHelper.method;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.jdbc.SQL;

public class Query {

    private SQL sql = new SQL();

    private Class<?> clazz;

    private Map<Method, String> introspector;

    public Query(Class<?> clazz) {
        this.clazz = clazz;
        introspector = QueryHelper.introspector(clazz);
        sql.FROM(clazz.getSimpleName().toUpperCase());
    }

    public Query select() {
        sql.SELECT("*");
        return this;
    }

    public <T> Query select(Property<T, ?>... fun) {
        String[] args = Arrays.asList(fun).stream().map(x -> introspector.get(method(x))).collect(Collectors.toList()).toArray(new String[]{});
        sql.SELECT(args);
        return this;
    }

    public <T> Query eq(Property<T, ?> fun, Object param) {
        String name = introspector.get(method(fun));
        sql.WHERE(name + "=" + param);
        return this;
    }

    public <T> Query orderBy(Property<T, ?> fun) {
        String name = introspector.get(method(fun));
        sql.ORDER_BY(name);
        return this;
    }

    @Override
    public String toString() {
        return sql.toString();
    }

    public interface Property<T, R> extends Function<T, R>, Serializable {

    }

}
