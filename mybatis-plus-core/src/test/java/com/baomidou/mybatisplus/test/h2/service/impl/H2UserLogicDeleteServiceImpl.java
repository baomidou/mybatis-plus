package com.baomidou.mybatisplus.test.h2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserLogicDeleteMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserLogicDelete;
import com.baomidou.mybatisplus.test.h2.service.IH2UserLogicDeleteService;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/15
 */
@Service
public class H2UserLogicDeleteServiceImpl extends ServiceImpl<H2UserLogicDeleteMapper, H2UserLogicDelete> implements IH2UserLogicDeleteService {

    @Autowired
    H2UserLogicDeleteMapper userLogicDeleteMapper;


    @Override
    public H2UserLogicDelete selectByIdMy(Long id) {
        return userLogicDeleteMapper.selectByIdMy(id);
    }
}
