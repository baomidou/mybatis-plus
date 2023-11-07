package com.baomidou.mybatisplus.test.multisqlsessionfactory.b.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.entity.BEntity;

import java.util.List;

/**
 * @author nieqiurong
 */
public interface BEntityService extends IService<BEntity> {

    void testSaveBath(List<BEntity> bEntityList);

}
