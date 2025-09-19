package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.session.Configuration}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class Configuration extends OverwriteFile {

    public Configuration() {
        addStep(i -> i
            .source("""
                import java.util.function.BiFunction;
                """)
            .target("""
                import lombok.Getter;
                import lombok.Setter;
                """)
        );
        addStep(i -> i
            .source("""
                protected boolean mapUnderscoreToCamelCase;
                """)
            .target("""
                protected boolean mapUnderscoreToCamelCase = true;
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                protected boolean argNameBasedConstructorAutoMapping;
                """)
            .target("""
                /**
                 * 是否生成短key缓存
                 *
                 * @since 3.4.0
                 */
                @Setter
                @Getter
                protected boolean useGeneratedShortKey = true;
                """)
        );
    }
}
