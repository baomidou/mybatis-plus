package com.baomidou.mybatisplus.code;

import lombok.Builder;
import lombok.Data;

/**
 * @author miemie
 * @since 2025/9/1
 */
@Data
@Builder
public class Overwrite {
    private String source;
    @Builder.Default
    private Operate operate = Operate.APPEND;
    private String target;

    public enum Operate {
        APPEND, // 插入后面
        DELETE, // 删除
        COVERAGE, // 覆盖
    }
}
