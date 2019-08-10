package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableSuperClass;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author by keray
 * date:2019/7/25 15:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableSuperClass
public class BaseEntity extends Model<BaseEntity> {
    @TableId(type = IdType.INPUT)
    private String id;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    @TableLogic(delval = "1", value = "0")
    private Boolean deleted = false;

    private LocalDateTime deleteTime;

    private String createBy;

    private String updateBy;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
