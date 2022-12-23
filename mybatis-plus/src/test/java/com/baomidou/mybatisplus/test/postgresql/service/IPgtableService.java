package com.baomidou.mybatisplus.test.postgresql.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.postgresql.entity.Pgtable;
import org.springframework.stereotype.Service;

@Service
public interface IPgtableService extends IService<Pgtable> {

    void testTask();
}
