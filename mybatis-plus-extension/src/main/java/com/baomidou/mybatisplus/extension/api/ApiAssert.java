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

import java.util.Collection;
import java.util.Map;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;

/**
 * <p>
 * REST API 业务断言<br>
 * 参考：org.junit.Assert
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
public class ApiAssert {

    protected ApiAssert() {
        // to do noting
    }

    /**
     * 大于O
     */
    public static void gtZero(Integer num, IErrorCode errorCode) {
        if (num == null || num <= 0) {
            ApiAssert.fail(errorCode);
        }
    }

    /**
     * 大于等于O
     */
    public static void geZero(Integer num, IErrorCode errorCode) {
        if (num == null || num < 0) {
            ApiAssert.fail(errorCode);
        }
    }

    /**
     * num1大于num2
     */
    public static void gt(Integer num1, Integer num2, IErrorCode errorCode) {
        if (num1 <= num2) {
            ApiAssert.fail(errorCode);
        }
    }

    /**
     * num1大于等于num2
     */
    public static void ge(Integer num1, Integer num2, IErrorCode errorCode) {
        if (num1 < num2) {
            ApiAssert.fail(errorCode);
        }
    }

    /**
     * obj1 eq obj2
     */
    public static void eq(Object obj1, Object obj2, IErrorCode errorCode) {
        if (!obj1.equals(obj2)) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void isTrue(boolean condition, IErrorCode errorCode) {
        if (!condition) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void isFalse(boolean condition, IErrorCode errorCode) {
        if (condition) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void isNull(IErrorCode errorCode, Object... conditions) {
        if (ObjectUtils.isNotNull(conditions)) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void notNull(IErrorCode errorCode, Object... conditions) {
        if (ObjectUtils.isNull(conditions)) {
            ApiAssert.fail(errorCode);
        }
    }

    /**
     * <p>
     * 失败结果
     * </p>
     *
     * @param errorCode 异常错误码
     */
    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }

    public static void fail(boolean condition, IErrorCode errorCode) {
        if (condition) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(boolean condition, String message) {
        if (condition) {
            ApiAssert.fail(message);
        }
    }

    public static void notEmpty(Object[] array, IErrorCode errorCode) {
        if (ObjectUtils.isEmpty(array)) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void noNullElements(Object[] array, IErrorCode errorCode) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    ApiAssert.fail(errorCode);
                }
            }
        }
    }

    public static void notEmpty(Collection<?> collection, IErrorCode errorCode) {
        if (CollectionUtils.isNotEmpty(collection)) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void notEmpty(Map<?, ?> map, IErrorCode errorCode) {
        if (ObjectUtils.isEmpty(map)) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void isInstanceOf(Class<?> type, Object obj, IErrorCode errorCode) {
        ApiAssert.notNull(errorCode, type);
        if (!type.isInstance(obj)) {
            ApiAssert.fail(errorCode);
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, IErrorCode errorCode) {
        ApiAssert.notNull(errorCode, superType);
        if (subType == null || !superType.isAssignableFrom(subType)) {
            ApiAssert.fail(errorCode);
        }
    }

}
