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
package com.baomidou.mybatisplus.test.generator;

import java.util.Scanner;


/**
 * Generator test
 *
 * @author hubin
 * @since 2018-01-11
 */
public class GeneratorTest {

    /**
     * 读取控制台内容
     */
    public static int scanner() {
        Scanner scanner = new Scanner(System.in);
        String help =
            " ！！代码生成, 输入 0 表示使用 Velocity 引擎 ！！" +
                "\n对照表：" +
                "\n0 = Velocity 引擎" +
                "\n1 = Freemarker 引擎" +
                "\n请输入：";
        System.out.println(help);
        int slt = 0;
        // 现在有输入数据
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if ("1".equals(ipt)) {
                slt = 1;
            }
        }
        return slt;
    }
}
