/*
 * Copyright (c) 2011-2021, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.plugins.handler.sharding;

import java.util.List;
import java.util.Map;

/**
 * @author zengzhihong
 * @since 2021-01-14
 */
public interface ShardingProcessor {

    /**
     * 分表执行
     *
     * @param strategy       策略
     * @param shardingValues 分片字段和字段值
     * @return 真实表名
     */
    String doSharding(ShardingStrategy strategy, Map<String, List<Object>> shardingValues);
}
