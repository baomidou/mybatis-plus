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
package com.baomidou.mybatisplus.test.mysql;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.CrudTest;
import com.baomidou.mybatisplus.test.mysql.entity.Test;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.mapper.TestMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.UserMapper;

/**
 * <p>
 * 循环标签 查询分页失效 测试类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-21
 */
public class CircularLabelsTest extends CrudTest {

    /**
     * 循环标签 测试
     */
    @org.junit.Test
    public void test() {
        SqlSession session = this.sqlSessionFactory().openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        Page<User> page = new Page<>(1, 6);
        List<User> users = userMapper.forSelect(page, Arrays.asList("1", "2", "3"));
        System.out.println(users.toString());
        System.out.println(page);
        User user = new User();
        user.setId(1L);
        User users1 = userMapper.selectOne(user);
        System.out.println(users1);
        TestMapper mapper = session.getMapper(TestMapper.class);
        Test test = new Test();
        test.setCreateTime(new Date());
        test.setType("11111");
        mapper.insert(test);
        session.rollback();
    }
}
