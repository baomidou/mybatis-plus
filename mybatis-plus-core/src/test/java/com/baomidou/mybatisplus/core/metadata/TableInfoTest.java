package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieiqurong
 */
@Tag("unit")
public class TableInfoTest {

    @TableName("zz_projection_exclusion_record")
    static class ProjectionExclusionRecord {

        @TableId("record_id")
        Long id;
        String name;
        Integer age;
    }

    @Test
    void excludingAllOrdinaryFieldsKeepsOnlyThePrimaryKey() {
        TableInfo info = projectionInfo();
        assertThat(info.chooseSelect(TableFieldInfo::isSelect, Arrays.asList("name", "age")))
                .isEqualTo("record_id AS id");
        assertThat(info.chooseSelect(field -> false, Collections.singletonList("missing")))
                .isEqualTo("record_id AS id");
        assertThat(SqlUtils.getSelectBody("zz_projection_exclusion_record-name,age", null, null, "\""))
                .isEqualTo("record_id AS id");
    }

    @Test
    void exclusionsPreserveRemainingColumnsAndExplicitPrimaryKeyExclusion() {
        TableInfo info = projectionInfo();
        assertThat(info.chooseSelect(TableFieldInfo::isSelect, Collections.singletonList("age")))
                .isEqualTo("record_id AS id,name");
        assertThat(info.chooseSelect(TableFieldInfo::isSelect, Collections.singletonList("id"))).isEqualTo("name,age");
        assertThat(info.chooseSelect(TableFieldInfo::isSelect, Arrays.asList("id", "name", "age"))).isEmpty();
        assertThat(info.chooseSelect(TableFieldInfo::isSelect, Collections.emptyList()))
                .isEqualTo("record_id AS id,name,age");
        assertThat(SqlUtils.getSelectBody("zz_projection_exclusion_record-name,age", "r", "row", "\""))
                .isEqualTo("r.record_id AS \"row.id\"");
    }

    @TableName("zz_projection_exclusion_key_only")
    static class ProjectionExclusionKeyOnly {

        @TableId
        Long id;
    }

    @TableName("zz_projection_exclusion_no_key")
    static class ProjectionExclusionNoKey {

        String name;
        Integer age;
    }

    @Test
    void exclusionsHandleKeyOnlyAndKeylessEntities() {
        TableInfo keyOnly = TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProjectionExclusionKeyOnly.class);
        assertThat(keyOnly.chooseSelect(TableFieldInfo::isSelect, Collections.singletonList("missing")))
                .isEqualTo("id");
        TableInfo noKey = TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProjectionExclusionNoKey.class);
        assertThat(noKey.chooseSelect(TableFieldInfo::isSelect, Collections.singletonList("age"))).isEqualTo("name");
        assertThat(noKey.chooseSelect(TableFieldInfo::isSelect, Arrays.asList("name", "age"))).isEmpty();
    }

    private TableInfo projectionInfo() {
        return TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProjectionExclusionRecord.class);
    }

    static class Demo {

        String name;

    }

    @Test
    void shouldCreateTableInfoAndSetPropertyValues() {
        TableInfo tableInfo;
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        tableInfo = new TableInfo(configuration, Demo.class);
        Demo demo = tableInfo.newInstance();
        tableInfo.setPropertyValue(demo, "name", "test");
        assertThat(tableInfo.getPropertyValue(demo, "name")).isEqualTo("test");
        assertThat(tableInfo.isUnderCamel()).isTrue();
        assertThat(tableInfo.getReflector()).isNotNull();
        tableInfo = new TableInfo(configuration, Object.class);
        assertThat(tableInfo.isUnderCamel()).isTrue();
        assertThat(tableInfo.getReflector()).isNotNull();
    }

}
