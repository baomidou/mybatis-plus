/**
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
package com.baomidou.mybatisplus.toolkit;

import com.alibaba.druid.sql.PagerUtils;

/**
 * <p>
 * DruidUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-30
 */
public class DruidUtils {

    /**
     * 通过Druid方式获取count语句
     *
     * @param originalSql
     * @param dialectType
     * @return
     */
    public static String count(String originalSql, String dialectType) {
        return PagerUtils.count(originalSql, dialectType);
    }

}
