package com.baomidou.mybatisplus.test.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxyFactory;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

        public DemoServiceImpl(BaseMapper<Demo> baseMapper) {
            super.baseMapper = (DemoMapper) baseMapper;
        }
    }

    static class DemoServiceExtend extends DemoServiceImpl {

        public DemoServiceExtend(BaseMapper<Demo> baseMapper) {
            super(baseMapper);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void genericTest() {
        MybatisMapperProxyFactory<? extends BaseMapper<?>> mybatisMapperProxyFactory = new MybatisMapperProxyFactory<>(DemoMapper.class);
        BaseMapper<Demo> baseMapper = (BaseMapper<Demo>) mybatisMapperProxyFactory.newInstance(Mockito.mock(SqlSession.class));
        IService<Demo>[] services = new IService[]{new DemoServiceImpl(baseMapper), new DemoServiceExtend(baseMapper)};
        for (IService<Demo> service : services) {
            ServiceImpl<?,?> impl = (ServiceImpl<?,?>) service;
            Assertions.assertEquals(Demo.class, impl.getEntityClass());
            Assertions.assertEquals(DemoMapper.class, impl.getMapperClass());
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
