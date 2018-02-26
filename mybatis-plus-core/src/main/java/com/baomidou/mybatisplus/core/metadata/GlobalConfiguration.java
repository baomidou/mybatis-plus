/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.metadata;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.IDBType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.SqlReservedWordsHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>
 * Mybatis全局缓存
 * </p>
 *
 * @author Caratacus
 * @since 2016-12-06
 */
public class GlobalConfiguration implements Serializable {

    /**
     * 逻辑删除全局值
     */
    private String logicDeleteValue = null;
    /**
     * 逻辑未删除全局值
     */
    private String logicNotDeleteValue = null;
    /**
     * 数据库类型
     */
    private IDBType dbType;
    /**
     * 主键类型（默认 ID_WORKER）
     */
    private IdType idType = IdType.ID_WORKER;
    /**
     * 表名、字段名、是否使用下划线命名（默认 false）
     */
    private boolean dbColumnUnderline = false;
    /**
     * SQL注入器
     */
    private ISqlInjector sqlInjector;
    /**
     * 表关键词 key 生成器
     */
    private IKeyGenerator keyGenerator;
    /**
     * 元对象字段填充控制器
     */
    private MetaObjectHandler metaObjectHandler = MetaObjectHandler.getInstance();
    /**
     * //TODO: 3.0
     */
    private SqlReservedWordsHandler reservedWordsHandler;
    /**
     * 字段验证策略
     */
    private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;
    /**
     * 是否刷新mapper
     */
    private boolean isRefresh = false;
    /**
     * 是否大写命名
     */
    private boolean isCapitalMode = false;
    /**
     * 标识符
     */
    private String identifierQuote;
    /**
     * 缓存当前Configuration的SqlSessionFactory
     */
    private SqlSessionFactory sqlSessionFactory;
    /**
     * 缓存已注入CRUD的Mapper信息
     */
    private Set<String> mapperRegistryCache = new ConcurrentSkipListSet<>();
    /**
     * 单例重用SqlSession
     */
    private SqlSession sqlSession;
    /**
     * 缓存 Sql 解析初始化
     */
    private boolean sqlParserCache = false;


    public GlobalConfiguration() {
        // 构造方法
    }


    public GlobalConfiguration(ISqlInjector sqlInjector) {
        this.sqlInjector = sqlInjector;
    }


    public IKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(IKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    public String getLogicDeleteValue() {
        return logicDeleteValue;
    }

    public void setLogicDeleteValue(String logicDeleteValue) {
        this.logicDeleteValue = logicDeleteValue;
    }

    public String getLogicNotDeleteValue() {
        return logicNotDeleteValue;
    }

    public void setLogicNotDeleteValue(String logicNotDeleteValue) {
        this.logicNotDeleteValue = logicNotDeleteValue;
    }

    public IDBType getDbType() {
        return dbType;
    }

//    /**
//     * 根据jdbcUrl设置数据库类型
//     *
//     * @param jdbcUrl
//     */
    //TODO: 3.0 通过其他途径设置dbType
//    public void setDbTypeOfJdbcUrl(String jdbcUrl) {
//        this.dbType = JdbcUtils.getDbType(jdbcUrl);
//    }

    //TODO 3.0 通过其他途径设置dbType
    public void setDbType(IDBType dbType) {
        this.dbType = dbType;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = IdType.getIdType(idType);
    }

    public boolean isDbColumnUnderline() {
        return dbColumnUnderline;
    }

    public void setDbColumnUnderline(boolean dbColumnUnderline) {
        this.dbColumnUnderline = dbColumnUnderline;
    }

    public ISqlInjector getSqlInjector() {
        return sqlInjector;
    }

    public void setSqlInjector(ISqlInjector sqlInjector) {
        this.sqlInjector = sqlInjector;
    }

    public MetaObjectHandler getMetaObjectHandler() {
        return metaObjectHandler;
    }

    public void setMetaObjectHandler(MetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }

    public SqlReservedWordsHandler getReservedWordsHandler() {
        return reservedWordsHandler;
    }

    public GlobalConfiguration setReservedWordsHandler(SqlReservedWordsHandler reservedWordsHandler) {
        this.reservedWordsHandler = reservedWordsHandler;
        return this;
    }

    public FieldStrategy getFieldStrategy() {
        return fieldStrategy;
    }

    public void setFieldStrategy(int fieldStrategy) {
        this.fieldStrategy = FieldStrategy.getFieldStrategy(fieldStrategy);
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        this.isRefresh = refresh;
    }

    public Set<String> getMapperRegistryCache() {
        return mapperRegistryCache;
    }

    public void setMapperRegistryCache(Set<String> mapperRegistryCache) {
        this.mapperRegistryCache = mapperRegistryCache;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
//        //TODO: 3.0 在extension中初始化这个对象
//        this.sqlSession = new MybatisSqlSessionTemplate(sqlSessionFactory);
    }

    public GlobalConfiguration setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
        return this;
    }

    public boolean isCapitalMode() {
        return isCapitalMode;
    }

    public void setCapitalMode(boolean isCapitalMode) {
        this.isCapitalMode = isCapitalMode;
    }

    public String getIdentifierQuote() {
        if (null == identifierQuote) {
            return dbType.getQuote();
        }
        return identifierQuote;
    }

    public void setIdentifierQuote(String identifierQuote) {
        this.identifierQuote = identifierQuote;
    }

    public void setSqlKeywords(String sqlKeywords) {
        if (StringUtils.isNotEmpty(sqlKeywords)) {
            if (reservedWordsHandler == null) {
                reservedWordsHandler = SqlReservedWordsHandler.getInstance();
            }
            reservedWordsHandler.addAll(StringUtils.splitWorker(sqlKeywords.toUpperCase(), "," , -1, false));
        }
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public boolean isSqlParserCache() {
        return sqlParserCache;
    }

    public void setSqlParserCache(boolean sqlParserCache) {
        this.sqlParserCache = sqlParserCache;
    }

    /**
     * <p>
     * 标记全局设置 (统一所有入口)
     * </p>
     *
     * @param sqlSessionFactory
     * @return
     */
    public SqlSessionFactory signGlobalConfig(SqlSessionFactory sqlSessionFactory) {
        if (null != sqlSessionFactory) {
            GlobalConfigUtils.setGlobalConfig(sqlSessionFactory.getConfiguration(), this);
        }
        return sqlSessionFactory;
    }
}
