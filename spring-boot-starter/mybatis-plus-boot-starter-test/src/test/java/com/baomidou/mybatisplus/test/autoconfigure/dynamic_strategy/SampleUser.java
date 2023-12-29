package com.baomidou.mybatisplus.test.autoconfigure.dynamic_strategy;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2023/12/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SampleUser {

    private Integer id;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private String username;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String password;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

}
