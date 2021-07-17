package com.baomidou.mybatisplus.test.autoconfigure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author miemie
 * @since 2020-05-27
 */
@Mapper
public interface SampleMapper extends BaseMapper<Sample> {
}
