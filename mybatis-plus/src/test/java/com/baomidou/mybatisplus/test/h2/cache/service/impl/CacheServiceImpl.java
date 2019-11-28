package com.baomidou.mybatisplus.test.h2.cache.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.cache.mapper.CacheMapper;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import com.baomidou.mybatisplus.test.h2.cache.service.ICacheService;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl extends ServiceImpl<CacheMapper, CacheModel> implements ICacheService {

}
