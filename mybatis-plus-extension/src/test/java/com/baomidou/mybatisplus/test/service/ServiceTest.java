package com.baomidou.mybatisplus.test.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2021/1/19.
 */
public class ServiceTest {

    static class Demo {

    }

    interface DemoMapper extends BaseMapper<Demo> {

    }

    static class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> {

    }

    static class DemoServiceExtend extends DemoServiceImpl {

    }

    @Test
    @SuppressWarnings("unchecked")
    void genericTest() {
        IService<Demo>[] services = new IService[]{new DemoServiceImpl(), new DemoServiceExtend()};
        for (IService<Demo> service : services) {
            Assertions.assertEquals(Demo.class, service.getEntityClass());
            Assertions.assertEquals(DemoMapper.class, ReflectionKit.getFieldValue(service, "mapperClass"));
        }
    }


    static class MyServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    }

    static class MyServiceExtend extends MyServiceImpl<DemoMapper, Demo> {

    }

    @Test
    void testSuperClassGenericType() {
        // 多重继承测试
        assertThat(ReflectionKit.getSuperClassGenericType(MyServiceExtend.class,
            ServiceImpl.class, 0).equals(DemoMapper.class));
        assertThat(ReflectionKit.getSuperClassGenericType(MyServiceExtend.class,
            ServiceImpl.class, 1).equals(Demo.class));
    }
}
