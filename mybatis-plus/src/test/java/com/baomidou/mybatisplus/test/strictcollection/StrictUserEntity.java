package com.baomidou.mybatisplus.test.strictcollection;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nieqiurong
 * @since 3.5.18
 */
@Data
@TableName("t_strict_user")
public class StrictUserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long groupId;

    private String userName;

    private String email;

    private Integer age;
}
