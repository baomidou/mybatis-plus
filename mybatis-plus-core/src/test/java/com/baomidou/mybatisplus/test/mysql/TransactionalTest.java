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

import com.baomidou.mybatisplus.test.CrudTest;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.mapper.UserMapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * <p>
 * MySQL 数据库，表引擎  MyISAM  不支持事务，请使用  InnoDB  ！！！！
 * </p>
 *
 * @author hubin
 * @date 2016-09-20
 */
public class TransactionalTest extends CrudTest {

    /**
     * <p>
     * 事务测试
     * </p>
     */
    @Test
    public void test() {
        SqlSession sqlSession = this.sqlSessionFactory().openSession();

        /**
         * 插入
         */
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int rlt = userMapper.insert(new User(IdWorker.getId(), "1", 1, 1));
        System.err.println("--------- insertInjector --------- " + rlt);

        //session.commit();
        sqlSession.rollback();
        sqlSession.close();
    }

}
