package com.baomidou.mybatisplus.test.multisqlsessionfactory.a.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.entity.AEntity;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.mapper.AEntityMapper;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.a.service.AEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * @author nieqiurong
 */
@Service
public class AEntityServiceImpl extends ServiceImpl<AEntityMapper, AEntity> implements AEntityService {

    @Autowired
    protected TransactionTemplate transactionTemplateA;

    @Override
    @Transactional(rollbackFor = RuntimeException.class, transactionManager = "transactionManagerA")
    public void testSaveBath(List<AEntity> aEntityList) {
        transactionTemplateA.execute((c) -> {
            this.saveBatch(aEntityList);
            return null;
        });
    }

}
