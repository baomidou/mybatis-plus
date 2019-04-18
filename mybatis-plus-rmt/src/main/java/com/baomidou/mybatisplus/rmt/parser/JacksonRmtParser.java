package com.baomidou.mybatisplus.rmt.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * Jackson 可靠消息解析器
 *
 * @author jobob
 * @since 2019-04-18
 */
public class JacksonRmtParser implements IRmtParser {

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    @Override
    public MessageConverter getMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Override
    public <T> T readValue(String jsonStr, Class<T> valueType) throws Exception {
        if (null == jsonStr || "".equals(jsonStr)) {
            return null;
        }
        return getObjectMapper().readValue(jsonStr, valueType);
    }

    @Override
    public String toJSONString(Object object) throws Exception {
        return getObjectMapper().writeValueAsString(object);
    }
}
