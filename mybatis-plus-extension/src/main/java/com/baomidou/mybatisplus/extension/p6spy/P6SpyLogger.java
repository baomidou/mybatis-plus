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
package com.baomidou.mybatisplus.extension.p6spy;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * <p>
 * P6spy SQL 打印策略
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
public class P6SpyLogger implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        return StringUtils.isNotBlank(sql) ? " Consume Time：" + elapsed + " ms " + now +
            "\n Execute SQL：" + sql.replaceAll("[\\s]+", " ") + "\n" : "";
    }
}
