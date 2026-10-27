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

/**
 * 数据源属性
 *
 * @author hubin
 * @since 3.5.18
 */
@Getter
@Setter
public class DataSourceProperty {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String jndiName;
    private String schema;
    private String key;
    private DbCluster cluster = DbCluster.master;
    private String testQuery = "SELECT 1";

    public static DataSourceProperty of(String driverClassName, String url, String username, String password) {
        DataSourceProperty property = new DataSourceProperty();
        property.setDriverClassName(driverClassName);
        property.setUrl(url);
        property.setUsername(username);
        property.setPassword(password);
        return property;
    }

}
