package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;

/**
 * 初始化 TableInfo 同时进行一些操作
 *
 * @author miemie
 * @since 2022-09-20
 */
public interface JoinTableInfoInitHandler {

    /**
     * 参与 TableFieldInfo 初始化
     *
     * @param fieldInfo     TableFieldInfo
     * @param configuration Configuration
     */
    default void joinTableFieldInfo(TableFieldInfo fieldInfo, Configuration configuration) {
        // ignore
    }

    /**
     * 参与 TableInfo 初始化
     *
     * @param tableInfo     TableInfo
     * @param configuration Configuration
     */
    default void joinTableInfo(TableInfo tableInfo, Configuration configuration) {
        // ignore
    }
}
