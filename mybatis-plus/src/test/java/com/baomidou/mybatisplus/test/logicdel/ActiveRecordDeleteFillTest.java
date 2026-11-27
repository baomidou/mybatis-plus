package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.spring.activerecord.Model;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class ActiveRecordDeleteFillTest extends DbTestSupport<ActiveRecordDeleteFillTest.InvoiceMapper> {

    @Test
    void fillsAuditColumnWhenDeletingById() {
        jdbcTemplate.update("INSERT INTO invoice (id, deleted) VALUES (1, 0), (2, 0)");
        Invoice invoice = new Invoice();
        invoice.setId(2L);
        assertThat(invoice.deleteById(1L)).isTrue();
        assertThat(
                jdbcTemplate
                        .queryForObject("SELECT updated_by FROM invoice WHERE id = 1 AND deleted = 1", String.class))
                .isEqualTo("billing");
        assertThat(invoice.getId()).isEqualTo(2L);
        assertThat(invoice.getUpdatedBy()).isNull();
        assertThat(invoice.selectById()).isNotNull();
    }

    @Override
    protected GlobalConfig globalConfig() {
        return super.globalConfig().setMetaObjectHandler(new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "updatedBy", String.class, "billing");
            }
        });
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "DROP TABLE IF EXISTS invoice",
                "CREATE TABLE invoice (id BIGINT PRIMARY KEY, deleted INT, updated_by VARCHAR(32))");
    }

    interface InvoiceMapper extends BaseMapper<Invoice> {
    }

    @Getter
    @Setter
    static class Invoice extends Model<Invoice> {

        private Long id;
        @TableLogic
        private Integer deleted;
        @TableField(fill = FieldFill.UPDATE)
        private String updatedBy;
    }
}
