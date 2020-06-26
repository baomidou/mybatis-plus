package com.baomidou.mybatisplus.test.h2.cache.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;

public interface ICacheService extends IService<CacheModel> {

    long testBatchTransactionalClear1();

    long testBatchTransactionalClear2();

    long testBatchTransactionalClear3();

    long testBatchTransactionalClear4();

    long testBatchTransactionalClear5();

    long testBatchTransactionalClear6();

    long testBatchTransactionalClear7();
}
