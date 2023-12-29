package com.baomidou.mybatisplus.test.autoconfigure.dynamic_strategy;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2023/12/29
 */
@Service
public class SampleUserServiceImpl extends ServiceImpl<SampleUserMapper, SampleUser> implements SampleUserService{
}
