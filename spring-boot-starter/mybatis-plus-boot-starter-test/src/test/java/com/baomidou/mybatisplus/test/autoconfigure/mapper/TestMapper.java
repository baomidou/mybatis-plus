package com.baomidou.mybatisplus.test.autoconfigure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.autoconfigure.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jam804
 * @since 2024-08-22
 */

@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {
}
