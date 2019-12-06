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

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

/**
 * 扩展p6spy
 *
 * @author nieqiurong 2019/11/10.
 */
public class MybatisPlusLogFactory implements P6Factory {

    @Override
    public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
        return new P6LogOptions(optionsRepository);
    }

    @Override
    public JdbcEventListener getJdbcEventListener() {
        return MybatisPlusLoggingEventListener.INSTANCE;
    }
}
