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
    public static final String OFFSET_NAME = "mybatis_plus_first";
    public static final String LIMIT_NAME = "mybatis_plus_second";

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
    private Consumer<List<ParameterMapping>> firstParamConsumer = i -> {
    };
    /**
     * 消费范围量
     */
    private Consumer<List<ParameterMapping>> secondParamConsumer = i -> {
    };
    /**
     * 分页数据参数 map
     */
    @Setter(value = AccessLevel.NONE)
    private Map<String, Long> dialectMap;

    public DialectModel(String dialectSql, long firstParam, long secondParam) {
        this.dialectSql = dialectSql;
        this.putToDialectMap(firstParam, secondParam);
    }

    /**
     * 设置消费
     * <p>
     * 带下标的
     *
     * @return this
     */
    public DialectModel setConsumer(boolean isFirstParam, Function<List<ParameterMapping>, Integer> function) {
        if (isFirstParam) {
            firstParamConsumer = i -> i.add(function.apply(mappingsSupplier.get()),
                new ParameterMapping.Builder(configurationSupplier.get(), OFFSET_NAME, long.class).build());
        } else {
            secondParamConsumer = i -> i.add(function.apply(mappingsSupplier.get()),
                new ParameterMapping.Builder(configurationSupplier.get(), LIMIT_NAME, long.class).build());
        }
        return this;
    }

    /**
     * 设置消费
     * <p>
     * 不带下标的
     *
     * @return this
     */
    public DialectModel setConsumer(boolean isFirstParam) {
        if (isFirstParam) {
            firstParamConsumer = i -> i.add(new ParameterMapping.Builder(configurationSupplier.get(),
                OFFSET_NAME, long.class).build());
        } else {
            secondParamConsumer = i -> i.add(new ParameterMapping.Builder(configurationSupplier.get(),
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
        firstParamConsumer.accept(mappings);
        secondParamConsumer.accept(mappings);
    }

    /**
     * 存储 offset 和 limit
     */
    private void putToDialectMap(long firstParam, long secondParam) {
        dialectMap = new HashMap<>(2);
        dialectMap.put(OFFSET_NAME, firstParam);
        dialectMap.put(LIMIT_NAME, secondParam);
    }
}
