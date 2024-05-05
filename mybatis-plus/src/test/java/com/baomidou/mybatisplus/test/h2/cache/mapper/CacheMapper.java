package com.baomidou.mybatisplus.test.h2.cache.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.h2.cache.CustomCache;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace(implementation = CustomCache.class)
public interface CacheMapper extends BaseMapper<CacheModel> {

}
