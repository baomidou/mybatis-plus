package com.baomidou.mybatisplus.test.scheam;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nieqiurong
 */
@Data
@TableName(value = "${my.tableName}", schema = "${my.schema}")
public class SchemaEntity {

    private Long id;

    private String name;

}
