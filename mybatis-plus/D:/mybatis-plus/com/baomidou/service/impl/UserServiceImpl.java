package com.baomidou.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mapper.UserMapper;
import com.baomidou.entity.User;
import com.baomidou.service.IUserService;
import com.baomidou.framework.service.impl.SuperServiceImpl;

/**
 *
 * User 表数据服务层接口实现类
 *
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements IUserService {


}