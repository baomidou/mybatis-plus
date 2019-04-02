/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.test.base.mapper.commons;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.mapper.MyBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * @author miemie
 * @since 2018/6/7
 */
public interface CommonDataMapper extends MyBaseMapper<CommonData> {

    @SqlParser(filter = true)
    @Select("select * from common_data")
    List<CommonData> getAllNoTenant();

    @Select("select * from common_data ${ew.customSqlSegment}")
    List<CommonData> getByWrapper(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select * from common_data where id = #{ooxx}")
    Optional<CommonData> getById(Long id);

    @Select("select * from common_data")
    IPage<CommonData> myPage(Page<CommonData> page);
}
