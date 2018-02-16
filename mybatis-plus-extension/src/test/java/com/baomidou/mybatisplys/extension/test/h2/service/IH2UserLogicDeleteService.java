package com.baomidou.mybatisplys.extension.test.h2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplys.extension.test.h2.entity.persistent.H2UserLogicDelete;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/15
 */
public interface IH2UserLogicDeleteService extends IService<H2UserLogicDelete> {

    H2UserLogicDelete selectByIdMy(Long id);
}
