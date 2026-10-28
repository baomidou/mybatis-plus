package com.baomidou.mybatisplus.test.h2.cache.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;

public interface ICacheService extends IService<CacheModel> {

    long batchTransactionalClear1();

    long batchTransactionalClear2();

    long batchTransactionalClear3();

    long batchTransactionalClear4();

    long batchTransactionalClear5();

    long batchTransactionalClear6();

    long batchTransactionalClear7();

    //    boolean testCustomSaveOrUpdateBatch();
}
