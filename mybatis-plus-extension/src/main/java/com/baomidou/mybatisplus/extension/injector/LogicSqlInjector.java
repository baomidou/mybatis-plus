/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.injector;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDelete;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteBatchByIds;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteById;
import com.baomidou.mybatisplus.extension.injector.methods.LogicDeleteByMap;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectBatchByIds;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectById;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectByMap;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectCount;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectList;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectMaps;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectMapsPage;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectObjs;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectOne;
import com.baomidou.mybatisplus.extension.injector.methods.LogicSelectPage;
import com.baomidou.mybatisplus.extension.injector.methods.LogicUpdate;
import com.baomidou.mybatisplus.extension.injector.methods.LogicUpdateById;


/**
 * <p>
 * SQL 逻辑删除注入器
 * </p>
 *
 * @author hubin
 * @since 2018-06-12
 */
public class LogicSqlInjector extends AbstractSqlInjector {


    @Override
    public List<AbstractMethod> getMethodList() {
        return Stream.of(
            new Insert(),
            new LogicDelete(),
            new LogicDeleteByMap(),
            new LogicDeleteById(),
            new LogicDeleteBatchByIds(),
            new LogicUpdate(),
            new LogicUpdateById(),
            new LogicSelectById(),
            new LogicSelectBatchByIds(),
            new LogicSelectByMap(),
            new LogicSelectOne(),
            new LogicSelectCount(),
            new LogicSelectMaps(),
            new LogicSelectMapsPage(),
            new LogicSelectObjs(),
            new LogicSelectList(),
            new LogicSelectPage()
        ).collect(Collectors.toList());
    }

}
