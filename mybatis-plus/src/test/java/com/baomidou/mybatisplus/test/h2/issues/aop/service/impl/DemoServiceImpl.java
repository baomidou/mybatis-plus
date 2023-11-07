package com.baomidou.mybatisplus.test.h2.issues.aop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.issues.aop.entity.Demo;
import com.baomidou.mybatisplus.test.h2.issues.aop.mapper.DemoMapper;
import com.baomidou.mybatisplus.test.h2.issues.aop.service.IDemoService;
import org.springframework.stereotype.Service;

/**
 * @author nieqiurong
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements IDemoService {

}
