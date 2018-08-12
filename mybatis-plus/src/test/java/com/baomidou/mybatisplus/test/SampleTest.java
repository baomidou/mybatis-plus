package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class SampleTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testPageJsonDecode() throws Exception {
        String json = "{\"current\":2,\"pages\":1222,\"total\":10,\"size\":5,\"ascs\":[\"name\",\"age\",\"qiuqiu\"]}";
        Page page = mapper.readValue(json, Page.class);
        Assert.assertEquals(2, page.getCurrent());
        Assert.assertEquals(5, page.getSize());
        Assert.assertEquals(3, page.ascs().length);
        Assert.assertEquals(2, page.getPages());
    }
}
