package com.baomidou.mybatisplus.test.plugin.OptimisticLocker.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.entity.IntVersionUser;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.mapper.IntVersionUserMapper;
import com.baomidou.mybatisplus.test.plugin.OptimisticLocker.service.IntVersionUserService;

@Service
public class IntVersionUserServiceImpl extends ServiceImpl<IntVersionUserMapper, IntVersionUser> implements IntVersionUserService {

}
