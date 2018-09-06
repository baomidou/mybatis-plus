package com.baomidou.mybatisplus.test.h2.service.impl;

import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2018/9/6.
 */
@Service
public class H2StudentServiceImpl implements IH2StudentService {

    @Override
    @Transactional
    public void testTransactional() {
        new H2Student(null, "tx1", 2).insert();
        new H2Student(null, "tx2", 2).insert();
        new H2Student(null, "tx3", 2).insert();
        throw new RuntimeException("测试AR事务回滚");
    }
}
