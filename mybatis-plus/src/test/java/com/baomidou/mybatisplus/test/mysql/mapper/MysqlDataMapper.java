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
package com.baomidou.mybatisplus.test.mysql.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.mysql.MyBaseMapper;
import com.baomidou.mybatisplus.test.mysql.entity.MysqlData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2018-08-18
 */
public interface MysqlDataMapper extends MyBaseMapper<MysqlData> {

    @Select("select * from mysql_data ${ew.customSqlSegment}")
    List<MysqlData> getAll(@Param(Constants.WRAPPER) Wrapper<?> wrapper);

    @Select("select * from mysql_data")
    Page<Map<String, Object>> getMaps(Page page);

    @Select("select * from mysql_data limit 1")
    Map<String, Object> getRandomOne(String xx, String ww);
}
