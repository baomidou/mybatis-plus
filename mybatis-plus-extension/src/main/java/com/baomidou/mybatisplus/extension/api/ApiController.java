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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * REST API 通用控制器
 * </p>
 *
 * @author hubin
 * @since 2018-06-08
 */
public class ApiController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * <p>
     * 请求成功
     * </p>
     *
     * @param data 数据内容
     * @param <T>  对象泛型
     * @return
     */
    protected <T> ApiResult<T> success(T data) {
        return ApiResult.ok(data);
    }

    /**
     * <p>
     * 请求失败
     * </p>
     *
     * @param msg 提示内容
     * @return
     */
    protected ApiResult<Object> failed(String msg) {
        return ApiResult.failed(msg);
    }

    /**
     * <p>
     * 请求失败
     * </p>
     *
     * @param errorCode 请求错误码
     * @return
     */
    protected ApiResult<Object> failed(IErrorCode errorCode) {
        return ApiResult.failed(errorCode);
    }

}
