package com.baomidou.mybatisplus.core.toolkit;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试 AES 算法
 *
 * @author hubin
 * @since 2020-05-23
 */
@Tag("unit")
public class AESTest {

    @Test
    public void encryptDecrypt() {
        String data = "1@2#3qwe111";
        String randomKey = AES.generateRandomKey();
        String result = AES.encrypt(data, randomKey);
        assertThat(AES.decrypt(result, randomKey)).isEqualTo(data);
    }
}
