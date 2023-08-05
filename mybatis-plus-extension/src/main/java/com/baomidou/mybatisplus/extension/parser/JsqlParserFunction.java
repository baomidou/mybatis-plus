package com.baomidou.mybatisplus.extension.parser;

import net.sf.jsqlparser.JSQLParserException;

/**
 * @author miemie
 * @since 2023-08-05
 */
@FunctionalInterface
public interface JsqlParserFunction<T, R> {

    R apply(T t) throws JSQLParserException;
}
