/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.plugins.datasource;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 分库配置属性
 *
 * @author hubin
 * @since 3.5.18
 */
@Getter
public class ShardingProperties {
    @Setter
    private String primary;
    @Setter
    private IShardingStrategy shardingStrategy;
    private Map<String, List<DataSourceProperty>> datasource = new LinkedHashMap<>();

    public boolean enable() {
        return primary != null && !primary.trim().isEmpty();
    }

    public void setDatasource(Map<String, List<DataSourceProperty>> datasource) {
        this.datasource = datasource == null ? new LinkedHashMap<>() : datasource;
    }
}
