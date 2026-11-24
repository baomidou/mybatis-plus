package com.baomidou.mybatisplus.core;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class MybatisXMLMapperBuilderTest {

    private static final String NAMESPACE = AutoResultMapper.class.getName();
    private static final String AUTO_RESULT_MAP = NAMESPACE + ".mybatis-plus_AutoResultEntity";
    private static final String XML_RESOURCE = NAMESPACE.replace('.', '/') + ".xml";

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void autoResultMapIsAvailableBeforeXmlIsParsed(boolean xmlFirst) throws IOException {
        TrackingConfiguration configuration = new TrackingConfiguration();
        if (xmlFirst) {
            parseXml(configuration);
        } else {
            configuration.addMapper(AutoResultMapper.class);
        }

        // 最终 pending 为空不能证明没有重试，记录首次解析是否挂起。
        assertThat(configuration.incompleteStatements).isZero();
        assertThat(configuration.incompleteResultMaps).isZero();
        assertThat(configuration.autoResultMaps).isEqualTo(1);
        ResultMap resultMap = configuration.getResultMap(AUTO_RESULT_MAP);
        assertThat(resultMap.getResultMappings()).extracting(ResultMapping::getProperty).containsExactly("id", "name");
        assertThat(resultMap.getResultMappings().get(1).getJdbcType()).isEqualTo(JdbcType.VARCHAR);
        assertThat(resultMap.getResultMappings().get(1).getTypeHandler()).isInstanceOf(StringTypeHandler.class);

        // 与默认 CRUD 同名，确保 XML 语句没有因为延迟注册被默认实现抢占。
        MappedStatement selectById = configuration.getMappedStatement(NAMESPACE + ".selectById", false);
        assertThat(selectById.getBoundSql(1L).getSql().replaceAll("\\s+", " ").trim())
                .isEqualTo("SELECT id, name FROM zz_xml_auto_result WHERE id = ? AND name = 'xml'");
        assertThat(selectById.getResultMaps()).containsExactly(resultMap);
        assertThat(configuration.getResultMap(NAMESPACE + ".extendedResultMap").getResultMappings())
                .extracting(ResultMapping::getProperty).containsExactlyInAnyOrder("id", "name");
        assertThat(configuration.getMappedStatement(NAMESPACE + ".selectAnnotated", false).getResultMaps())
                .containsExactly(resultMap);
        assertThat(configuration.hasStatement(NAMESPACE + ".insert", false)).isTrue();
        assertThat(configuration.hasMapper(AutoResultMapper.class)).isTrue();

        parseXml(configuration);
        configuration.addMapper(AutoResultMapper.class);
        assertThat(configuration.autoResultMaps).isEqualTo(1);
    }

    @Test
    void preparesMetadataForEachConfiguration() throws IOException {
        TrackingConfiguration first = new TrackingConfiguration();
        parseXml(first);
        TrackingConfiguration second = new TrackingConfiguration();
        second.addMapper(AutoResultMapper.class);

        assertThat(first.getResultMap(AUTO_RESULT_MAP)).isNotSameAs(second.getResultMap(AUTO_RESULT_MAP));
        assertThat(TableInfoHelper.getTableInfo(AutoResultEntity.class).getConfiguration()).isSameAs(second);
        assertThat(second.incompleteStatements).isZero();
        assertThat(second.incompleteResultMaps).isZero();
        assertThat(second.autoResultMaps).isEqualTo(1);
    }

    @Test
    void namespaceDoesNotNeedAMapperClass() {
        TrackingConfiguration configuration = new TrackingConfiguration();
        parseXml(configuration, "com.baomidou.mybatisplus.core.NoSuchMapper");

        assertThat(
                configuration.getMappedStatement("com.baomidou.mybatisplus.core.NoSuchMapper.query", false)
                        .getResultMaps().get(0).getType())
                .isEqualTo(Long.class);
        assertThat(configuration.autoResultMaps).isZero();
    }

    @Test
    void ordinaryMapperDoesNotNeedTableMetadata() {
        TrackingConfiguration configuration = new TrackingConfiguration();
        parseXml(configuration, OrdinaryMapper.class.getName());

        assertThat(configuration.hasMapper(OrdinaryMapper.class)).isTrue();
        assertThat(configuration.hasStatement(OrdinaryMapper.class.getName() + ".query", false)).isTrue();
        assertThat(configuration.autoResultMaps).isZero();
    }

    private static void parseXml(MybatisConfiguration configuration) throws IOException {
        try (InputStream input = AutoResultMapper.class.getResourceAsStream("/" + XML_RESOURCE)) {
            new MybatisXMLMapperBuilder(input, configuration, XML_RESOURCE, configuration.getSqlFragments()).parse();
        }
    }

    private static void parseXml(MybatisConfiguration configuration, String namespace) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\""
                + " \"https://mybatis.org/dtd/mybatis-3-mapper.dtd\">" + "<mapper namespace=\"" + namespace + "\">"
                + "<select id=\"query\" resultType=\"java.lang.Long\">SELECT 1</select></mapper>";
        new MybatisXMLMapperBuilder(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), configuration,
                namespace + ".xml", configuration.getSqlFragments()).parse();
    }

    interface AutoResultMapper extends BaseMapper<AutoResultEntity> {

        AutoResultEntity selectExtended();

        @Select("SELECT id, name FROM zz_xml_auto_result")
        @org.apache.ibatis.annotations.ResultMap("mybatis-plus_AutoResultEntity")
        AutoResultEntity selectAnnotated();
    }

    interface OrdinaryMapper {

        Long query();
    }

    @TableName(value = "zz_xml_auto_result", autoResultMap = true)
    static class AutoResultEntity {

        private Long id;
        @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = StringTypeHandler.class)
        private String name;
    }

    private static class TrackingConfiguration extends MybatisConfiguration {

        private int incompleteStatements;
        private int incompleteResultMaps;
        private int autoResultMaps;

        @Override
        public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
            incompleteStatements++;
            super.addIncompleteStatement(incompleteStatement);
        }

        @Override
        public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
            incompleteResultMaps++;
            super.addIncompleteResultMap(resultMapResolver);
        }

        @Override
        public void addResultMap(ResultMap resultMap) {
            if (AUTO_RESULT_MAP.equals(resultMap.getId())) {
                autoResultMaps++;
            }
            super.addResultMap(resultMap);
        }
    }
}
