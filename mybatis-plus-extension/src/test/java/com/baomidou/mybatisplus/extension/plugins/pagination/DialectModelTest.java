package com.baomidou.mybatisplus.extension.plugins.pagination;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.apache.ibatis.mapping.ParameterMapping;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

/**
 * {@link DialectModel} 是"动态入参"分页方言的传递袋：SQL 里的 {@code limit ?,?} 要靠它 往 {@code ParameterMapping} 追加
 * {@code mybatis_plus_first}/{@code mybatis_plus_second} 两个占位， 再往 {@code additionalParameters} 放对应的偏移量与行数。方言本身只给字符串，
 * 参数能不能对上全看这个袋子，而它此前没有任何直接用例。
 */
class DialectModelTest {

    private static final String FIRST = "mybatis_plus_first";
    private static final String SECOND = "mybatis_plus_second";

    @Test
    void modelWithoutConsumersAddsNoBindingAtAll() {
        DialectModel model = new DialectModel("select * from t limit ?,?");
        List<ParameterMapping> mappings = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        model.consumers(mappings, new MybatisConfiguration(), parameters);
        assertThat(model.getDialectSql()).isEqualTo("select * from t limit ?,?");
        assertThat(mappings).isEmpty();
        assertThat(parameters).isEmpty();
    }

    @Test
    void firstParamConsumerBindsOnlyTheFirstValue() {
        DialectModel model = new DialectModel("limit ?", 10).setConsumer(true);
        List<ParameterMapping> mappings = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        model.consumers(mappings, new MybatisConfiguration(), parameters);
        assertThat(mappings).hasSize(1);
        assertThat(mappings.get(0).getProperty()).isEqualTo(FIRST);
        assertThat(mappings.get(0).getJavaType()).isEqualTo(long.class);
        assertThat(parameters).containsExactly(entry(FIRST, 10L));
    }

    @Test
    void secondParamConsumerBindsOnlyTheSecondValue() {
        DialectModel model = new DialectModel("limit ?", 10, 20).setConsumer(false);
        List<ParameterMapping> mappings = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        model.consumers(mappings, new MybatisConfiguration(), parameters);
        assertThat(mappings).hasSize(1);
        assertThat(mappings.get(0).getProperty()).isEqualTo(SECOND);
        assertThat(parameters).containsExactly(entry(SECOND, 20L));
    }

    @Test
    void consumerChainBindsBothValuesInOrder() {
        DialectModel model = new DialectModel("limit ?,?", 10, 20);
        assertThat(model.setConsumerChain()).isSameAs(model);
        List<ParameterMapping> mappings = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        model.consumers(mappings, new MybatisConfiguration(), parameters);
        assertThat(mappings).extracting(ParameterMapping::getProperty).containsExactly(FIRST, SECOND);
        assertThat(parameters).containsExactlyInAnyOrderEntriesOf(Map.of(FIRST, 10L, SECOND, 20L));
    }

    @Test
    void indexedConsumerInsertsTheMappingAtTheReturnedPosition() {
        DialectModel model = new DialectModel("limit ?,?", 10, 20).setConsumer(true, list -> 1);
        List<ParameterMapping> mappings = new ArrayList<>();
        mappings.add(existing("a"));
        mappings.add(existing("b"));
        model.consumers(mappings, new MybatisConfiguration(), new HashMap<>());
        assertThat(mappings).extracting(ParameterMapping::getProperty).containsExactly("a", FIRST, "b");
    }

    @Test
    void nullArgumentsAreRejectedOneByOne() {
        DialectModel model = new DialectModel("limit ?,?", 10, 20).setConsumerChain();
        MybatisConfiguration configuration = new MybatisConfiguration();
        List<ParameterMapping> mappings = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        assertThatThrownBy(() -> model.consumers(null, configuration, parameters))
                .isInstanceOf(MybatisPlusException.class).hasMessage("parameterMappings must notNull !");
        assertThatThrownBy(() -> model.consumers(mappings, null, parameters)).isInstanceOf(MybatisPlusException.class)
                .hasMessage("configuration must notNull !");
        assertThatThrownBy(() -> model.consumers(mappings, configuration, null))
                .isInstanceOf(MybatisPlusException.class).hasMessage("additionalParameters must notNull !");
    }

    private static ParameterMapping existing(String property) {
        return new ParameterMapping.Builder(new MybatisConfiguration(), property, String.class).build();
    }
}
