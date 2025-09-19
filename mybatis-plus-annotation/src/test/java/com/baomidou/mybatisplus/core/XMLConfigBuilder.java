package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.builder.xml.XMLConfigBuilder}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class XMLConfigBuilder extends OverwriteFile {

    public XMLConfigBuilder() {
        addStep(i -> i
            .source("""
                configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
                """)
            .target("""
                configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), true));
                configuration.setUseGeneratedShortKey(booleanValueOf(props.getProperty("useGeneratedShortKey"), true));
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
