/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Mybatis 全局缓存
 *
 * @author Caratacus
 * @since 2016-12-06
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class GlobalConfig implements Serializable {

    /**
     * 是否开启 LOGO
     */
    private boolean banner = true;
    /**
     * 机器 ID 部分
     *
     * @see #setIdentifierGenerator(IdentifierGenerator)
     * @deprecated 3.3.0
     */
    @Deprecated
    private Long workerId;
    /**
     * 数据标识 ID 部分
     *
     * @see #setIdentifierGenerator(IdentifierGenerator)
     * @deprecated 3.3.0
     */
    @Deprecated
    private Long datacenterId;
    /**
     * 是否初始化 SqlRunner
     */
    private boolean enableSqlRunner = false;
    /**
     * 数据库相关配置
     */
    private DbConfig dbConfig;
    /**
     * SQL注入器
     */
    private ISqlInjector sqlInjector = new DefaultSqlInjector();
    /**
     * Mapper父类
     */
    private Class<?> superMapperClass = Mapper.class;
    /**
     * 仅用于缓存 SqlSessionFactory(外部勿进行set,set了也没用)
     */
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 缓存已注入CRUD的Mapper信息
     */
    private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<>();
    /**
     * 元对象字段填充控制器
     */
    private MetaObjectHandler metaObjectHandler;
    /**
     * 主键生成器
     */
    private IdentifierGenerator identifierGenerator;

    @Data
    public static class DbConfig {
        /**
         * 主键类型
         */
        private IdType idType = IdType.ASSIGN_ID;
        /**
         * 表名前缀
         */
        private String tablePrefix;
        /**
         * schema
         *
         * @since 3.1.1
         */
        private String schema;
        /**
         * db字段 format
         * <p>
         * 例: `%s`
         * <p>
         * 对主键无效
         *
         * @since 3.1.1
         */
        private String columnFormat;
        /**
         * entity 的字段(property)的 format,只有在 column as property 这种情况下生效
         * <p>
         * 例: `%s`
         * <p>
         * 对主键无效
         *
         * @since 3.3.0
         */
        private String propertyFormat;
        /**
         * 实验性功能,占位符替换,等同于 {@link com.baomidou.mybatisplus.extension.plugins.inner.ReplacePlaceholderInnerInterceptor},
         * 只是这个属于启动时替换,用得地方多会启动慢一点点,不适用于其他的 {@link org.apache.ibatis.scripting.LanguageDriver}
         *
         * @since 3.4.2
         */
        private boolean replacePlaceholder;
        /**
         * 转义符
         * <p>
         * 配合 {@link #replacePlaceholder} 使用时有效
         * <p>
         * 例: " 或 ' 或 `
         *
         * @since 3.4.2
         */
        private String escapeSymbol;
        /**
         * 表名是否使用驼峰转下划线命名,只对表名生效
         */
        private boolean tableUnderline = true;
        /**
         * 大写命名,对表名和字段名均生效
         */
        private boolean capitalMode = false;
        /**
         * 表主键生成器
         */
        private IKeyGenerator keyGenerator;
        /**
         * 逻辑删除全局属性名
         */
        private String logicDeleteField;
        /**
         * 逻辑删除全局值（默认 1、表示已删除）
         */
        private String logicDeleteValue = "1";
        /**
         * 逻辑未删除全局值（默认 0、表示未删除）
         */
        private String logicNotDeleteValue = "0";
        /**
         * 字段验证策略之 insert
         *
         * @since 3.1.2
         */
        private FieldStrategy insertStrategy = FieldStrategy.NOT_NULL;
        /**
         * 字段验证策略之 update
         *
         * @since 3.1.2
         */
        private FieldStrategy updateStrategy = FieldStrategy.NOT_NULL;
        /**
         * 字段验证策略之 select
         *
         * @since 3.1.2
         */
        private FieldStrategy selectStrategy = FieldStrategy.NOT_NULL;
    }
}
