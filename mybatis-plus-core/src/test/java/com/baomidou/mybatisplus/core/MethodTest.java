package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodTest {

    static class TestMethod extends AbstractMethod {

        public TestMethod() {
        }

        public TestMethod(String methodName) {
            super(methodName);
        }

        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            return null;
        }

        @Override
        public String getMethod(SqlMethod sqlMethod) {
            return "testMethod";
        }

    }

    @Test
    void test(){
        Assertions.assertEquals(new Insert().getMethod(SqlMethod.INSERT_ONE),"insert");
        Assertions.assertEquals(new Insert("testInsert").getMethod(SqlMethod.INSERT_ONE),"testInsert");
        Assertions.assertEquals(new TestMethod().getMethod(SqlMethod.INSERT_ONE),"testMethod");
        //这里方法被复写了,用复写的方法为准
        Assertions.assertEquals(new TestMethod("xxxx").getMethod(SqlMethod.INSERT_ONE),"testMethod");
    }

}
