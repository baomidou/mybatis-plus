package com.baomidou.mybatisplus.test.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.test.base.enums.TestEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 包含功能:
 * 1.自动填充        验证无误
 * 2.乐观锁          验证无误
 * 3.多租户          验证无误
 * 4.枚举            验证无误
 *
 * @author meimie
 * @since 2018/6/7
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CommonData extends Model<CommonData> {

    private Long id;
    @TableField(el = "testInt, jdbcType=INTEGER")
    private Integer testInt;
    @TableField(strategy = FieldStrategy.DEFAULT)
    private String testStr;
    @TableField(value = "c_time", fill = FieldFill.INSERT)
    private LocalDateTime createDatetime;
    @TableField(value = "u_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateDatetime;
    private TestEnum testEnum;

    @Version
    private Integer version;
    /**
     * 多租户
     * 不用配置实体字段,但是数据库需要该字段
     */
//    private Long tenantId;

}
