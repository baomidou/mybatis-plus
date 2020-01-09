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
import org.apache.ibatis.reflection.MetaObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 元对象字段填充控制器抽象类，实现公共字段自动写入<p>
 * <p>
 * 所有入参的 MetaObject 必定是 entity 或其子类的 MetaObject
 *
 * @author hubin
 * @since 2016-08-28
 */
public interface MetaObjectHandler {

    /**
     * 是否开启了插入填充
     */
    default boolean openInsertFill() {
        return true;
    }

    /**
     * 兼容填充主键判断开关
     * 如果启用开关:当主键值为空且主键生成策略为NONE或INPUT会进入新增填充
     * 这开关主要是用来兼容旧版本的用户使用插入填充来进行主键填充的开关
     * 暂时不确定什么时候会移出此开关,请尽快使用新的Id生成策略来生成Id
     *
     * @return 是否启用
     * @since 3.3.0
     */
    default boolean compatibleFillId() {
        return false;
    }

    /**
     * 是否开启了更新填充
     */
    default boolean openUpdateFill() {
        return true;
    }

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
        if (Objects.nonNull(fieldVal) && metaObject.hasSetter(fieldName)) {
            metaObject.setValue(fieldName, fieldVal);
        }
        return this;
    }

    /**
     * get value from java bean by propertyName
     *
     * @param fieldName  java bean property name
     * @param metaObject parameter wrapper
     * @return 字段值
     */
    default Object getFieldValByName(String fieldName, MetaObject metaObject) {
        return metaObject.hasGetter(fieldName) ? metaObject.getValue(fieldName) : null;
    }

    /**
     * insert 时填充,只会填充 fill 被标识为 INSERT 与 INSERT_UPDATE 的字段
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @since 3.0.7
     * @deprecated 3.3.0 please use {@link #strictInsertFill}
     */
    @Deprecated
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
     * @deprecated 3.3.0 please use {@link #strictUpdateFill}
     */
    @Deprecated
    default MetaObjectHandler setUpdateFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.UPDATE);
    }

    /**
     * Common method to set value for java bean.
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @param fieldFill  填充策略枚举
     * @since 3.0.7
     * @deprecated 3.3.0 please use like {@link #strictInsertFill} or {@link #strictUpdateFill}
     */
    @Deprecated
    default MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        if (Objects.nonNull(fieldVal) && isFill(fieldName, fieldVal, metaObject, fieldFill)) {
            metaObject.setValue(fieldName, fieldVal);
        }
        return this;
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
     * @deprecated 3.3.0
     */
    @Deprecated
    default boolean isFill(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        Optional<TableFieldInfo> first = findTableInfo(metaObject).getFieldList().stream()
            //v_3.1.1+ 设置子类的值也可以通过
            .filter(e -> e.getProperty().equals(fieldName) && e.getPropertyType().isAssignableFrom(fieldVal.getClass()))
            .findFirst();
        if (first.isPresent()) {
            FieldFill fill = first.get().getFieldFill();
            return fill == fieldFill || FieldFill.INSERT_UPDATE == fill;
        }
        return false;
    }

    /**
     * find the tableInfo cache by metaObject </p>
     * 获取 TableInfo 缓存
     *
     * @param metaObject meta object parameter
     * @return TableInfo
     * @since 3.3.0
     */
    default TableInfo findTableInfo(MetaObject metaObject) {
        return TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
    }

    /**
     * @param metaObject metaObject meta object parameter
     * @since 3.3.0
     */
    default <T> MetaObjectHandler strictInsertFill(MetaObject metaObject, String fieldName, Class<T> fieldType, Object fieldVal) {
        return strictInsertFill(findTableInfo(metaObject), metaObject, Collections.singletonList(StrictFill.of(fieldName, fieldType, fieldVal)));
    }

    /**
     * @param metaObject metaObject meta object parameter
     * @since 3.3.0
     */
    default <T> MetaObjectHandler strictInsertFill(MetaObject metaObject, String fieldName, Class<T> fieldType, Supplier<T> fieldVal) {
        return strictInsertFill(findTableInfo(metaObject), metaObject, Collections.singletonList(StrictFill.of(fieldName, fieldType, fieldVal)));
    }

    /**
     * @param metaObject metaObject meta object parameter
     * @since 3.3.0
     */
    default MetaObjectHandler strictInsertFill(TableInfo tableInfo, MetaObject metaObject, List<StrictFill> strictFills) {
        return strictFill(true, tableInfo, metaObject, strictFills);
    }

    /**
     * @param metaObject metaObject meta object parameter
     * @since 3.3.0
     */
    default <T> MetaObjectHandler strictUpdateFill(MetaObject metaObject, String fieldName, Class<T> fieldType, Supplier<T> fieldVal) {
        return strictUpdateFill(findTableInfo(metaObject), metaObject, Collections.singletonList(StrictFill.of(fieldName, fieldType, fieldVal)));
    }

    /**
     * @param metaObject metaObject meta object parameter
     * @since 3.3.0
     */
    default <T> MetaObjectHandler strictUpdateFill(MetaObject metaObject, String fieldName, Class<T> fieldType, Object fieldVal) {
        return strictUpdateFill(findTableInfo(metaObject), metaObject, Collections.singletonList(StrictFill.of(fieldName, fieldType, fieldVal)));
    }

    /**
     * @param metaObject metaObject meta object parameter
     * @since 3.3.0
     */
    default MetaObjectHandler strictUpdateFill(TableInfo tableInfo, MetaObject metaObject, List<StrictFill> strictFills) {
        return strictFill(false, tableInfo, metaObject, strictFills);
    }

    /**
     * 严格填充,只针对非主键的字段,只有该表注解了fill 并且 字段名和字段属性 能匹配到才会进行填充(null 值不填充)
     *
     * @param insertFill  是否验证在 insert 时填充
     * @param tableInfo   cache 缓存
     * @param metaObject  metaObject meta object parameter
     * @param strictFills 填充信息
     * @return this
     * @since 3.3.0
     */
    default MetaObjectHandler strictFill(boolean insertFill, TableInfo tableInfo, MetaObject metaObject, List<StrictFill> strictFills) {
        if ((insertFill && tableInfo.isWithInsertFill()) || (!insertFill && tableInfo.isWithUpdateFill())) {
            strictFills.forEach(i -> {
                final String fieldName = i.getFieldName();
                tableInfo.getFieldList().stream()
                    .filter(j -> j.getProperty().equals(fieldName) && i.getFieldType().equals(j.getPropertyType()) &&
                        ((insertFill && j.isWithInsertFill()) || (!insertFill && j.isWithUpdateFill()))).findFirst()
                    .ifPresent(j -> strictFillStrategy(metaObject, fieldName, i.getFieldVal()));
            });
        }
        return this;
    }

    /**
     * 填充策略,默认有值不覆盖,如果提供的值为null也不填充
     *
     * @param metaObject metaObject meta object parameter
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value of Supplier
     * @since 3.3.0
     */
    default MetaObjectHandler fillStrategy(MetaObject metaObject, String fieldName, Object fieldVal) {
        if (getFieldValByName(fieldName, metaObject) == null) {
            setFieldValByName(fieldName, fieldVal, metaObject);
        }
        return this;
    }

    /**
     * 严格模式填充策略,默认有值不覆盖,如果提供的值为null也不填充
     *
     * @param metaObject metaObject meta object parameter
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value of Supplier
     * @since 3.3.0
     */
    default MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<Object> fieldVal) {
        if (metaObject.getValue(fieldName) == null) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        return this;
    }
}
