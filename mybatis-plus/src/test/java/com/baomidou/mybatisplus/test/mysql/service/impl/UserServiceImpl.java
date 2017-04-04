/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.mapper.UserMapper;
import com.baomidou.mybatisplus.test.mysql.service.IUserService;
import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * <p>
 * Service层测试
 * </p>
 *
 * @author hubin
 * @date 2017-01-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    // 注入测试
    public void testSqlInjector() {
        Long id = IdWorker.getId();
        int rlt = baseMapper.insert(new User(id, "abc", 18, 1));
        System.err.println("插入ID：" + id + ", 执行结果：" + rlt);
        rlt = baseMapper.deleteLogicById(id);
        System.err.println("测试注入执行结果：" + rlt);
        System.err.println("逻辑修改：" + baseMapper.selectById(id).toString());
    }

}
