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
package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author HCL
 * Create at 2018/9/17
 */

class StringUtilsTest {

    @Test
    void camelToUnderlineTest() {
        String s = "userId";
        Assertions.assertEquals("user_id", StringUtils.camelToUnderline(s));
    }

    @Test
    void isCapitalModeTest(){
        Assertions.assertFalse(StringUtils.isCapitalMode("test"));
        Assertions.assertFalse(StringUtils.isCapitalMode("Test"));
        Assertions.assertFalse(StringUtils.isCapitalMode("teSt"));
        Assertions.assertTrue(StringUtils.isCapitalMode("TEST"));
    }

    @Test
    void testGuessGetterName(){
        Assertions.assertEquals("getSex",StringUtils.guessGetterName("sex",String.class));
        Assertions.assertEquals("getIsSex",StringUtils.guessGetterName("isSex",String.class));
        Assertions.assertEquals("getTestWrap",StringUtils.guessGetterName("testWrap",Boolean.class));
        Assertions.assertEquals("getIsTestWrap",StringUtils.guessGetterName("isTestWrap",Boolean.class));
        Assertions.assertEquals("isTestSimple",StringUtils.guessGetterName("testSimple",boolean.class));
        Assertions.assertEquals("isTestSimple",StringUtils.guessGetterName("isTestSimple",boolean.class));
    }
}
