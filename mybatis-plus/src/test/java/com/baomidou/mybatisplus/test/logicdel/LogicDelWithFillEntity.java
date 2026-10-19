package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity used to test dynamic logic-delete values supplied via {@link TableField#fill()}.
 * <p>
 * When the application calls {@code deleteById(entity)}, the fill handler sets <br>
 * {@code deletedAt} to the application-computed timestamp (supporting i18n / timezone <br>
 * customisation). When only the primary key is supplied (e.g., {@code deleteById(id)}), <br>
 * the static {@code logicDeleteValue} configured on the global DbConfig is used instead.
 */
@Data
@TableName("logic_del_fill_entity")
public class LogicDelWithFillEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    /**
     * Logic-delete timestamp.
     * <ul>
     * <li>NULL → record is <em>not</em> deleted ({@code logicNotDeleteValue = "null"})</li>
     * <li>non-NULL → record is logically deleted</li>
     * </ul>
     * <p>
     * {@link FieldFill#UPDATE} causes the fill handler to provide the actual timestamp <br>
     * (e.g., {@code LocalDateTime.now(ZoneId.of("Asia/Shanghai"))}) instead of relying <br>
     * on the database-side {@code NOW()} function, enabling proper i18n support. <br>
     * When no entity is available (e.g., {@code deleteById(id)}), the global <br>
     * {@code logicDeleteValue} (default: {@code "NOW()"}) is used as a fallback.
     */
    @TableField(fill = FieldFill.UPDATE)
    @TableLogic(value = "null")
    private LocalDateTime deletedAt;
}
