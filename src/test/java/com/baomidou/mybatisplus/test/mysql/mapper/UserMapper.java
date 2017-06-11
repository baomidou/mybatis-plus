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
package com.baomidou.mybatisplus.test.mysql.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.test.mysql.MyBaseMapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;

/**
 * <p>
 * 继承 BaseMapper，就自动拥有CRUD方法
 * </p>
 *
 * @author hubin
 * @Date 2016-01-23
 */
public interface UserMapper extends MyBaseMapper<User> {

    /**
     * 用户列表，分页显示
     *
     * @param pagination
     *            传递参数包含该属性，即自动分页
     * @return
     */
    List<User> selectListRow(RowBounds pagination);

    /**
     * 注解插入【测试】
     */
    @Insert("insert into user(test_id,name,age) values(#{id},#{name},#{age})")
    int insertInjector(User user);

    /**
     * 自定义注入方法
     */
    int deleteAll();

    /**
     * 自定义注入逻辑删除方法<br>
     * com.baomidou.mybatisplus.test.mysql.MetaObjectHandlerTest
     */
    int deleteLogicById(@Param("id") Long id);

    /**
     * 根据主键批量查询
     *
     * @param pagination
     * @param ids
     * @return
     */
    List<User> forSelect(RowBounds pagination, @Param("ids") List<String> ids);

}
