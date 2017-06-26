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

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.test.CrudTest;
import com.baomidou.mybatisplus.test.mysql.entity.PhoneNumber;
import com.baomidou.mybatisplus.test.mysql.entity.Role;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.mapper.RoleMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.UserMapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * <p>
 * 对User, Role及el, typeHandler, resultMap进行测试
 * </p>
 *
 * @author junyu
 * @Date 2016-09-09
 */
public class URPTest extends CrudTest {

    @Test
    public void urpTest() {
        // 加载配置文件
        SqlSession session = this.sqlSessionFactory().openSession();

        UserMapper userMapper = session.getMapper(UserMapper.class);
        RoleMapper roleMapper = session.getMapper(RoleMapper.class);

        /**
         * sjy 测试@TableField的el属性, 级联resultMap
         */
        Role role = new Role();
        role.setId(IdWorker.getId());
        role.setName("admin");
        int rlt = roleMapper.insert(role);
        System.err.println("--------- insert role --------- " + rlt);

        PhoneNumber phone = new PhoneNumber("81", "0576", "82453832");

        User userA = new User();
        userA.setId(IdWorker.getId());
        userA.setName("junyu_shi");
        userA.setAge(15);
        userA.setTestType(1);
        userA.setRole(role);
        userA.setPhone(phone);
        rlt = userMapper.insert(userA);
        System.err.println("--------- insert user --------- " + rlt);

        User whereUser = userMapper.selectOne(userA);
        System.err.println("--------- select user --------- " + whereUser.toString());

        // 如果不使用el表达式, User类中就同时需要roleId用于对应User表中的字段,
        // 和Role属性用于保存resultmap的级联查询. 在插入时, 就需要写user.setRoleId(), 然后updateUser.
        role = new Role();
        role.setId(IdWorker.getId());
        role.setName("root");
        roleMapper.insert(role);
        userA.setRole(role);
        userMapper.updateById(userA);
        System.err.println("--------- upadte user's role --------- " + rlt);

        whereUser = userMapper.selectOne(userA);
        System.err.println("--------- select user --------- " + whereUser.toString());

        userMapper.delete(new EntityWrapper<>(userA));
        System.err.println("--------- delete user --------- " + rlt);

    }

}
