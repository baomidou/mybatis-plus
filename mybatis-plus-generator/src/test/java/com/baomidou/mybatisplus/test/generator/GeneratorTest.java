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
