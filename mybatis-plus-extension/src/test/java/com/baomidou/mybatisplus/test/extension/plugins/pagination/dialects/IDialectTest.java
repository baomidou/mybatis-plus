package com.baomidou.mybatisplus.test.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2022-05-30
 */
class IDialectTest {

    /**
     * 已登记的分页 SQL 重复组，组内类名按字典序用"与"拼接。
     * <p>
     * 新增重复组必须在这里显式登记并说明理由；登记项对应的重复消失时本测试同样会失败，避免白名单留下过期条目。
     */
    private static final List<String> acceptedDuplicates = List.of(
            // GaussDB 分页语法与 MySQL 同为 "LIMIT [offset,] count"，两份实现逐字符一致，属方言复用而非疏漏。
            "GaussDBDialect与MySqlDialect");

    /**
     * 检查 IDialect 的实现类生成的sql不要老是有重复,同样实现类的sql类型只需要留一个
     */
    @Test
    void singleRealize() {
        List<Class<?>> classList = ReflectionUtils.findAllClassesInPackage(
                "com.baomidou.mybatisplus.extension.plugins.pagination.dialects",
                i -> !i.isInterface() && IDialect.class.isAssignableFrom(i), i -> true);
        // 扫描失效时分组结果恒为空，会让重复断言假绿，必须先确认确实扫到了实现类。
        assertThat(classList).isNotEmpty();

        List<String> duplicates = classList.stream().filter(i -> i.getAnnotation(Deprecated.class) == null)
                .collect(
                        Collectors.groupingBy(
                                IDialectTest::paginationSql, TreeMap::new,
                                Collectors.mapping(Class::getSimpleName, Collectors.toList())))
                .values().stream().filter(simpleNames -> simpleNames.size() > 1)
                .map(simpleNames -> simpleNames.stream().sorted().collect(Collectors.joining("与"))).sorted()
                .collect(Collectors.toList());

        assertThat(duplicates).containsExactlyInAnyOrderElementsOf(acceptedDuplicates);
    }

    private static String paginationSql(Class<?> dialectClass) {
        IDialect dialect = (IDialect) ReflectionUtils.newInstance(dialectClass);
        return dialect.buildPaginationSql("select * from table", 1, 10).getDialectSql();
    }

}
