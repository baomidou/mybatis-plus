package com.baomidou.mybatisplus.test.h2.entity.persistent;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 测试逻辑删除实体
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/15
 */
@Data
@Accessors(chain = true)
@TableName("h2user")
public class H2UserLogicDelete {

    /* 表字段注解，false 表中不存在的字段，可无该注解 默认 true */
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /* 主键ID 注解，value 字段名，type 用户输入ID */
    @TableId(value = "test_id", type = IdType.AUTO)
    private Long id;

    /* 测试忽略验证 */
    private String name;

    private Integer age;

    /*BigDecimal 测试*/
    private BigDecimal price;

    /* 测试下划线字段命名类型, 字段填充 */
    @TableField(value = "test_type", strategy = FieldStrategy.IGNORED)
    private Integer testType;

    private String desc;

    //    @Version
    @TableLogic
    private Integer version;

    @TableField(value = "last_updated_dt", fill = FieldFill.UPDATE)
    private Timestamp lastUpdatedDt;

}
