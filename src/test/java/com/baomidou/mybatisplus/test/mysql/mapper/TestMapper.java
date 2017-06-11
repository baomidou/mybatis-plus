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

import org.apache.ibatis.annotations.Insert;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.mysql.entity.Test;

/**
 * <p>
 * 继承 BaseMapper，就自动拥有CRUD方法
 * </p>
 *
 * @author Caratacus hubin
 * @Date 2016-09-25
 */
public interface TestMapper extends BaseMapper<Test> {

    /**
     * 注解插入【测试】
     */
    @Insert("insert into test(id,type) values(#{id},#{type})")
    int insertInjector(Test test);

}
