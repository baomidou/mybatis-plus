package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 包含功能:
 * 1.自动填充        验证无误
 * 2.逻辑删除        验证无误
 * 3.乐观锁          验证无误
 * 4.不搜索指定字段   验证无误
 *
 * @author meimie
 * @since 2018/6/7
 */
@Data
@Accessors(chain = true)
public class CommonLogicData {

    private Long id;
    private Integer testInt;
    private String testStr;
    @TableField(value = "c_time", fill = FieldFill.INSERT)
    private LocalDateTime createDatetime;
    @TableField(value = "u_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateDatetime;
    @TableLogic
    @TableField(select = false)
    private Integer deleted;

    @Version
    private Integer version;
}
