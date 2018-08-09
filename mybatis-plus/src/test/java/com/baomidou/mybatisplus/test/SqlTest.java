package com.baomidou.mybatisplus.test;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SqlTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testPageJsonDecode() throws Exception {
        String json = "{\"current\":2,\"size\":9,\"ascs\":[\"name\",\"age\",\"qiuqiu\"]}";
        Page page = mapper.readValue(json, Page.class);
        Assert.assertEquals(2, page.getCurrent());
        Assert.assertEquals(9, page.getSize());
        Assert.assertEquals(3, page.ascs().length);
    }
}
