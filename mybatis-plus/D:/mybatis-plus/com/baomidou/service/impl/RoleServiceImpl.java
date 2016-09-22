package com.baomidou.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mapper.RoleMapper;
import com.baomidou.entity.Role;
import com.baomidou.service.IRoleService;
import com.baomidou.framework.service.impl.SuperServiceImpl;

/**
 *
 * Role 表数据服务层接口实现类
 *
 */
@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements IRoleService {


}