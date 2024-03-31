package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


class MybatisParameterHandlerTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Model {

        @TableId(type = IdType.INPUT)
        private Long id;

        private String name;

        @TableField(fill = FieldFill.INSERT)
        private String insertOperator;

        @TableField(fill = FieldFill.UPDATE)
        private String updateOperator;

        Model(String name) {
            this.name = name;
        }

        Model(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    void test() {
        Configuration configuration = new MybatisConfiguration();
        BoundSql boundSql = mock(BoundSql.class);
        StaticSqlSource staticSqlSource = mock(StaticSqlSource.class);
        GlobalConfigUtils.getGlobalConfig(configuration).setIdentifierGenerator(new DefaultIdentifierGenerator()).setMetaObjectHandler(new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                setFieldValByName("id", 666L, metaObject);
                setFieldValByName("insertOperator", "咩咩", metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                setFieldValByName("updateOperator", "逗号", metaObject);
            }
        });
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), Model.class);

        Model model = new Model("坦克");
        //查询
        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.SELECT).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getId()).isNull();
        assertThat(model.getInsertOperator()).isNull();
        assertThat(model.getUpdateOperator()).isNull();


        // 普通插入
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getId()).isNotNull();
        assertThat(model.getInsertOperator()).isNotNull();
        assertThat(model.getUpdateOperator()).isNull();


        //map参数
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.INSERT).build();
        model = new Model("坦克");
        Map<String, Object> params1 = new HashMap<>();
        params1.put(Constants.ENTITY, model);
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model.getId()).isNotNull();
        assertThat(model.getInsertOperator()).isNotNull();
        assertThat(model.getUpdateOperator()).isNull();
//        //map参数
//        Model model3 = new Model("坦克");
//        Map<String, Object> params2 = new HashMap<>();
//        params2.put(Constants.ENTITY, new HashMap<String, Object>() {{
//            put(Constants.MP_OPTLOCK_ET_ORIGINAL, model3);
//        }});
//        MybatisDefaultParameterHandler.processParameter(mappedStatement, params2);
//        Assertions.assertNotNull(model3.getId());
//        Assertions.assertNotNull(model3.getInsertOperator());
//        Assertions.assertNull(model3.getUpdateOperator());

        params1 = new HashMap<>();
        Model model1 = new Model();
        Model model2 = new Model();
        params1.put("arrays", new Model[]{model1, model2});
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model1.getId()).isNotNull();
        assertThat(model2.getId()).isNotNull();
        assertThat(model1.getInsertOperator()).isNotNull();
        assertThat(model1.getUpdateOperator()).isNull();

        model1 = new Model();
        model2 = new Model();
        params1 = new HashMap<>();
        params1.put(Constants.ARRAY, new Model[]{model1, model2});
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model1.getId()).isNotNull();
        assertThat(model2.getId()).isNotNull();
        assertThat(model1.getInsertOperator()).isNotNull();
        assertThat(model1.getUpdateOperator()).isNull();

        model1 = new Model();
        model2 = new Model();
        params1 = new HashMap<>();
        params1.put(Constants.ARRAY, new Object[]{model1, model2});
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model1.getId()).isNotNull();
        assertThat(model2.getId()).isNotNull();
        assertThat(model1.getInsertOperator()).isNotNull();
        assertThat(model1.getUpdateOperator()).isNull();


        model1 = new Model();
        model2 = new Model();
        params1 = new HashMap<>();
        params1.put(Constants.LIST, Arrays.asList(model1, model2));
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model1.getId()).isNotNull();
        assertThat(model2.getId()).isNotNull();
        assertThat(model1.getInsertOperator()).isNotNull();
        assertThat(model1.getUpdateOperator()).isNull();


        model1 = new Model();
        model2 = new Model();
        params1 = new HashMap<>();
        params1.put(Constants.COLL, Arrays.asList(model1, model2));
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model1.getId()).isNotNull();
        assertThat(model2.getId()).isNotNull();
        assertThat(model1.getInsertOperator()).isNotNull();
        assertThat(model1.getUpdateOperator()).isNull();

        model1 = new Model();
        model2 = new Model();
        params1 = new HashMap<>();
        params1.put("collection", Arrays.asList(model1, model2));
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        assertThat(model1.getId()).isNotNull();
        assertThat(model2.getId()).isNotNull();
        assertThat(model1.getInsertOperator()).isNotNull();
        assertThat(model1.getUpdateOperator()).isNull();


        params1 = new HashMap<>();
        params1.put(Constants.ARRAY, new Object());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1.put(Constants.ARRAY, new HashMap<>());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1.put(Constants.ARRAY, new Object[]{});
        new MybatisParameterHandler(mappedStatement, params1, boundSql);


        params1.put(Constants.ARRAY, null);
        new MybatisParameterHandler(mappedStatement, params1, boundSql);


        params1 = new HashMap<>();
        params1.put(Constants.COLL, new Object());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1 = new HashMap<>();
        params1.put(Constants.COLL, new HashMap<>());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1.put(Constants.COLL, null);
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1.put(Constants.COLL, new ArrayList<>());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1 = new HashMap<>();
        params1.put("collection", new Object());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1 = new HashMap<>();
        params1.put("collection", new Object());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1 = new HashMap<>();
        params1.put("collection", new HashMap<>());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1.put("collection", null);
        new MybatisParameterHandler(mappedStatement, params1, boundSql);

        params1.put("collection", new ArrayList<>());
        new MybatisParameterHandler(mappedStatement, params1, boundSql);
        //普通更新
        model = new Model(1L, "坦克");
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.UPDATE).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getId()).isNotNull();
        assertThat(model.getUpdateOperator()).isNotNull();


        //批量插入
        List<Model> list = Arrays.asList(new Model("坦克一号"), new Model("坦克二号"));
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, list, boundSql);
        list.forEach(m -> {
            assertThat(m.getId()).isNotNull();
            assertThat(m.getInsertOperator()).isNotNull();
            assertThat(m.getUpdateOperator()).isNull();
        });


        //批量更新
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.UPDATE).build();
        new MybatisParameterHandler(mappedStatement, list, boundSql);
        list.forEach(m -> {
            assertThat(m.getId()).isNotNull();
            assertThat(m.getInsertOperator()).isNotNull();
            assertThat(m.getUpdateOperator()).isNotNull();
        });

        Model[] arrays = new Model[]{new Model("坦克一号"), new Model("坦克二号")};
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, arrays, boundSql);
        Arrays.stream(arrays).forEach(m -> {
            assertThat(m.getId()).isNotNull();
            assertThat(m.getInsertOperator()).isNotNull();
            assertThat(m.getUpdateOperator()).isNull();
        });

        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.UPDATE).build();
        new MybatisParameterHandler(mappedStatement, arrays, boundSql);
        Arrays.stream(arrays).forEach(m -> {
            assertThat(m.getId()).isNotNull();
            assertThat(m.getInsertOperator()).isNotNull();
            assertThat(m.getUpdateOperator()).isNotNull();
        });

        byte[] bytes1 = new byte[]{};
        model = new Model();
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, Map.of("test", bytes1, Constants.ENTITY, model), boundSql);
        assertThat(model.getId()).isNotNull();
        assertThat(model.getInsertOperator()).isNotNull();
        assertThat(model.getUpdateOperator()).isNull();

        Byte[] bytes2 = new Byte[]{};
        model = new Model();
        mappedStatement = new MappedStatement.Builder(configuration, "***", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, Map.of("test", bytes2, Constants.ENTITY, model), boundSql);
        assertThat(model.getId()).isNotNull();
        assertThat(model.getInsertOperator()).isNotNull();
        assertThat(model.getUpdateOperator()).isNull();

    }

    @Test
    void testIgnoreMapperStatement() {
        MappedStatement mappedStatement;
        Configuration configuration = new MybatisConfiguration();
        BoundSql boundSql = mock(BoundSql.class);
        StaticSqlSource staticSqlSource = mock(StaticSqlSource.class);
        GlobalConfigUtils.getGlobalConfig(configuration).setIdentifierGenerator(DefaultIdentifierGenerator.getInstance()).setMetaObjectHandler(new MetaObjectHandler() {

            @Override
            public boolean openInsertFill(MappedStatement mappedStatement) {
                //根据msId跳过填充
                return !"com.baomidou.demo.ignoreFillSave".equals(mappedStatement.getId());
            }

            @Override
            public boolean openUpdateFill(MappedStatement mappedStatement) {
                //根据msId跳过填充
                return !"com.baomidou.demo.ignoreFillUpdate".equals(mappedStatement.getId());
            }

            @Override
            public void insertFill(MetaObject metaObject) {
                setFieldValByName("id", 666L, metaObject);
                setFieldValByName("insertOperator", "秋秋", metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                setFieldValByName("updateOperator", "秋秋", metaObject);
            }

        });
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(configuration, ""), Model.class);
        Model model;

        model = new Model("测试新增忽略填充");
        mappedStatement = new MappedStatement.Builder(configuration, "com.baomidou.demo.ignoreFillSave", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getId()).isNull();
        assertThat(model.getInsertOperator()).isNull();

        model = new Model("测试新增填充");
        mappedStatement = new MappedStatement.Builder(configuration, "com.baomidou.demo.save", staticSqlSource, SqlCommandType.INSERT).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getId()).isNotNull();
        assertThat(model.getInsertOperator()).isNotNull();
        assertThat(model.getInsertOperator()).isEqualTo("秋秋");

        model = new Model("测试更新忽略填充");
        mappedStatement = new MappedStatement.Builder(configuration, "com.baomidou.demo.ignoreFillUpdate", staticSqlSource, SqlCommandType.UPDATE).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getUpdateOperator()).isNull();

        model = new Model("测试更新填充");
        mappedStatement = new MappedStatement.Builder(configuration, "com.baomidou.demo.update", staticSqlSource, SqlCommandType.UPDATE).build();
        new MybatisParameterHandler(mappedStatement, model, boundSql);
        assertThat(model.getUpdateOperator()).isEqualTo("秋秋");
    }

}
