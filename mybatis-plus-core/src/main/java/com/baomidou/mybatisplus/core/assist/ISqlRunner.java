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
package com.baomidou.mybatisplus.core.assist;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @since 2018/2/7
 */
public interface ISqlRunner {

    String INSERT = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Insert";
    String DELETE = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Delete";
    String UPDATE = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Update";
    String SELECT_LIST = "com.baomidou.mybatisplus.core.mapper.SqlRunner.SelectList";
    String SELECT_OBJS = "com.baomidou.mybatisplus.core.mapper.SqlRunner.SelectObjs";
    String COUNT = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Count";
    String SQL_SCRIPT = "${sql}";
    String SQL = "sql";
    String PAGE = "page";

    boolean insert(String sql, Object... args);

    boolean delete(String sql, Object... args);

    boolean update(String sql, Object... args);

    List<Map<String, Object>> selectList(String sql, Object... args);

    List<Object> selectObjs(String sql, Object... args);

    Object selectObj(String sql, Object... args);

    int selectCount(String sql, Object... args);

    Map<String, Object> selectOne(String sql, Object... args);

    <E extends IPage<Map<String, Object>>> E selectPage(E page, String sql, Object... args);
}
