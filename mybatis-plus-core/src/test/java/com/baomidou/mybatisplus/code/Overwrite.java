package com.baomidou.mybatisplus.code;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * @author miemie
 * @since 2025/9/1
 */
@Data
@Builder
public class Overwrite {
    private String front;
    private String behind;
    private int interval;
    /*  */
    @Singular
    private List<Content> contents;
    @Singular("addImport")
    private List<String> imports;

    @Data
    @Builder
    public static class Content {
        @Builder.Default
        private Operate operate = Operate.INSERT;
        private int frontDown;
        private int behindUp;
        private String code;
    }

    enum Operate {
        INSERT,
        DELETE
    }
}
