/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.mapper.AutoMapper;
import com.baomidou.mybatisplus.test.mysql.entity.User;

/**
 * <p>
 * 继承 AutoMapper，就自动拥有CRUD方法
 * </p>
 * 
 * @author Caratacus
 * @Date 2016-09-25
 */
public interface TestMapper extends AutoMapper<User> {

}
