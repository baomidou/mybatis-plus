package com.baomidou.mybatisplus.test.record;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author nieqiurong 2023年9月14日
 */
@TableName(value = "t_record")
public record RecordEntity(Long id,
                           String name, @TableField(value = "tel") String phone) {


}



