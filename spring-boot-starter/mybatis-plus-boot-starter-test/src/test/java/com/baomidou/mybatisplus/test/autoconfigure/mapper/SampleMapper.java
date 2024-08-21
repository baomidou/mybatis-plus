package com.baomidou.mybatisplus.test.autoconfigure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.autoconfigure.entity.Sample;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author miemie
 * @since 2020-05-27
 */
@Mapper
public interface SampleMapper extends BaseMapper<Sample> {
}
