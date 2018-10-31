package com.baomidou.mybatisplus.extension.plugins.pagination;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 分页参数动态化所需 model
 *
 * @author miemie
 * @since 2018-10-31
 */
@Data
@Accessors(chain = true)
public class DialectModel {
    public static final String OFFSET_NAME = "mybatis_plus_offset";
    public static final String LIMIT_NAME = "mybatis_plus_limit";

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
    /**
     * 分页数据参数 map
     */
    @Setter(value = AccessLevel.NONE)
    private Map<String, Long> dialectMap = new HashMap<>(2);

    public DialectModel putToDialectMap(long offset, long limit) {
        dialectMap.put(OFFSET_NAME, offset);
        dialectMap.put(LIMIT_NAME, limit);
        return this;
    }
}
