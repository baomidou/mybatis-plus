package com.baomidou.mybatisplus.test.framework.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.baomidou.mybatisplus.test.mysql.UserMapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;

@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements IUserService {
	
	/**
	 * 测试事务
	 */
	@Transactional
	@Override
	public boolean insert(User entity) {
		return super.insert(entity);
	}
	
}
