package com.baomidou.mybatisplus.core.toolkit;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import lombok.Getter;

public class LambdaUtilsTest {

    @Test
    public void testResolve() {
        SerializedLambda lambda = LambdaUtils.resolve(TestPojo::getId);
        Assert.assertEquals(TestPojo.class.getName(), lambda.getImplClass().replace("/", "."));
        Assert.assertEquals("getId", lambda.getImplMethodName());
    }

    @Getter
    private class TestPojo {

        private int id;
    }

}
