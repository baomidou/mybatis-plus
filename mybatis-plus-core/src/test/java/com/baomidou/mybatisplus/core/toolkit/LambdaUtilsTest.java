package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.toolkit.support.Property;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

public class LambdaUtilsTest {

    @Test
    public void testResolve() {
        SerializedLambda lambda = LambdaUtils.resolve(TestPojo::getId);
        Assert.assertEquals(TestPojo.class.getName(), lambda.getImplClass().replace("/", "."));
        Assert.assertEquals("getId", lambda.getImplMethodName());
        Assert.assertEquals("id", StringUtils.resolveFieldName(lambda.getImplMethodName()));

        Cond<TestPojo> cond = new Cond<>();
        System.out.println(cond.eq(TestPojo::getId, 123).toString());

        // 如果连着写，必须指定后者的泛型
        new Cond<>().eq(TestPojo::getId, 123)
            .eq(TestPojo::getId, 123)
            .eq(TestPojo::getId, 123);

        new Cond<TestPojo>() {{
            eq(TestPojo::getId, 123);
            eq(TestPojo::getId, 123);
            eq(TestPojo::getId, 456);
        }};
    }

    @Getter
    private class TestPojo {

        private int id;
    }

    public class Cond<T> {

        private StringBuilder sb = new StringBuilder();

        // 这个 TYPE 类型和 T 就没有关系了 ；
        // 如果有需要的话，使用 extends 来建立关系，保证class一致，稍微做点编写检查
        <TYPE extends T> Cond<T> eq(Property<TYPE, ?> prop, Object val) {
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
