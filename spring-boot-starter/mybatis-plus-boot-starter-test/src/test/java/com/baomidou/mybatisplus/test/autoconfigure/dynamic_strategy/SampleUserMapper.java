package com.baomidou.mybatisplus.test.autoconfigure.dynamic_strategy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 张治保
 * @since 2023/12/29
 */
@Mapper
public interface SampleUserMapper extends BaseMapper<SampleUser> {
}
