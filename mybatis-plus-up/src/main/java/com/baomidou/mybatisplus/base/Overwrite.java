package com.baomidou.mybatisplus.base;

import lombok.Builder;
import lombok.Data;

/**
 * @author miemie
 * @since 2025/9/1
 */
@Data
@Builder
public class Overwrite {
    /**
     * 定位源码片段；REPLACE 时为原样匹配的文本，DELETE_INNER_CLASS 时为成员内部类的简单类名，
     * APPEND_METHOD 时为完整方法源码。
     */
    private String source;

    @Builder.Default
    private Operate operate = Operate.APPEND;

    private String target;

    public enum Operate {
        APPEND, // 插入后面
        APPEND_METHOD, // 将 source 中的完整方法追加到类末尾，不需要 target
        DELETE, // 删除
        DELETE_INNER_CLASS, // 按 source 指定的类名删除成员内部类，不需要 target
        COVERAGE, // 覆盖
        REPLACE, // 将全部 source 文本原样替换为 target，不处理缩进或换行
    }
}
