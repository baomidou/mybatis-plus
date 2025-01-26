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
package com.baomidou.mybatisplus.generator.model;

import com.baomidou.mybatisplus.generator.config.po.TableField;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nieqiurong
 * @since 3.5.10
 */
public class MapperMethod {

    /**
     * 索引名称
     */
    @Getter
    @NotNull
    private final String indexName;

    /**
     * 方法体
     */
    @Getter
    @NotNull
    private final String method;

    /**
     * 索引字段信息
     */
    @NotNull
    private final List<TableField> tableFieldList = new ArrayList<>();

    /**
     * 扩展参数
     */
    @NotNull
    private final Map<String, Object> extendData = new HashMap<>();

    public MapperMethod(@NotNull String indexName, @NotNull String method, @NotNull List<TableField> tableFieldList) {
        this.indexName = indexName;
        this.method = method;
        this.tableFieldList.addAll(tableFieldList);
    }

    public void addExtendData(@NotNull String key, @NotNull Object data) {
        this.extendData.put(key, data);
    }

    public @Unmodifiable @NotNull Map<String, Object> getExtendData() {
        return Collections.unmodifiableMap(this.extendData);
    }

    public @Unmodifiable @NotNull List<TableField> getTableFieldList() {
        return Collections.unmodifiableList(this.tableFieldList);
    }

}
