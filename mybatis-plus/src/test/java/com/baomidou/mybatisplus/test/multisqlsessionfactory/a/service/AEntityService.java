package com.baomidou.mybatisplus.test.multisqlsessionfactory.a.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.entity.AEntity;

import java.util.List;

/**
 * @author nieqiurong
 */
public interface AEntityService extends IService<AEntity> {

    void testSaveBath(List<AEntity> aEntityList);

}
