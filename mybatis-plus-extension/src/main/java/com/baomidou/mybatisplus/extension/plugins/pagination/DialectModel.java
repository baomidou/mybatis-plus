package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分页参数动态化所需 model
 *
 * @author miemie
 * @since 2018-10-31
 */
@Getter
@Accessors(chain = true)
public class DialectModel {
    public static final String OFFSET_NAME = "mybatis_plus_offset";
    public static final String LIMIT_NAME = "mybatis_plus_limit";

    /**
     * 分页方言 sql
     */
    @Setter
    private String dialectSql;
    /**
     * 提供 Configuration
     */
    @Getter(AccessLevel.NONE)
    private Supplier<Configuration> configurationSupplier = () -> null;
    /**
     * 提供 List<ParameterMapping>
     */
    @Getter(AccessLevel.NONE)
    private Supplier<List<ParameterMapping>> mappingsSupplier = () -> null;
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

    /**
     * 设置消费
     * <p>
     * 带下标的
     *
     * @param isOffset 是否是 offset
     * @param function 提供 index
     * @return this
     */
    public DialectModel setConsumer(boolean isOffset, Function<List<ParameterMapping>, Integer> function) {
        if (isOffset) {
            offsetConsumer = i -> i.add(function.apply(mappingsSupplier.get()),
                new ParameterMapping.Builder(configurationSupplier.get(), OFFSET_NAME, long.class).build());
        } else {
            limitConsumer = i -> i.add(function.apply(mappingsSupplier.get()),
                new ParameterMapping.Builder(configurationSupplier.get(), LIMIT_NAME, long.class).build());
        }
        return this;
    }

    /**
     * 设置消费
     * <p>
     * 不带下标的
     *
     * @param isOffset 是否是 offset
     * @return this
     */
    public DialectModel setConsumer(boolean isOffset) {
        if (isOffset) {
            offsetConsumer = i -> i.add(new ParameterMapping.Builder(configurationSupplier.get(),
                OFFSET_NAME, long.class).build());
        } else {
            limitConsumer = i -> i.add(new ParameterMapping.Builder(configurationSupplier.get(),
                LIMIT_NAME, long.class).build());
        }
        return this;
    }

    /**
     * 消费掉
     *
     * @param mappings      ParameterMapping 集合
     * @param configuration Configuration
     */
    public void consumers(List<ParameterMapping> mappings, Configuration configuration) {
        Assert.notNull(mappings, "List<ParameterMapping> must not be null!");
        Assert.notNull(configuration, "configuration must not be null!");
        configurationSupplier = () -> configuration;
        mappingsSupplier = () -> mappings;
        offsetConsumer.accept(mappings);
        limitConsumer.accept(mappings);
    }

    /**
     * 存储 offset 和 limit
     *
     * @param offset 偏移量
     * @param limit  范围量
     * @return this
     */
    public DialectModel putToDialectMap(long offset, long limit) {
        dialectMap.put(OFFSET_NAME, offset);
        dialectMap.put(LIMIT_NAME, limit);
        return this;
    }
}
