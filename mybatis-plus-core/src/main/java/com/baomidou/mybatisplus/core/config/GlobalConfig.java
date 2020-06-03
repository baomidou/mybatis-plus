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
import com.baomidou.mybatisplus.core.toolkit.StringPool;
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
     * SqlSessionFactory
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
         * 以枚举类方式创建，默认线程安全，可防止反序列化导致重新创建新的对象
         */
        private enum DbConfigHolder{
            INSTANCE;

            private DbConfig dbConfig;

            DbConfigHolder(){
                dbConfig=new DbConfig();
            }

            public DbConfig getInstance(){
                return dbConfig;
            }
        }

        public static DbConfig instance(){
            return DbConfigHolder.INSTANCE.getInstance();
        }

        /**
         * 主键类型
         */
        private IdType idType = IdType.ASSIGN_ID;
        /**
         * 表名前缀,(默认无)
         */
        private String tablePrefix = StringPool.EMPTY;
        /**
         * schema,(默认无)
         *
         * @since 3.1.1
         */
        private String schema = StringPool.EMPTY;
        /**
         * db字段 format,(默认无)
         * <li> 例: `%s` </li>
         * <p> 对主键无效 </p>
         *
         * @since 3.1.1
         */
        private String columnFormat = StringPool.EMPTY;
        /**
         * entity字段 format, (默认无)
         * 只有在 column as property 这种情况下生效
         * <li> 例: `%s` </li>
         * <p> 对主键无效 </p>
         *
         * @since 3.3.0
         */
        private String propertyFormat = StringPool.EMPTY;
        /**
         * 表名、是否使用下划线命名（默认 true:默认数据库表下划线命名）
         */
        private boolean tableUnderline = true;
        /**
         * 大写命名
         */
        private boolean capitalMode = false;
        /**
         * 表主键生成器
         */
        private IKeyGenerator keyGenerator = null;
        /**
         * 逻辑删除全局字段 (默认无 设置会自动扫描实体字段)
         */
        private String logicDeleteField = StringPool.EMPTY;
        /**
         * 逻辑删除全局值（默认 1、表示已删除）
         */
        private String logicDeleteValue = StringPool.ONE;
        /**
         * 逻辑未删除全局值（默认 0、表示未删除）
         */
        private String logicNotDeleteValue = StringPool.ZERO;
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
