package com.baomidou.mybatisplus.test.batch;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class InsertBatchSomeColumnTest extends DbTestSupport<InsertBatchSomeColumnTest.DefaultRowMapper> {

    @Test
    void insertsOneDefaultRowPerEntityAndReturnsTheGeneratedIds() {
        var rows = List.of(new DefaultRow(), new DefaultRow());
        doTestAutoCommit(mapper -> assertThat(mapper.insertBatchSomeColumn(rows)).isEqualTo(2));
        assertThat(rows).extracting(DefaultRow::getId).doesNotContainNull().doesNotHaveDuplicates();
        assertThat(jdbcTemplate.queryForList("SELECT marker FROM default_batch_rows", Integer.class))
                .containsExactly(42, 42);
    }

    @Override
    protected GlobalConfig globalConfig() {
        return super.globalConfig().setSqlInjector(new DefaultSqlInjector() {

            @Override
            public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass,
                    TableInfo info) {
                List<AbstractMethod> methods = super.getMethodList(configuration, mapperClass, info);
                methods.add(new InsertBatchSomeColumn());
                return methods;
            }
        });
    }

    @Override
    protected List<String> tableSql() {
        return List.of(
                "DROP TABLE IF EXISTS default_batch_rows",
                "CREATE TABLE default_batch_rows (id BIGINT AUTO_INCREMENT PRIMARY KEY, marker INT DEFAULT 42)");
    }

    interface DefaultRowMapper extends BaseMapper<DefaultRow> {

        int insertBatchSomeColumn(List<DefaultRow> rows);
    }

    @Getter
    @Setter
    @TableName("default_batch_rows")
    static class DefaultRow {

        @TableId(type = IdType.AUTO)
        private Long id;
    }
}
