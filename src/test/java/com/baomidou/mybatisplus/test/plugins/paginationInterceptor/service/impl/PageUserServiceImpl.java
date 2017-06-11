package com.baomidou.mybatisplus.test.plugins.paginationInterceptor.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.entity.PageUser;
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.mapper.PageUserMapper;
import com.baomidou.mybatisplus.test.plugins.paginationInterceptor.service.PageUserService;

@Service
public class PageUserServiceImpl extends ServiceImpl<PageUserMapper, PageUser> implements PageUserService {

}
