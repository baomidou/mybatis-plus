package com.baomidou.mybatisplus.code;

import lombok.*;

import java.util.List;

/**
 * @author miemie
 * @since 2025/9/1
 */
@Data
@Builder
public class Overwrite {
    private Point front;
    private Point behind;
    /*  */
    @Singular
    private List<Content> contents;
    @Singular("addImport")
    private List<String> imports;

    @Getter
    @AllArgsConstructor
    public static class Point {
        private int line;
        private String content;

        public Point(String content) {
            this.content = content;
        }
    }

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
