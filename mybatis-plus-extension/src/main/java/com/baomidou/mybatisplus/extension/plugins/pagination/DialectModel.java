package com.baomidou.mybatisplus.extension.plugins.pagination;

import lombok.Data;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author miemie
 * @since 2018-10-31
 */
@Data
public class DialectModel {

    /**
     * 分页方言 sql
     */
    private String dialectSql;
    /**
     * 消费偏移量
     */
    private Consumer<List<ParameterMapping>> offsetConsumer = i -> {
    };
    /**
     * 消费范围量
     */
    private Consumer<List<ParameterMapping>> limitConsumer = i -> {
    };
}
