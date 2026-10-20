package com.baomidou.mybatisplus.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/3/20.
 */
class PageTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Gson gson = new Gson();

    /**
     * 特征化断言：Page 的属性访问方法不是 JavaBean 命名（orders()、optimizeCountSql()、searchCount()、maxLimit()、 countId() 都没有 is/get
     * 前缀），四个标准属性又用链式 setter，于是基于 JavaBean 内省的拷贝工具会静默丢属性。
     * <p>
     * 三种拷贝方式的实测结果：MyBatis 的 PropertyCopier 全量拷贝；Spring BeanUtils 只拷 current、total、size、records； cglib BeanCopier 因为
     * setter 有返回值，一个都拷不动。跨层传递分页结果请改用 PageDTO，它有标准 getter。
     */
    @Test
    void copyPageWithDifferentCopiers() {
        Page<?> page1 = new Page<>(2, 10, 100, false);
        page1.setOptimizeCountSql(false);
        page1.setOrders(Collections.singletonList(OrderItem.asc("test")));
        page1.setCountId("cid");
        page1.setMaxLimit(999L);

        Page<?> page2 = new Page<>();
        PropertyCopier.copyBeanProperties(Page.class, page1, page2);
        assertThat(page2.getCurrent()).isEqualTo(2L);
        assertThat(page2.getTotal()).isEqualTo(100L);
        assertThat(page2.getSize()).isEqualTo(10L);
        assertThat(page2.orders()).hasSize(1);
        assertThat(page2.orders().get(0).getColumn()).isEqualTo("test");
        assertThat(page2.optimizeCountSql()).isFalse();
        assertThat(page2.searchCount()).isFalse();
        assertThat(page2.countId()).isEqualTo("cid");
        assertThat(page2.maxLimit()).isEqualTo(999L);

        Page<?> page3 = new Page<>();
        BeanUtils.copyProperties(page1, page3);
        assertThat(page3.getCurrent()).isEqualTo(2L);
        assertThat(page3.getTotal()).isEqualTo(100L);
        assertThat(page3.getSize()).isEqualTo(10L);
        // 以下属性读方法不符合 JavaBean 规范，Spring 内省不到，拷贝后仍是默认值
        assertThat(page3.orders()).isEmpty();
        assertThat(page3.optimizeCountSql()).isTrue();
        assertThat(page3.searchCount()).isTrue();
        assertThat(page3.countId()).isNull();
        assertThat(page3.maxLimit()).isNull();

        Page<?> page4 = new Page<>();
        BeanCopier.create(page1.getClass(), page4.getClass(), false).copy(page1, page4, null);
        // cglib 只认 void setter，链式 setter 让它拷不出任何属性 https://github.com/cglib/cglib/issues/108
        assertThat(page4.getCurrent()).isEqualTo(1L);
        assertThat(page4.getTotal()).isZero();
        assertThat(page4.orders()).isEmpty();
        assertThat(page4.optimizeCountSql()).isTrue();
        assertThat(page4.searchCount()).isTrue();
    }

    @Test
    void testPageToJson() throws JsonProcessingException {
        var page = new Page<>(1, 10, 2000);
        page.setOrders(List.of(OrderItem.asc("a")));
        //page无法序列化排序等其他属性 {"records":[],"total":2000,"size":10,"current":1,"pages":200}
        assertPage(page);
    }

    @Test
    void testPageDtoToJson() throws JsonProcessingException {
        var page = new PageDTO<>(1, 10, 100);
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setOptimizeCountSql(false);
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setSearchCount(false);
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setOptimizeJoinOfCountSql(false);
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setRecords(List.of("1", "2", "3"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setRecords(List.of("1", "2", "3"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setMaxLimit(1000L);
        page.setRecords(List.of("1", "2", "3"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setRecords(List.of("1", "2", "3"));
        page.setCountId("123");
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setOrders(OrderItem.descs("a", "b"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setOrders(OrderItem.ascs("a", "b"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setRecords(List.of("1", "2", "3"));
        page.setOrders(OrderItem.ascs("a", "b"));
        assertPageDto(page);

    }

    private void assertPage(Page<?> source) throws JsonProcessingException {
        toConvert(source, Page.class).forEach(target -> {
            Assertions.assertEquals(source.getCurrent(), target.getCurrent());
            Assertions.assertEquals(source.getTotal(), target.getTotal());
            Assertions.assertEquals(source.getSize(), target.getSize());
            Assertions.assertEquals(source.countId(), target.countId());
            Assertions.assertEquals(source.getRecords().size(), target.getRecords().size());
            Assertions.assertEquals(source.getPages(), target.getPages());
        });
    }

    private <T extends IPage<?>> List<T> toConvert(T source, Class<T> tClass) throws JsonProcessingException {
        return List.of(
                objectMapper.readValue(objectMapper.writeValueAsString(source), tClass),
                gson.fromJson(gson.toJson(source), tClass), JSON.parseObject(JSON.toJSONString(source), tClass),
                // dubbo 反序列化下出现问题  https://github.com/alibaba/fastjson2/issues/2734
                JSONB.parseObject(JSONB.toBytes(source), tClass, JSONReader.Feature.FieldBased),
                com.alibaba.fastjson.JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(source), tClass));
    }

    private void assertPageDto(PageDTO<?> source) throws JsonProcessingException {
        toConvert(source, PageDTO.class).forEach(target -> {
            Assertions.assertEquals(source.toString(), target.toString());
            Assertions.assertEquals(source.getCurrent(), target.getCurrent());
            Assertions.assertEquals(source.getTotal(), target.getTotal());
            Assertions.assertEquals(source.getSize(), target.getSize());
            Assertions.assertEquals(source.countId(), target.getCountId());
            Assertions.assertEquals(source.countId(), target.countId());
            Assertions.assertEquals(source.searchCount(), target.isSearchCount());
            Assertions.assertEquals(source.searchCount(), target.searchCount());
            Assertions.assertEquals(source.optimizeCountSql(), target.isOptimizeCountSql());
            Assertions.assertEquals(source.optimizeCountSql(), target.optimizeCountSql());
            Assertions.assertEquals(source.optimizeJoinOfCountSql(), target.optimizeJoinOfCountSql());
            Assertions.assertEquals(source.optimizeJoinOfCountSql(), target.isOptimizeJoinOfCountSql());
            Assertions.assertEquals(source.getRecords().size(), target.getRecords().size());
            Assertions.assertEquals(source.maxLimit(), target.getMaxLimit());
            Assertions.assertEquals(source.maxLimit(), target.maxLimit());
            Assertions.assertEquals(source.getOrders().size(), target.getOrders().size());
            Assertions.assertEquals(source.getOrders().size(), target.orders().size());
            Assertions.assertEquals(source.getPages(), target.getPages());
        });
    }

}
