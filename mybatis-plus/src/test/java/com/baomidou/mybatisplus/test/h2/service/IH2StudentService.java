package com.baomidou.mybatisplus.test.h2.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;

/**
 * Created by nieqiuqiu on 2018/9/6.
 */
public interface IH2StudentService extends IService<H2Student> {

    void testTransactional();

    void testSqlRunnerTransactional();
}
