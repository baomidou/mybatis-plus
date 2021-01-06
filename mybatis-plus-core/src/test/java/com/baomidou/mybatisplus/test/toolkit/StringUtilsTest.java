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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author HCL
 * Create at 2018/9/17
 */

class StringUtilsTest {

    @Test
    void camelToUnderlineTest() {
        String s = "userId";
        Assertions.assertEquals("user_id", StringUtils.camelToUnderline(s));
        Assertions.assertEquals("a", StringUtils.camelToUnderline("A"));
    }

    @Test
    void isCapitalModeTest() {
        Assertions.assertFalse(StringUtils.isCapitalMode("test"));
        Assertions.assertFalse(StringUtils.isCapitalMode("Test"));
        Assertions.assertFalse(StringUtils.isCapitalMode("teSt"));
        assertTrue(StringUtils.isCapitalMode("TEST"));
    }

    /**
     * 测试当前字符串能否是合法的列名
     */
    @Test
    void canBeAColumnName() {
//        assertTrue(StringUtils.canBeColumnName("a$"));
    }

    /**
     * 取列名
     */
    @Test
    void getTargetColumn() {
        assertThat(StringUtils.getTargetColumn("order")).isEqualTo("order");
        assertThat(StringUtils.getTargetColumn("`order`")).isEqualTo("order");
        assertThat(StringUtils.getTargetColumn("'order'")).isEqualTo("order");
    }

    /**
     * 测试equals方法
     */
    @Test
    void equal(){
        assertTrue(StringUtils.equals(null, null));
        Assertions.assertFalse(StringUtils.equals(null, "abc"));
        Assertions.assertFalse(StringUtils.equals("abc", null));
        assertTrue(StringUtils.equals("abc", "abc"));
        Assertions.assertFalse(StringUtils.equals("abc", "ABC"));
    }
}
