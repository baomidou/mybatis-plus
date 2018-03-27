package com.baomidou.mybatisplus.extension.test.h2.service;

import com.baomidou.mybatisplus.extension.test.h2.entity.persistent.H2UserVersionIntWithAR;

/**
 * <p>
 * AR模式的service
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/3/15
 */
public interface IH2UserVersionIntWithARService {

    public void updateLogicDeletedRecord(Long id);

    public H2UserVersionIntWithAR selectByIdWithoutLogicDeleteLimit(Long id);

}
