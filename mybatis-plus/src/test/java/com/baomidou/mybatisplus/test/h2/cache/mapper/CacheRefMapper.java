package com.baomidou.mybatisplus.test.h2.cache.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import org.apache.ibatis.annotations.CacheNamespaceRef;

@CacheNamespaceRef(CacheMapper.class)
public interface CacheRefMapper extends BaseMapper<CacheModel> {

}
