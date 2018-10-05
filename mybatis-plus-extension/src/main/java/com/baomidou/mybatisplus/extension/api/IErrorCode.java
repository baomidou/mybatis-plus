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
package com.baomidou.mybatisplus.extension.api;

/**
 * <p>
 * REST API 错误码接口
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
public interface IErrorCode {

    /**
     * 错误编码 -1、失败 0、成功
     */
    long getCode();

    /**
     * 错误描述
     */
    String getMsg();
}
