/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.handlers.AnnotationHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class GlobalConfig implements Serializable {
    /**
     * 是否开启 LOGO
     */
    private boolean banner = true;
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
     *
     * @deprecated 3.5.3.2
     */
    @Deprecated
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
     * 注解控制器
     */
    private AnnotationHandler annotationHandler = new AnnotationHandler(){};
    /**
     * 参与 TableInfo 的初始化
     */
    private PostInitTableInfoHandler postInitTableInfoHandler = new PostInitTableInfoHandler() {
    };
    /**
     * 主键生成器
     */
    private IdentifierGenerator identifierGenerator;
    /**
     * 数据库相关配置
     */
    private Sequence sequence = new Sequence();

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
         * db 表 format
         * <p>
         * 例: `%s`
         * <p>
         *
         * @since 3.5.3.2
         */
        private String tableFormat;
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
        private List<IKeyGenerator> keyGenerators;
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
         * @deprecated 3.4.4
         */
        @Deprecated
        private FieldStrategy selectStrategy;

        /**
         * 字段验证策略之 where
         * 替代selectStrategy，保持与{@link TableField#whereStrategy()}一致
         *
         * @since 3.4.4
         */
        private FieldStrategy whereStrategy = FieldStrategy.NOT_NULL;

        /**
         * 生成INSERT语句时忽略自增主键字段(默认不忽略,主键有值时写入主键值,无值自增).
         * <p>当设置为true时,执行生成SQL语句无论ID是否有值都会忽视 (此为3.4.3.1版本下策略,如果升级遇到问题可以考虑开启此配置来兼容升级)</p>
         *
         * @since 3.5.6
         */
        private boolean insertIgnoreAutoIncrementColumn = false;

        /**
         * 重写whereStrategy的get方法，适配低版本：
         * - 如果用户自定义了selectStrategy则用用户自定义的，
         * - 后续版本移除selectStrategy后，直接删除该方法即可。
         *
         * @return 字段作为查询条件时的验证策略
         * @since 3.4.4
         */
        public FieldStrategy getWhereStrategy() {
            return selectStrategy == null ? whereStrategy : selectStrategy;
        }
    }

    /**
     * 雪花ID配置
     * <p>
     * 1. 手动指定{@link #workerId} 和 {@link #datacenterId}
     * </p>
     * <p>
     * 2. 基于网卡信息和进程PID计算 {@link #workerId} 和 {@link #datacenterId}
     * </p>
     *
     * @since 3.5.7
     */
    @Getter
    @Setter
    public static class Sequence {

        /**
         * 工作机器 ID
         */
        private Long workerId;

        /**
         * 数据标识 ID 部分
         */
        private Long datacenterId;

        /**
         * 首选网络地址 (例如: 192.168.1,支持正则)
         */
        private List<String> preferredNetworks = new ArrayList<>();

        /**
         * 忽略网卡(例如:eth0,,支持正则)
         */
        private List<String> ignoredInterfaces = new ArrayList<>();

    }

}
