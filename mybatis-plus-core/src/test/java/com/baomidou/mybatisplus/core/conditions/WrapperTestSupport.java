package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Data;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * core 里 wrapper 测试共用的基类：三个 log* 方法边打印边断言，{@link Entity} 是它们公用的实体夹具。 本身不含用例，所以不叫 {@code *Test}。
 *
 * @author miemie
 * @since 2021-01-27
 */
public class WrapperTestSupport {

    protected void logParams(AbstractWrapper<?, ?, ?> wrapper) {
        wrapper.getParamNameValuePairs().forEach((k, v) -> {
            System.out.println("key: '" + k + "'\t\tvalue: '" + v + StringPool.SINGLE_QUOTE);
            assertThat(k).startsWith(Constants.WRAPPER_PARAM);
        });
    }

    protected void logSqlSet(String explain, AbstractWrapper<?, ?, ?> wrapper, String targetSql) {
        System.out.printf(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓%n", explain);
        System.out.println(wrapper.getSqlSet());
        Assertions.assertThat(wrapper.getSqlSet().trim()).isEqualTo(targetSql);
    }

    protected void logSqlWhere(String explain, AbstractWrapper<?, ?, ?> wrapper, String targetSql) {
        System.out.printf(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓%n", explain);
        System.out.println(wrapper.getSqlSegment());
        assertThat(wrapper.getTargetSql().trim()).isEqualTo(targetSql);
    }

    @Data
    protected static class Entity {

        private Integer id;

        @TableField("username")
        private String name;

        private Integer roleId;
    }
}
