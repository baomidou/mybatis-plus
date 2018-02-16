package com.baomidou.mybatisplys.extension.test.plugins.paginationInterceptor.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplys.extension.test.plugins.paginationInterceptor.entity.PageUser;
import com.baomidou.mybatisplys.extension.test.plugins.paginationInterceptor.mapper.PageUserMapper;
import com.baomidou.mybatisplys.extension.test.plugins.paginationInterceptor.service.PageUserService;

@Service
public class PageUserServiceImpl extends ServiceImpl<PageUserMapper, PageUser> implements PageUserService {

}
