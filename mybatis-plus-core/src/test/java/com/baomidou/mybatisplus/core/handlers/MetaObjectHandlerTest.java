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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-11
 */
@Tag("unit")
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

    /**
     * 以下两例自聚合模块 Kotlin 版 {@code test.h2.MetaObjectHandlerTest} 并入：同一个 MetaObjectHandler 不该有两处测试，而那两条行为（原始类型 Class
     * 的静默跳过、已有值不覆盖）这里都没覆盖。
     */
    @Test
    void shouldNotFillWhenFieldTypeIsPrimitiveClass() {
        assertThat(long.class).as("原始 long.class 和实体属性的 java.lang.Long 是两个不同的 Class").isNotEqualTo(Long.class);

        final MetaObjectHandler handler = new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                // 故意用原始类型 long.class，用例要靠它演示填充被静默跳过
                strictInsertFill(metaObject, "count", long.class, 123456L);
                strictInsertFill(metaObject, "age", Integer.class, 222);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
            }
        };

        Xt xt = new Xt();
        handler.insertFill(configuration.newMetaObject(xt));
        assertThat(xt.getCount()).as("按原始类型 Class 匹配不到属性，既不报错也不填").isNull();
        assertThat(xt.getAge()).as("换成包装类型就填得上").isEqualTo(222);
    }

    @Test
    void shouldKeepValueAlreadySetOnEntity() {
        final MetaObjectHandler handler = new MetaObjectHandler() {

            @Override
            public void insertFill(MetaObject metaObject) {
                strictInsertFill(metaObject, "name", String.class, "2222");
                strictInsertFill(metaObject, "age", Integer.class, 222);
                strictInsertFill(metaObject, "birthday", LocalDate.class, LocalDate.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
            }
        };

        Xt xt = new Xt();
        xt.setName("原有值");
        handler.insertFill(configuration.newMetaObject(xt));
        assertThat(xt.getName()).as("有值不覆盖").isEqualTo("原有值");
        assertThat(xt.getAge()).isNotNull();
        assertThat(xt.getBirthday()).isNotNull();
    }

    @Data
    static class Xt {

        private Long id;

        @TableField(fill = FieldFill.INSERT_UPDATE)
        private String name;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Integer age;
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Long count;
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
