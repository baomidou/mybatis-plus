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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
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
     * 缓存 Sql 解析初始化
     *
     * @deprecated 3.1.1 不再需要这个属性, 现在是全局开启状态
     */
    @Deprecated
    private boolean sqlParserCache = false;
    /**
     * 机器 ID 部分
     */
    private Long workerId;
    /**
     * 数据标识 ID 部分
     */
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
    private ISqlInjector sqlInjector;
    /**
     * Mapper父类
     */
    private Class<?> superMapperClass = Mapper.class;
    /**
     * 缓存当前Configuration的SqlSessionFactory
     */
    @Setter(value = AccessLevel.NONE)
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
     * 是否开启多继承指定数据库字段基础超类
     * 默认不开启
     */
    private Boolean enableSuperClass = false;

    /**
     * 标记全局设置 (统一所有入口)
     */
    public void signGlobalConfig(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Data
    public static class DbConfig {

        /**
         * 数据库类型
         *
         * @deprecated 3.1.1 不再需要, mp不应该也不需要关心数据库类型
         */
        @Deprecated
        private DbType dbType = DbType.OTHER;
        /**
         * 主键类型（默认 ID_WORKER）
         */
        private IdType idType = IdType.ID_WORKER;
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
         * 字段 format
         * <li> 例: `%s` </li>
         * <p> 对主键无效 </p>
         *
         * @since 3.1.1
         */
        private String columnFormat;
        /**
         * 表名、是否使用下划线命名（默认 true:默认数据库表下划线命名）
         */
        private boolean tableUnderline = true;
        /**
         * String 类型字段 LIKE
         *
         * @deprecated 3.1.1 后续将删除这个属性
         */
        @Deprecated
        private boolean columnLike = false;
        /**
         * 大写命名
         */
        private boolean capitalMode = false;
        /**
         * 表主键生成器
         */
        private IKeyGenerator keyGenerator;
        /**
         * 逻辑删除全局值（默认 1、表示已删除）
         */
        private String logicDeleteValue = "1";
        /**
         * 逻辑未删除全局值（默认 0、表示未删除）
         */
        private String logicNotDeleteValue = "0";
        /**
         * 字段验证策略
         *
         * @deprecated 3.1.2 please use {@link #insertStrategy} and {@link #updateStrategy} and {@link #selectStrategy}
         */
        @Deprecated
        private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;
        /**
         * 字段验证策略之 insert
         * 目前没有默认值,等 {@link #fieldStrategy} 完全去除掉,会给个默认值 NOT_NULL
         * 没配则按 {@link #fieldStrategy} 为准
         *
         * @since 3.1.2
         */
        private FieldStrategy insertStrategy;
        /**
         * 字段验证策略之 update
         * 目前没有默认值,等 {@link #fieldStrategy} 完全去除掉,会给个默认值 NOT_NULL
         * 没配则按 {@link #fieldStrategy} 为准
         *
         * @since 3.1.2
         */
        private FieldStrategy updateStrategy;
        /**
         * 字段验证策略之 select
         * 目前没有默认值,等 {@link #fieldStrategy} 完全去除掉,会给个默认值 NOT_NULL
         * 没配则按 {@link #fieldStrategy} 为准
         *
         * @since 3.1.2
         */
        private FieldStrategy selectStrategy;
    }
}
