/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * 钉住 {@link GlobalConfigUtils} 的默认值、按 Configuration 缓存与单次安装语义。
 *
 * @author TaoYu
 */
class GlobalConfigUtilsTest {

    @Test
    void defaultsProvideDbConfigWithDocumentedDefaults() {
        GlobalConfig.DbConfig dbConfig = GlobalConfigUtils.defaults().getDbConfig();
        assertThat(dbConfig).isNotNull();
        assertThat(dbConfig.getIdType()).isEqualTo(IdType.ASSIGN_ID);
        assertThat(dbConfig.getLogicDeleteValue()).isEqualTo("1");
        assertThat(dbConfig.getLogicNotDeleteValue()).isEqualTo("0");
        assertThat(dbConfig.getInsertStrategy()).isEqualTo(FieldStrategy.NOT_NULL);
        assertThat(dbConfig.getUpdateStrategy()).isEqualTo(FieldStrategy.NOT_NULL);
        assertThat(dbConfig.getWhereStrategy()).isEqualTo(FieldStrategy.NOT_NULL);
        assertThat(dbConfig.isTableUnderline()).isTrue();
        assertThat(dbConfig.isCapitalMode()).isFalse();
    }

    @Test
    void getGlobalConfigCachesDefaultsPerConfiguration() {
        Configuration first = new Configuration();
        Configuration second = new Configuration();
        GlobalConfig cached = GlobalConfigUtils.getGlobalConfig(first);
        assertThat(cached).isNotNull();
        assertThat(GlobalConfigUtils.getGlobalConfig(first)).isSameAs(cached);
        assertThat(GlobalConfigUtils.getGlobalConfig(second)).isNotSameAs(cached);
    }

    @Test
    void getGlobalConfigRejectsNullConfiguration() {
        assertThatThrownBy(() -> GlobalConfigUtils.getGlobalConfig(null)).isInstanceOf(MybatisPlusException.class)
                .hasMessageContaining("You need Initialize MybatisConfiguration");
    }

    @Test
    void setGlobalConfigInstallsOnlyOncePerConfiguration() {
        Configuration configuration = new Configuration();
        GlobalConfig installed = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig());
        GlobalConfigUtils.setGlobalConfig(configuration, installed);
        assertThat(GlobalConfigUtils.getGlobalConfig(configuration)).isSameAs(installed);

        GlobalConfig ignored = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig());
        GlobalConfigUtils.setGlobalConfig(configuration, ignored);
        assertThat(GlobalConfigUtils.getGlobalConfig(configuration)).isSameAs(installed);

        Configuration materialized = new Configuration();
        GlobalConfig defaults = GlobalConfigUtils.getGlobalConfig(materialized);
        GlobalConfigUtils.setGlobalConfig(materialized, installed);
        assertThat(GlobalConfigUtils.getGlobalConfig(materialized)).isSameAs(defaults);
    }

    @Test
    void setGlobalConfigRejectsNullArguments() {
        Configuration configuration = new Configuration();
        GlobalConfig globalConfig = new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig());
        assertThatThrownBy(() -> GlobalConfigUtils.setGlobalConfig(null, globalConfig))
                .isInstanceOf(MybatisPlusException.class).hasMessageContaining("Could not setGlobalConfig");
        assertThatThrownBy(() -> GlobalConfigUtils.setGlobalConfig(configuration, null))
                .isInstanceOf(MybatisPlusException.class).hasMessageContaining("Could not setGlobalConfig");
    }

    @Test
    void delegatingGettersReflectTheInstalledConfig() {
        Configuration configuration = new Configuration();
        IKeyGenerator keyGenerator = mock(IKeyGenerator.class);
        MetaObjectHandler metaObjectHandler = mock(MetaObjectHandler.class);
        ISqlInjector sqlInjector = mock(ISqlInjector.class);
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.INPUT);
        dbConfig.setKeyGenerators(List.of(keyGenerator));
        GlobalConfig globalConfig = new GlobalConfig().setDbConfig(dbConfig).setMetaObjectHandler(metaObjectHandler)
                .setSqlInjector(sqlInjector).setSuperMapperClass(BaseMapper.class);
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);

        assertThat(GlobalConfigUtils.getDbConfig(configuration)).isSameAs(dbConfig);
        assertThat(GlobalConfigUtils.getIdType(configuration)).isEqualTo(IdType.INPUT);
        assertThat(GlobalConfigUtils.getKeyGenerators(configuration)).containsExactly(keyGenerator);
        assertThat(GlobalConfigUtils.getMetaObjectHandler(configuration)).containsSame(metaObjectHandler);
        assertThat(GlobalConfigUtils.getSqlInjector(configuration)).isSameAs(sqlInjector);
        assertThat(GlobalConfigUtils.getSuperMapperClass(configuration)).isEqualTo(BaseMapper.class);
        assertThat(GlobalConfigUtils.getMapperRegistryCache(configuration))
                .isSameAs(globalConfig.getMapperRegistryCache());
        assertThat(GlobalConfigUtils.isSupperMapperChildren(configuration, BaseMapper.class)).isTrue();
        assertThat(GlobalConfigUtils.isSupperMapperChildren(configuration, Mapper.class)).isFalse();
        assertThat(GlobalConfigUtils.isSupperMapperChildren(configuration, Runnable.class)).isFalse();
    }

    @Test
    void optionalHandlersFollowTheInstalledConfig() {
        Configuration defaultsOnly = new Configuration();
        assertThat(GlobalConfigUtils.getMetaObjectHandler(defaultsOnly)).isEmpty();
        assertThat(GlobalConfigUtils.getAnnotationHandler(defaultsOnly)).isPresent();

        Configuration installed = new Configuration();
        MetaObjectHandler metaObjectHandler = mock(MetaObjectHandler.class);
        GlobalConfigUtils.setGlobalConfig(
                installed,
                new GlobalConfig().setDbConfig(new GlobalConfig.DbConfig()).setMetaObjectHandler(metaObjectHandler));
        assertThat(GlobalConfigUtils.getMetaObjectHandler(installed)).containsSame(metaObjectHandler);
    }
}
