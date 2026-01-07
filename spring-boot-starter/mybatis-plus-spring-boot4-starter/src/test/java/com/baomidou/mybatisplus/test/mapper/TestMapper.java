package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.entity.Test;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nieqiurong 2019/5/4.
 */
@Mapper
public interface TestMapper extends BaseMapper<Test> {

}
