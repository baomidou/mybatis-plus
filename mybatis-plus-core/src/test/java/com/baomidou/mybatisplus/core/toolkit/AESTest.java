package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(AES.decrypt(result, randomKey)).isEqualTo(data);
    }
}
