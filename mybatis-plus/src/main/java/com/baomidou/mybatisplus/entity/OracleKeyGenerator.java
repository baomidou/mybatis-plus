/**
 * Copyright (c) 2011-2016, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.entity;

import com.baomidou.mybatisplus.mapper.IKeyGenerator;

/**
 * <p>
 * Oracle Key Sequence 生成器
 * </p>
 *
 * @author hubin
 * @Date 2017-05-08
 */
public class OracleKeyGenerator implements IKeyGenerator {

    @Override
    public String executeSql(TableInfo tableInfo) {
        return String.format("SELECT %s.NEXTVAL FROM DUAL", tableInfo.getKeySequence().value());
    }

}
