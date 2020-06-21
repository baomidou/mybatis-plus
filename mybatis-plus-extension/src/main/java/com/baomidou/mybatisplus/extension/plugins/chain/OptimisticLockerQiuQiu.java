package com.baomidou.mybatisplus.extension.plugins.chain;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @author miemie
 * @since 2020-06-21
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class OptimisticLockerQiuQiu implements QiuQiu {

    private static final String PARAM_UPDATE_METHOD_NAME = "update";

    @Override
    public void update(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return;
        }
        if (parameter instanceof Map) {
            Map map = (Map) parameter;
            //updateById(et), update(et, wrapper);
            Object et = map.getOrDefault(Constants.ENTITY, null);
            if (et != null) {
                // entity
                String methodId = ms.getId();
                String methodName = methodId.substring(methodId.lastIndexOf(StringPool.DOT) + 1);
                TableInfo tableInfo = TableInfoHelper.getTableInfo(et.getClass());
                if (tableInfo == null || !tableInfo.isWithVersion()) {
                    return;
                }
                try {
                    TableFieldInfo fieldInfo = tableInfo.getVersionFieldInfo();
                    Field versionField = fieldInfo.getField();
                    // 旧的 version 值
                    Object originalVersionVal = versionField.get(et);
                    if (originalVersionVal == null) {
                        return;
                    }
                    String versionColumn = fieldInfo.getColumn();
                    // 新的 version 值
                    Object updatedVersionVal = this.getUpdatedVersionVal(fieldInfo.getPropertyType(), originalVersionVal);
                    if (PARAM_UPDATE_METHOD_NAME.equals(methodName)) {
                        AbstractWrapper<?, ?, ?> aw = (AbstractWrapper<?, ?, ?>) map.getOrDefault(Constants.WRAPPER, null);
                        if (aw == null) {
                            UpdateWrapper<?> uw = new UpdateWrapper<>();
                            uw.eq(versionColumn, originalVersionVal);
                            map.put(Constants.WRAPPER, uw);
                        } else {
                            aw.apply(versionColumn + " = {0}", originalVersionVal);
                        }
                    } else {
                        map.put(Constants.MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
                    }
                    versionField.set(et, updatedVersionVal);
                } catch (IllegalAccessException e) {
                    throw ExceptionUtils.mpe(e);
                }
            }
        }
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal ignore
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Class<?> clazz, Object originalVersionVal) {
        if (long.class.equals(clazz) || Long.class.equals(clazz)) {
            return ((long) originalVersionVal) + 1;
        } else if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
            return ((int) originalVersionVal) + 1;
        } else if (Date.class.equals(clazz)) {
            return new Date();
        } else if (Timestamp.class.equals(clazz)) {
            return new Timestamp(System.currentTimeMillis());
        } else if (LocalDateTime.class.equals(clazz)) {
            return LocalDateTime.now();
        }
        //not supported type, return original val.
        return originalVersionVal;
    }
}
