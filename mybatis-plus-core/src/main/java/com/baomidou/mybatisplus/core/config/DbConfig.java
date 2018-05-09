package com.baomidou.mybatisplus.core.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.IDBType;
import com.baomidou.mybatisplus.core.handlers.SqlReservedWordsHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;

/**
 * 数据库相关配置
 */
public class DbConfig {

    /**
     * 数据库类型
     */
    private IDBType dbType;
    /**
     * 主键类型（默认 ID_WORKER）
     */
    private IdType idType = IdType.ID_WORKER;
    /**
     * 表名前缀
     */
    private String tablePrefix;
    /**
     * 表名、是否使用下划线命名（默认 true:默认数据库表下划线命名）
     */
    private boolean tableUnderline = true;
    /**
     * 字段名、是否使用下划线命名（默认 true:默认数据库字段下划线命名）
     */
    private boolean columnUnderline = true;
    /**
     * 大写命名
     */
    private boolean capitalMode = false;
    /**
     * 表关键词 key 生成器
     */
    private IKeyGenerator keyGenerator;
    /**
     * 逻辑删除全局值（默认 0、否）
     */
    private String logicDeleteValue = "0";
    /**
     * 逻辑未删除全局值（默认 1、是）
     */
    private String logicNotDeleteValue = "1";
    /**
     * 字段验证策略
     */
    private FieldStrategy fieldStrategy = FieldStrategy.NOT_NULL;
    /**
     * Sql 保留字处理器
     */
    private SqlReservedWordsHandler reservedWordsHandler = SqlReservedWordsHandler.getInstance();


    public IDBType getDbType() {
        return dbType;
    }

    public void setDbType(IDBType dbType) {
        this.dbType = dbType;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = IdType.getIdType(idType);
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public boolean isTableUnderline() {
        return tableUnderline;
    }

    public void setTableUnderline(boolean tableUnderline) {
        this.tableUnderline = tableUnderline;
    }

    public boolean isColumnUnderline() {
        return columnUnderline;
    }

    public void setColumnUnderline(boolean columnUnderline) {
        this.columnUnderline = columnUnderline;
    }

    public boolean isCapitalMode() {
        return capitalMode;
    }

    public void setCapitalMode(boolean capitalMode) {
        this.capitalMode = capitalMode;
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

    public FieldStrategy getFieldStrategy() {
        return fieldStrategy;
    }

    public void setFieldStrategy(int fieldStrategy) {
        this.fieldStrategy = FieldStrategy.getFieldStrategy(fieldStrategy);
    }

    public SqlReservedWordsHandler getReservedWordsHandler() {
        return reservedWordsHandler;
    }

    public void setReservedWordsHandler(SqlReservedWordsHandler reservedWordsHandler) {
        this.reservedWordsHandler = reservedWordsHandler;
    }
}
