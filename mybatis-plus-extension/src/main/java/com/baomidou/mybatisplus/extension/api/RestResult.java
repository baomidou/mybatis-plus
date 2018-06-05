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

import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.RestException;

/**
 * <p>
 * API REST 返回结果
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
public class RestResult<T> {

    /**
     * 业务错误码
     */
    private String code;
    /**
     * 结果集
     */
    private T data;
    /**
     * 描述
     */
    private String msg;

    public RestResult() {
        // to do nothing
    }

    public RestResult(IErrorCode errorCode) {
        if (errorCode == null) {
            errorCode = ApiErrorCode.FAILED;
        }
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public static <T> RestResult<T> ok(T data) {
        return restResult(data, ApiErrorCode.SUCCESS);
    }

    public static <T> RestResult<T> failed(String msg) {
        return restResult(null, ApiErrorCode.FAILED.getCode(), msg);
    }

    public static <T> RestResult<T> failed(IErrorCode errorCode) {
        return restResult(null, errorCode);
    }

    public static <T> RestResult<T> restResult(T data, IErrorCode errorCode) {
        return restResult(data, errorCode.getCode(), errorCode.getMsg());
    }

    private static <T> RestResult<T> restResult(T data, String code, String msg) {
        RestResult<T> restResult = new RestResult<>();
        restResult.setCode(code);
        restResult.setData(data);
        restResult.setMsg(msg);
        return restResult;
    }

    public boolean isSuccess() {
        return ApiErrorCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 服务间调用非业务正常，异常直接释放
     */
    public T serviceData() {
        if (!isSuccess()) {
            throw new RestException(this.msg);
        }
        return data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
