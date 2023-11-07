package com.baomidou.mybatisplus.test.multisqlsessionfactory.b.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.entity.BEntity;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.mapper.BEntityMapper;
import com.baomidou.mybatisplus.test.multisqlsessionfactory.b.service.BEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * @author nieqiurong
 */
@Service
public class BEntityServiceImpl extends ServiceImpl<BEntityMapper, BEntity> implements BEntityService {

    @Autowired
    private TransactionTemplate transactionTemplateB;

    @Override
    @Transactional(rollbackFor = RuntimeException.class, transactionManager = "transactionManagerB")
    public void testSaveBath(List<BEntity> bEntityList) {
        transactionTemplateB.execute((c) -> {
            saveBatch(bEntityList);
            return null;
        });
    }

}
