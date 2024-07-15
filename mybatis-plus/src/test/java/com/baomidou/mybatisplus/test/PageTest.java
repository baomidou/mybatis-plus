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
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Collections;
import java.util.List;

/**
 * @author nieqiurong 2020/3/20.
 */
class PageTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Gson gson = new Gson();

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testCopy() {
        Page page1 = new Page(2, 10, 100, false);
        page1.setOptimizeCountSql(false);
        page1.setOrders(Collections.singletonList(OrderItem.asc("test")));

        Page page2 = new Page();
        PropertyCopier.copyBeanProperties(Page.class, page1, page2);
        Assertions.assertEquals(page1.getCurrent(), page2.getCurrent());
        Assertions.assertEquals(page1.getTotal(), page2.getTotal());
        Assertions.assertEquals(page1.getSize(), page2.getSize());
        Assertions.assertEquals(page1.optimizeCountSql(), page2.optimizeCountSql());
        Assertions.assertEquals(page1.searchCount(), page2.searchCount());
        Assertions.assertEquals(page1.orders().size(), page2.orders().size());

        Page page3 = new Page();
        BeanUtils.copyProperties(page1, page3);
        Assertions.assertEquals(page1.getCurrent(), page3.getCurrent());
        Assertions.assertEquals(page1.getTotal(), page3.getTotal());
        Assertions.assertEquals(page1.getSize(), page3.getSize());
        Assertions.assertEquals(page1.optimizeCountSql(), page3.optimizeCountSql());
        Assertions.assertEquals(page1.searchCount(), page3.searchCount());
        Assertions.assertEquals(page1.orders().size(), page3.orders().size());

        Page page4 = new Page();
        BeanCopier.create(page1.getClass(), page4.getClass(), false).copy(page1, page4, null);
        //链式的set方法会导致属性BeanCopier的拷贝方法失败  https://github.com/cglib/cglib/issues/108.
        Assertions.assertTrue(page4.optimizeCountSql());
        Assertions.assertTrue(page4.searchCount());
        Assertions.assertEquals(1, page4.getCurrent());
        Assertions.assertEquals(1, page4.orders().size());
        Assertions.assertEquals(10, page4.getSize());
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
        page.setOrders(OrderItem.descs("a","b"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setOrders(OrderItem.ascs("a","b"));
        assertPageDto(page);

        page = new PageDTO<>(1, 10, 100);
        page.setRecords(List.of("1", "2", "3"));
        page.setOrders(OrderItem.ascs("a","b"));
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
            gson.fromJson(gson.toJson(source), tClass),
            JSON.parseObject(JSON.toJSONString(source), tClass),
            // dubbo 反序列化下出现问题  https://github.com/alibaba/fastjson2/issues/2734
            JSONB.parseObject(JSONB.toBytes(source), tClass,
                JSONReader.Feature.FieldBased
            ),
            com.alibaba.fastjson.JSON.parseObject(com.alibaba.fastjson.JSON.toJSONString(source), tClass)
        );
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
