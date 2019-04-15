/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.service.impl;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.baomidou.mybatisplus.test.h2.mapper.H2StudentMapper;
import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.service.IH2StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2018/9/6.
 */
@Service
public class H2StudentServiceImpl extends ServiceImpl<H2StudentMapper,H2Student> implements IH2StudentService {

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testTransactional() {
        new H2Student(null, "tx1", 2).insert();
        new H2Student(null, "tx2", 2).insert();
        new H2Student(null, "tx3", 2).insert();
        throw new MybatisPlusException("测试AR事务回滚");
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSqlRunnerTransactional() {
        SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","sqlRunnerTx1",2);
        SqlRunner.db().insert("INSERT INTO h2student ( name, age ) VALUES ( {0}, {1} )","sqlRunnerTx2",2);
        throw new MybatisPlusException("测试普通插入事务回滚");
    }
}
