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

import com.baomidou.mybatisplus.test.mysql.entity.CommonData;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * @author miemie
 * @since 2019-04-11
 */
public interface CommonDataCopyMapper {

    @Select("select * from common_data where id = #{id}")
    Optional<CommonData> selectById(Long id);
}
