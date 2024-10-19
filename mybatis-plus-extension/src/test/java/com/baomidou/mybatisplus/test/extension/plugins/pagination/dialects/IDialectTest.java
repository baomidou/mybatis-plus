package com.baomidou.mybatisplus.test.extension.plugins.pagination.dialects;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2022-05-30
 */
@Slf4j
class IDialectTest {

    /**
     * 检查 IDialect 的实现类生成的sql不要老是有重复,同样实现类的sql类型只需要留一个
     */
    @Test
    void singleRealize() {
        List<Class<?>> classList = ReflectionUtils.findAllClassesInPackage(
            "com.baomidou.mybatisplus.extension.plugins.pagination.dialects",
            i -> !i.isInterface() && IDialect.class.isAssignableFrom(i), i -> true);

        Map<String, List<Class<?>>> map = new ConcurrentHashMap<>();
        classList.forEach(i -> {
            IDialect o = (IDialect) ReflectionUtils.newInstance(i);
            DialectModel model = o.buildPaginationSql("select * from table", 1, 10);
            String sql = model.getDialectSql();
            if (!map.containsKey(sql)) {
                ArrayList<Class<?>> list = new ArrayList<>();
                list.add(i);
                map.put(sql,list);
            } else {
                map.get(sql).add(i);
            }
        });
        map.forEach((k, v) -> {
            List<Class<?>> list = v.stream().filter(i -> i.getAnnotation(Deprecated.class) == null).collect(Collectors.toList());
            assertThat(list.size()).as(() -> {
                String s = list.stream().map(Class::getSimpleName).collect(Collectors.joining("与"));
                return s + "的sql重复";
            }).isEqualTo(1);
        });
    }
}
