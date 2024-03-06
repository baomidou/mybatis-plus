package com.baomidou.mybatisplus.test.json;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author nieqiurong 2024年3月4日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_fastjson_entity", autoResultMap = true)
public class FastJsonEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Card card;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<Attr> attr;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Attr> attr2;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, String> attr3;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> attr4;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attr {

        private String name;

        private String value;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {

        private String id;

        private String value;

    }


}
