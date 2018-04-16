package com.baomidou.test.service.impl;

import com.baomidou.test.entity.User;
import com.baomidou.test.mapper.UserMapper;
import com.baomidou.test.service.IUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hubin
 * @since 2018-03-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
