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
package com.baomidou.mybatisplus.core.toolkit;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 加密工具类
 *
 * @author hubin
 * @since 2018-08-02
 */
public class EncryptUtils {

    /**
     * MD5 Base64 加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     */
    public static String md5Base64(String str) {
        //确定计算方法
        try {
            MessageDigest md5 = MessageDigest.getInstance(Constants.MD5);
            //加密后的字符串
            byte[] src = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(src);
        } catch (Exception e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}
