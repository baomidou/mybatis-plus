package com.baomidou.test.service.impl;

import com.baomidou.test.entity.TUser;
import com.baomidou.test.mapper.TUserMapper;
import com.baomidou.test.service.ITUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *   服务实现类
 * </p>
 *
 * @author Yanghu
 * @since 2017-01-11
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {
	
}
