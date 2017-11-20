package com.baomidou.mybatisplus.test.plugins;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>随机工具类</h1>
 * <p>
 * <pre>
 * 对{@link Random}和{@link UUID}常用方式的包装
 * </pre>
 *
 * @author TaoYu
 */
public final class RandomUtils {

    private static final Logger log = LoggerFactory.getLogger(RandomUtils.class);

    private static final Random RANDOM = new Random();
    private static final String STRING_AND_NUMBER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER = "0123456789";

    private RandomUtils() {
    }

    /**
     * 随机生成中文字符
     *
     * @param length 字符长度
     * @return 随机中文字符串
     */
    public static String randomChinese(final int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            try {
                Integer highPos = 176 + Math.abs(nextInt(39));// 获取高位值
                Integer lowPos = 161 + Math.abs(nextInt(93));// 获取低位值
                byte[] b = new byte[2];
                b[0] = highPos.byteValue();
                b[1] = lowPos.byteValue();
                sb.append(new String(b, "GBK"));
            } catch (UnsupportedEncodingException e) {
                log.error("转换字符为GBK失败", e);
            }
        }
        return sb.toString();
    }

    /**
     * 随机生成原生36位UUID字符串
     *
     * @return UUID字符串
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 随机生成原生32位UUID字符串，去除"-"
     *
     * @return 32位UUID字符串，去除"-"
     */
    public static String random32UUID() {
        return randomUUID().replace("-", "");
    }

    /**
     * 随机生成指定长度的UUID字符串
     *
     * @param length 指定长度。最大36，超出抛异常IllegalArgumentException
     * @return UUID字符串
     */
    public static String randomUUID(final int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    /**
     * 随机生成指定长度的全字母字符串
     *
     * @param length 指定长度。 正整数，否则超出抛异常IllegalArgumentException
     * @return 全字母字符串
     */
    public static String randomStr(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = nextInt(STRING.length());
            sb.append(STRING.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机生成指定长度的全数字字符串
     *
     * @param length 指定长度。 正整数，否则超出抛异常IllegalArgumentException
     * @return 全数字字符串
     */
    public static String randomNum(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = nextInt(NUMBER.length());
            sb.append(NUMBER.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机生成指定长度的字母数字混合字符串
     *
     * @param length 指定字符串长度。正整数，否则超出抛异常IllegalArgumentException
     * @return 字母数字混合字符串
     */
    public static String randomStrAndNum(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(STRING_AND_NUMBER.length());
            sb.append(STRING_AND_NUMBER.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 随机生成[0,Integer.MAX_VALUE)之间的数字
     *
     * @return 随机数
     */
    public static int nextInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }

    /**
     * 随机生成[0,endExclusive)之间的数字
     *
     * @param endExclusive 终止数，不包含
     * @return 随机数
     */
    public static int nextInt(final int endExclusive) {
        return RANDOM.nextInt(endExclusive);
    }

    /**
     * 随机生成[startInclusive,endExclusive)之间的数字
     *
     * @param startInclusive 起始数，包含
     * @param endExclusive   终止数，不包含
     * @return 随机数
     */
    public static int nextInt(final int startInclusive, final int endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }

    /**
     * 随机生成真假值
     *
     * @return 随机真假值
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    /**
     * 随机生成[0,Long.MAX_VALUE)之间的long型数字
     *
     * @return long型随机数
     */
    public static long nextLong() {
        return nextLong(0, Long.MAX_VALUE);
    }

    /**
     * 随机生成[startInclusive,endExclusive)之间的long型数字
     *
     * @param startInclusive 起始数，包含
     * @param endExclusive   终止数，不包含
     * @return long型随机数
     */
    public static long nextLong(final long startInclusive, final long endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return (long) nextDouble(startInclusive, endExclusive);
    }

    /**
     * 随机生成[0,Double.MAX_VALUE)之间的long型数字
     *
     * @return double型随机数
     */
    public static double nextDouble() {
        return nextDouble(0, Double.MAX_VALUE);
    }

    /**
     * 随机生成[startInclusive,endExclusive)之间的double型数字
     *
     * @param startInclusive 起始数，包含
     * @param endExclusive   终止数，不包含
     * @return double型随机数
     */
    public static double nextDouble(final double startInclusive, final double endExclusive) {
        // FIXME 精度丢失问题，不能直接比较，不过一般也不会遇到，实际应该判断两者差值在某极小数值
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ((endExclusive - startInclusive) * RANDOM.nextDouble());
    }
}
