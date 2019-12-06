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
package com.baomidou.mybatisplus.test.mysql.mapper.commons;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.mysql.MyBaseMapper;
import com.baomidou.mybatisplus.test.mysql.entity.CommonData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * @author miemie
 * @since 2018/6/7
 */
@SqlParser
public interface CommonDataMapper extends MyBaseMapper<CommonData> {

    @SqlParser(filter = true)
    @Select("select * from common_data")
    List<CommonData> getAllNoTenant();

    @SqlParser
    @Select("select * from common_data ${ew.customSqlSegment}")
    List<CommonData> getByWrapper(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from common_data where id = #{ooxx}")
    Optional<CommonData> getById(Long id);

    @Select("select * from common_data")
    Page<CommonData> myPage(Page<CommonData> page);
}
