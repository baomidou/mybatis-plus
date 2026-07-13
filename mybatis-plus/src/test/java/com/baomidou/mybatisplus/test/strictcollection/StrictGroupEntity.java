package com.baomidou.mybatisplus.test.strictcollection;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @author nieqiurong
 * @since 3.5.18
 */
@Data
@TableName("t_strict_group")
public class StrictGroupEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String groupName;

    private String description;

    /**
     * Collection field whose generic element type is {@link StrictUserEntity}.
     * When {@code strictResultMapCollectionTypeCheck} is enabled, the
     * {@code ofType} attribute in the XML mapping must be compatible with this type.
     */
    @TableField(exist = false)
    private List<StrictUserEntity> users;
}
