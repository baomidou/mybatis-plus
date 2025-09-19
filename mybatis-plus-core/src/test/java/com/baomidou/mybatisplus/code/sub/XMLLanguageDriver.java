package com.baomidou.mybatisplus.code.sub;

import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * {@link org.apache.ibatis.scripting.xmltags.XMLLanguageDriver}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class XMLLanguageDriver extends OverwriteFile {

    public XMLLanguageDriver() {
        addStep(i -> i
            .source("""
                import org.apache.ibatis.session.Configuration;
                """)
            .target("""
                import java.util.List;
                import com.baomidou.mybatisplus.core.config.GlobalConfig;
                import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
                import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
                import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
                import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
                import org.apache.ibatis.builder.IncompleteElementException;
                """)
        );
        addStep(i -> i
            .source("""
                public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
                """)
            .target("""
                GlobalConfig.DbConfig config = GlobalConfigUtils.getDbConfig(configuration);
                if (config.isReplacePlaceholder()) {
                    List<String> find = SqlUtils.findPlaceholder(script);
                    if (CollectionUtils.isNotEmpty(find)) {
                        try {
                            script = SqlUtils.replaceSqlPlaceholder(script, find, config.getEscapeSymbol());
                        } catch (MybatisPlusException e) {
                            throw new IncompleteElementException(e);
                        }
                    }
                }
                """)
        );
    }
}
