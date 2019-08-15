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
package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.test.base.type.JsonTypeHandler;
import com.baomidou.mybatisplus.test.base.type.ListTypeHandler;
import com.baomidou.mybatisplus.test.base.type.MapTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author miemie
 * @since 2019-01-19
 */
@Data
@Accessors(chain = true)
//@TableName(resultMap = "resultChildren1")
@TableName(autoResultMap = true)
public class ResultMapEntity {

    private Long id;

    private String column1;
    private String column2;
    private String column3;
    private String column4;
    @TableField(typeHandler = ListTypeHandler.class)
    private List<String> list;
    @TableField(typeHandler = MapTypeHandler.class)
    private Map<String, Object> map;
    @TableField(typeHandler = JsonTypeHandler.class)
    private Map<String, Object> mapp;
}
