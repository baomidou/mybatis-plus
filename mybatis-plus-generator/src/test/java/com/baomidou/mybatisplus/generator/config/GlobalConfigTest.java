package com.baomidou.mybatisplus.generator.config;

import com.baomidou.mybatisplus.generator.config.builder.GeneratorBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2020/10/12.
 */
public class GlobalConfigTest {

    private void buildAssert(GlobalConfig globalConfig) {
        assertThat(globalConfig.isKotlin()).isTrue();
        assertThat(globalConfig.isSwagger()).isTrue();
        assertThat(globalConfig.isOpen()).isTrue();
        assertThat(globalConfig.getAuthor()).isEqualTo("mp");
        assertThat(globalConfig.getOutputDir()).isEqualTo("/temp/code");
        assertThat(globalConfig.getDateType()).isEqualTo(DateType.SQL_PACK);
        assertThat(globalConfig.isServiceInterface()).isTrue();
    }

    @Test
    void builderTest() {
        GlobalConfig globalConfig = GeneratorBuilder.globalConfigBuilder().author("mp").enableKotlin().enableSwagger()
                .dateType(DateType.SQL_PACK).outputDir("/temp/code").build();
        buildAssert(globalConfig);
    }

    @Test
    void commentDateTest() {
        String defaultDate = GeneratorBuilder.globalConfig().getCommentDate();
        String commentDate = GeneratorBuilder.globalConfigBuilder().commentDate("yyyy-MM-dd").build().getCommentDate();
        assertThat(commentDate).isEqualTo(defaultDate);
        assertThat(GeneratorBuilder.globalConfigBuilder().commentDate(() -> "2200年11月10日").build().getCommentDate())
                .isEqualTo("2200年11月10日");
        assertThat(
                GeneratorBuilder.globalConfigBuilder()
                        .commentDate(() -> LocalDate.now().format(DateTimeFormatter.ISO_DATE)).build().getCommentDate())
                .isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
    }
}
