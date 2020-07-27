package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.MetaObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-11
 */
class MetaObjectHandlerTest {

    private static final MybatisConfiguration configuration = new MybatisConfiguration();
    private static final MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");

    @BeforeAll
    static void initXt() {
        TableInfoHelper.initTableInfo(assistant, Xt.class);
    }

    @Test
    void strictInsertFill1() {
        final MetaObjectHandler handler = new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                strictInsertFill(metaObject, "name", String.class, "2222");
                strictInsertFill(metaObject, "age", Integer.class, 222);
                strictInsertFill(metaObject, "birthday", LocalDate.class, LocalDate.now());
                strictInsertFill(metaObject, "dateTime", LocalDateTime.class, LocalDateTime.now());
                // set 子类
                strictInsertFill(metaObject, "ojbk", Ojbk.class, new OjbkXx());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
            }
        };

        Xt xt = new Xt();
        MetaObject metaObject = configuration.newMetaObject(xt);
        handler.insertFill(metaObject);
        check(xt);
    }

    @Test
    void strictInsertFill2() {
        final MetaObjectHandler handler = new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                strictInsertFill(metaObject, "name", () -> "2222", String.class);
                strictInsertFill(metaObject, "age", () -> 222, Integer.class);
                strictInsertFill(metaObject, "birthday", LocalDate::now, LocalDate.class);
                strictInsertFill(metaObject, "dateTime", LocalDateTime::now, LocalDateTime.class);
                // set 子类
                strictInsertFill(metaObject, "ojbk", OjbkXx::new, Ojbk.class);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
            }
        };

        Xt xt = new Xt();
        MetaObject metaObject = configuration.newMetaObject(xt);
        handler.insertFill(metaObject);
        check(xt);
    }

    @Test
    void strictUpdateFill1() {
        final MetaObjectHandler handler = new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "name", String.class, "2222");
                strictUpdateFill(metaObject, "age", Integer.class, 222);
                strictUpdateFill(metaObject, "birthday", LocalDate.class, LocalDate.now());
                strictUpdateFill(metaObject, "dateTime", LocalDateTime.class, LocalDateTime.now());
                // set 子类
                strictUpdateFill(metaObject, "ojbk", Ojbk.class, new OjbkXx());
            }
        };

        Xt xt = new Xt();
        MetaObject metaObject = configuration.newMetaObject(xt);
        handler.updateFill(metaObject);
        check(xt);
    }

    @Test
    void strictUpdateFill2() {
        final MetaObjectHandler handler = new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "name", () -> "2222", String.class);
                strictUpdateFill(metaObject, "age", () -> 222, Integer.class);
                strictUpdateFill(metaObject, "birthday", LocalDate::now, LocalDate.class);
                strictUpdateFill(metaObject, "dateTime", LocalDateTime::now, LocalDateTime.class);
                // set 子类
                strictUpdateFill(metaObject, "ojbk", OjbkXx::new, Ojbk.class);
            }
        };

        Xt xt = new Xt();
        MetaObject metaObject = configuration.newMetaObject(xt);
        handler.updateFill(metaObject);
        check(xt);
    }

    void check(Xt xt) {
        System.out.println(xt);
        assertThat(xt.getName()).isNotNull();
        assertThat(xt.getAge()).isNotNull();
        assertThat(xt.getBirthday()).isNotNull();
        assertThat(xt.getDateTime()).isNotNull();
        assertThat(xt.getOjbk()).isNotNull();
    }

    @Data
    static class Xt {
        private Long id;

        @TableField(fill = FieldFill.INSERT_UPDATE)
        private String name;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Integer age;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDate birthday;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime dateTime;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Ojbk ojbk;
    }

    @Data
    static class Ojbk {
        private String xx = "木大木大木大木大,欧拉欧拉欧拉欧拉";
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    static class OjbkXx extends Ojbk {
    }
}
