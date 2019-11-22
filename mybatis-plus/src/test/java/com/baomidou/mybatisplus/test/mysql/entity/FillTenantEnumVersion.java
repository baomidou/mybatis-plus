package com.baomidou.mybatisplus.test.mysql.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.test.mysql.enums.TestEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miemie
 * @since 2019-11-22
 */
@Data
@Accessors(chain = true)
@TableName("ftev")
public class FillTenantEnumVersion {

    private Long id;
    private Integer tInt;
    @TableField //无意义
    private String tStr;
    @TableField(value = "c_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "u_time", fill = FieldFill.UPDATE)
    private LocalDateTime updTime;
    private TestEnum testEnum;

    @Version
    private Integer version;
//    /**
//     * 多租户
//     * 不用配置实体字段,但是数据库需要该字段
//     */
//    private Long tenantId;
}
