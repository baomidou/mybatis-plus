package com.baomidou.mybatisplus.test.h2.issues.aop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.h2.issues.aop.entity.Demo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nieqiurong
 */
@Mapper
public interface DemoMapper extends BaseMapper<Demo> {

}
