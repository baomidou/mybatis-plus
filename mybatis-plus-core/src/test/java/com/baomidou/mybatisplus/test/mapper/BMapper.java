package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.entity.BEntity;

public interface BMapper extends BaseMapper<BEntity> {

    BEntity test();

}
