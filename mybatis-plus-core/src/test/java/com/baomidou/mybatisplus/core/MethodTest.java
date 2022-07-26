package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.methods.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.apache.ibatis.mapping.MappedStatement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MethodTest {

    static class TestMethod extends AbstractMethod {

        public TestMethod(){
            this("TestMethod");
        }
        public TestMethod(String methodName) {
            super(methodName);
        }

        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            return null;
        }

    }

    @Test
    void test(){
        Stream.Builder<AbstractMethod> builder = Stream.<AbstractMethod>builder()
            .add(new Insert())
            .add(new Delete())
            .add(new Update())
            .add(new SelectPage())
            .add(new TestMethod());
        List<AbstractMethod> collect = builder.build().collect(toList());
        Assert.isTrue(collect.size() == 5, "创建失败！");
    }

}
