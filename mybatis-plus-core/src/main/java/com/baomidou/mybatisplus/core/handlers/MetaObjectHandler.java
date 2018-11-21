/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.core.handlers;

import java.util.Objects;
import java.util.Optional;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * 元对象字段填充控制器抽象类，实现公共字段自动写入
 * </p>
 *
 * @author hubin
 * @since 2016-08-28
 */
public interface MetaObjectHandler {

    /**
     * 乐观锁常量
     */
    String MP_OPTLOCK_ET_ORIGINAL = "MP_OPTLOCK_ET_ORIGINAL";

    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    void insertFill(MetaObject metaObject);

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    void updateFill(MetaObject metaObject);

    /**
     * <p>
     * Common method to set value for java bean.
     * </p>
     * <p>
     * 如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);
     * </p>
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @see MetaObjectHandler#setInsertFieldValByName(String, Object, MetaObject)  替代之前版本insert填充
     * @see MetaObjectHandler#setUpdateFieldValByName(String, Object, MetaObject)  替代之前版本update填充
     * @since 3.0.7
     */
    @Deprecated
    default MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        if (Objects.nonNull(fieldVal)) {
            if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName)) {
                metaObject.setValue(fieldName, fieldVal);
            } else if (metaObject.hasGetter(Constants.ENTITY)) {
                Object et = metaObject.getValue(Constants.ENTITY);
                if (et != null) {
                    MetaObject etMeta = SystemMetaObject.forObject(et);
                    if (etMeta.hasSetter(fieldName)) {
                        etMeta.setValue(fieldName, fieldVal);
                    }
                }
            }
        }
        return this;
    }

    /**
     * <p>
     * 替代之前版本insert填充
     * </p>
     *
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     * @return
     * @since 3.0.7
     */
    default MetaObjectHandler setInsertFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.INSERT);
    }

    /**
     * <p>
     * 替代之前版本update填充
     * </p>
     *
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     * @return
     * @since 3.0.7
     */
    default MetaObjectHandler setUpdateFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.UPDATE);
    }

    /**
     * <p>
     * Common method to set value for java bean.
     * </p>
     * <p>
     * 如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);
     * </p>
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @param fieldFill  填充策略枚举
     * @since 3.0.7
     */
    default MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        if (Objects.nonNull(fieldVal)) {
            if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName) && isFill(fieldName, metaObject, fieldFill)) {
                metaObject.setValue(fieldName, fieldVal);
            } else if (metaObject.hasGetter(Constants.ENTITY)) {
                Object et = metaObject.getValue(Constants.ENTITY);
                if (et != null) {
                    MetaObject etMeta = SystemMetaObject.forObject(et);
                    if (etMeta.hasSetter(fieldName) && isFill(fieldName, etMeta, fieldFill)) {
                        etMeta.setValue(fieldName, fieldVal);
                    }
                }
            }
        }
        return this;
    }

    /**
     * <p>
     * get value from java bean by propertyName
     * </p>
     * <p>
     * 如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);
     * </p>
     *
     * @param fieldName  java bean property name
     * @param metaObject parameter wrapper
     * @return 字段值
     */
    default Object getFieldValByName(String fieldName, MetaObject metaObject) {
        if (metaObject.hasGetter(fieldName)) {
            return metaObject.getValue(fieldName);
        } else if (metaObject.hasGetter(Constants.ENTITY_DOT + fieldName)) {
            return metaObject.getValue(Constants.ENTITY_DOT + fieldName);
        }
        return null;
    }

    /**
     * 是否填充
     *
     * @param fieldName  字段名
     * @param metaObject
     * @param fieldFill  填充策略
     * @return
     * @since 3.0.7
     */
    default boolean isFill(String fieldName, MetaObject metaObject, FieldFill fieldFill) {
        TableInfo tableInfo = metaObject.hasGetter(MP_OPTLOCK_ET_ORIGINAL) ? TableInfoHelper.getTableInfo(metaObject.getValue(MP_OPTLOCK_ET_ORIGINAL).getClass()) : TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
        if (Objects.nonNull(tableInfo)) {
            Optional<TableFieldInfo> first = tableInfo.getFieldList().stream().filter(e -> e.getProperty().equals(fieldName)).findFirst();
            if (first.isPresent()) {
                FieldFill fill = first.get().getFieldFill();
                return fill.equals(fieldFill) || FieldFill.INSERT_UPDATE.equals(fill);
            }
        }
        return false;
    }

    /**
     * 是否开启了插入填充
     */
    default boolean openInsertFill() {
        return true;
    }

    /**
     * 是否开启了更新填充
     */
    default boolean openUpdateFill() {
        return true;
    }
}
