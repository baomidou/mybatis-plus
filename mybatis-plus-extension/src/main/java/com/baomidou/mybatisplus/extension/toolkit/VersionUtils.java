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
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * VersionUtils，比较版本号
 *
 * @author Caratacus
 * @since 2016-12-5
 */
public class VersionUtils {

    /**
     * 比较版本号的大小，前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1 ignore
     * @param version2 ignore
     * @return ignore
     */
    public static int compareVersion(String version1, String version2) {
        Assert.isTrue(!StringUtils.isBlank(version1) && !StringUtils.isBlank(version2),
            "Error: CompareVersion Error: Illegal Argument !");
        // 切割点 "."；
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        // 取最小长度值
        int minLength = Math.min(versionArray1.length, versionArray2.length);
        int diff = 0;
        // 先比较长度 再比较字符
        while (idx < minLength && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
            && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    /**
     * 比较版本号 true
     *
     * @param version1 ignore
     * @param version2 ignore
     * @return boolean true v1 &gt;= v2 false 相反
     */
    public static boolean compare(String version1, String version2) {
        int num = compareVersion(version1, version2);
        return num >= 0;
    }
}
