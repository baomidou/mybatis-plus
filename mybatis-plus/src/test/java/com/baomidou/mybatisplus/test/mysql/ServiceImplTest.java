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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.service.IUserService;

/**
 * <p>
 * Service层测试
 * </p>
 *
 * @author hubin
 * @date 2017-01-30
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "classpath:spring/spring-servlet.xml" })
public class ServiceImplTest {

    @Autowired
    private IUserService userService;

    //@Test
    public void testInsertBatch() throws IOException {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userList.add(new User("u-" + i, i, i));
        }
        boolean batchResult = userService.insertBatch(userList);
        System.err.println("batchResult: " + batchResult);

        // 注入测试
        userService.testSqlInjector();
    }

}
