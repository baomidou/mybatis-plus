package com.baomidou.mybatisplus.extension.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @author liuzhongbo
 * date 2020/11/30
 */
public class ServiceImplTest {

    private static class Entity {

    }

    private interface EntityMapper extends BaseMapper<Entity> {

    }

    private static class EntityService extends ServiceImpl<EntityMapper, Entity> implements IService<Entity> {

    }

    private static class ParameterizedSubEntityService<M, T> extends EntityService {

    }

    private static class SubParameterizedSubEntityService extends ParameterizedSubEntityService<Object, Integer> {

    }

    private static class SubEntityService extends EntityService {

    }

    @Test
    void testGetTargetSuperClassGenericType() {
        Assertions.assertSame(ReflectionKit.getTargetSuperClassGenericType(SubEntityService.class, ServiceImpl.class, 0), EntityMapper.class);
        Assertions.assertSame(ReflectionKit.getTargetSuperClassGenericType(SubEntityService.class, ServiceImpl.class, 1), Entity.class);
        Assertions.assertSame(ReflectionKit.getTargetSuperClassGenericType(EntityService.class, ServiceImpl.class, 0), EntityMapper.class);
        Assertions.assertSame(ReflectionKit.getTargetSuperClassGenericType(EntityService.class, ServiceImpl.class, 1), Entity.class);
        Assertions.assertSame(ReflectionKit.getTargetSuperClassGenericType(SubParameterizedSubEntityService.class, ServiceImpl.class, 0), EntityMapper.class);
        Assertions.assertSame(ReflectionKit.getTargetSuperClassGenericType(SubParameterizedSubEntityService.class, ServiceImpl.class, 1), Entity.class);
    }

}
