package com.baomidou.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mapper.AbcUserMapper;
import com.baomidou.entity.AbcUser;
import com.baomidou.service.IAbcUserService;
import com.baomidou.framework.service.impl.SuperServiceImpl;

/**
 *
 * AbcUser 表数据服务层接口实现类
 *
 */
@Service
public class AbcUserServiceImpl extends SuperServiceImpl<AbcUserMapper, AbcUser> implements IAbcUserService {


}