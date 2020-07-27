/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 测试 AES 算法
 *
 * @author hubin
 * @since 2020-05-23
 */
public class AESTest {

    @Test
    public void encryptDecrypt() {
        String data = "1@2#3qwe111";
        String randomKey = AES.generateRandomKey();
        System.out.println("AES 密钥：" + randomKey);
        String result = AES.encrypt(data, randomKey);
        System.out.println("AES 加密内容：" + result);
        Assertions.assertEquals(data, AES.decrypt(result, randomKey));
    }
}
