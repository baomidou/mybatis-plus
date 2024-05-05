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
