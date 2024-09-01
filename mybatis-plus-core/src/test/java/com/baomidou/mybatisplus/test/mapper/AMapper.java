package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.entity.AEntity;

public interface AMapper extends BaseMapper<AEntity> {


    AEntity test();

}
