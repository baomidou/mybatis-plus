package com.baomidou.mybatisplus.extension.test.h2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.test.h2.entity.mapper.H2UserVersionIntWithARMapper;
import com.baomidou.mybatisplus.extension.test.h2.entity.persistent.H2UserVersionIntWithAR;
import com.baomidou.mybatisplus.extension.test.h2.service.IH2UserVersionIntWithARService;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/3/15
 */
@Service
public class H2UserVersionIntWithARServiceImpl implements IH2UserVersionIntWithARService {

    @Autowired
    H2UserVersionIntWithARMapper h2UserVersionIntWithARMapper;

    @Override
    @Transactional
    public void updateLogicDeletedRecord(Long id) {
        h2UserVersionIntWithARMapper.updateLogicDeletedRecord(id);
    }

    @Override
    public H2UserVersionIntWithAR selectByIdWithoutLogicDeleteLimit(Long id) {
        return h2UserVersionIntWithARMapper.selectByIdWithoutLogicDeleteLimit(id);
    }
}
