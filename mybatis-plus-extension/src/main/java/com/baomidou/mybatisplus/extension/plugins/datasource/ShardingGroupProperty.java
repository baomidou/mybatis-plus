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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 动态数据库组信息
 *
 * @author hubin
 * @since 3.5.18
 */
@Slf4j
@Setter
@Getter
public class ShardingGroupProperty {
    private List<String> masterKeys;
    private List<String> slaveKeys;

    public List<String> getDatasourceKeys(SqlCommandType commandType, String group) {
        // 从读主写
        if (commandType == SqlCommandType.SELECT && slaveKeys != null && !slaveKeys.isEmpty()) {
            return slaveKeys;
        }
        // 主库不存在读从库
        if (null == masterKeys) {
            log.error("database group: [ {} ] master not available", group);
            return slaveKeys;
        }
        return masterKeys;
    }

    public boolean isHealth() {
        return masterKeys != null && !masterKeys.isEmpty();
    }

    public List<String> allDbKeys() {
        List<String> keys = new ArrayList<>();
        if (masterKeys != null) {
            keys.addAll(masterKeys);
        }
        if (slaveKeys != null) {
            keys.addAll(slaveKeys);
        }
        return Collections.unmodifiableList(keys);
    }

}
