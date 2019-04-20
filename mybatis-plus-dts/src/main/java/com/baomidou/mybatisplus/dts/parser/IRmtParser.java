package com.baomidou.mybatisplus.dts.parser;

import org.springframework.amqp.support.converter.MessageConverter;

/**
 * 可靠消息事务解析器
 *
 * @author jobob
 * @since 2019-04-18
 */
public interface IRmtParser {

    /**
     * 消息转换器
     *
     * @return
     */
    MessageConverter getMessageConverter();

    /**
     * JSON 字符串转为对象
     *
     * @param jsonStr   JSON 字符串
     * @param valueType 转换对象类
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T readValue(String jsonStr, Class<T> valueType) throws Exception;

    /**
     * 对象转换为 JSON 字符串
     *
     * @param object 转换对象
     * @return
     * @throws Exception
     */
    String toJSONString(Object object) throws Exception;
}
