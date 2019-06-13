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
package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.Objects;
import java.util.Optional;

/**
 * 元对象字段填充控制器抽象类，实现公共字段自动写入
 *
 * @author hubin
 * @since 2016-08-28
 */
public interface MetaObjectHandler {

    /**
     * 乐观锁常量
     *
     * @deprecated 3.1.1 {@link Constants#MP_OPTLOCK_ET_ORIGINAL}
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
     * 通用填充
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     */
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
     * insert 时填充,只会填充 fill 被标识为 INSERT 与 INSERT_UPDATE 的字段
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @since 3.0.7
     */
    default MetaObjectHandler setInsertFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.INSERT);
    }

    /**
     * update 时填充,只会填充 fill 被标识为 UPDATE 与 INSERT_UPDATE 的字段
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @since 3.0.7
     */
    default MetaObjectHandler setUpdateFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.UPDATE);
    }

    /**
     * Common method to set value for java bean.
     * <p>如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);</p>
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @param fieldFill  填充策略枚举
     * @since 3.0.7
     */
    default MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        if (Objects.nonNull(fieldVal)) {
            if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName)
                && isFill(fieldName, fieldVal, metaObject, fieldFill)) {
                metaObject.setValue(fieldName, fieldVal);
            } else if (metaObject.hasGetter(Constants.ENTITY)) {
                Object et = metaObject.getValue(Constants.ENTITY);
                if (et != null) {
                    MetaObject etMeta = SystemMetaObject.forObject(et);
                    if (etMeta.hasSetter(fieldName) && isFill(fieldName, fieldVal, etMeta, fieldFill)) {
                        etMeta.setValue(fieldName, fieldVal);
                    }
                }
            }
        }
        return this;
    }

    /**
     * get value from java bean by propertyName
     * <p>如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);</p>
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
     * 填充判断
     * <li> 如果是主键,不填充 </li>
     * <li> 根据字段名找不到字段,不填充 </li>
     * <li> 字段类型与填充值类型不匹配,不填充 </li>
     * <li> 字段类型需在TableField注解里配置fill: @TableField(value="test_type", fill = FieldFill.INSERT), 没有配置或者不匹配时不填充 </li>
     * v_3.1.0以后的版本(不包括3.1.0)，子类的值也可以自动填充，Timestamp的值也可以填入到java.util.Date类型里面
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @param fieldFill  填充策略枚举
     * @return 是否进行填充
     * @since 3.0.7
     */
    default boolean isFill(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        TableInfo tableInfo = metaObject.hasGetter(Constants.MP_OPTLOCK_ET_ORIGINAL) ?
            TableInfoHelper.getTableInfo(metaObject.getValue(Constants.MP_OPTLOCK_ET_ORIGINAL).getClass())
            : TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
        if (Objects.nonNull(tableInfo)) {
            Optional<TableFieldInfo> first = tableInfo.getFieldList().stream()
                //v_3.1.1+ 设置子类的值也可以通过
                .filter(e -> e.getProperty().equals(fieldName) && e.getPropertyType().isAssignableFrom(fieldVal.getClass()))
                .findFirst();
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
