package com.baomidou.mybatisplus.core.toolkit;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.core.toolkit.support.Property;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import lombok.Getter;

public class LambdaUtilsTest {

    @Test
    public void testResolve() {
        SerializedLambda lambda = LambdaUtils.resolve(TestPojo::getId);
        Assert.assertEquals(TestPojo.class.getName(), lambda.getImplClass().replace("/", "."));
        Assert.assertEquals("getId", lambda.getImplMethodName());
        Assert.assertEquals("id", TableInfoHelper.toColumn(TestPojo::getId));

        Cond<TestPojo> cond = new Cond<>();
        System.out.println(cond.eq(TestPojo::getId, 123).toString());
    }

    @Getter
    private class TestPojo {
        private int id;
    }

    private class Cond<T> {
        private StringBuilder sb = new StringBuilder();
        private Cond<T> eq(Property<T, ?> prop, Object val) {
            SerializedLambda lambda = LambdaUtils.resolve(prop);
            this.sb.append(lambda.getImplMethodName()).append(" = ").append(val);
            return this;
        }
        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
