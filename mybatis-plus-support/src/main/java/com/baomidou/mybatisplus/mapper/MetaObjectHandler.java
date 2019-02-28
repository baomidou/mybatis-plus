/**
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
package com.baomidou.mybatisplus.mapper;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * 元对象字段填充控制器抽象类，实现公共字段自动写入
 * </p>
 *
 * @author hubin
 * @Date 2016-08-28
 */
public abstract class MetaObjectHandler {

    protected static final String META_OBJ_PREFIX = "et";
    /**
     * 乐观锁常量
     */
    String MP_OPTLOCK_ET_ORIGINAL = "MP_OPTLOCK_ET_ORIGINAL";

    /**
     * <p>
     * 插入元对象字段填充
     * </p>
     *
     * @param metaObject 元对象
     */
    public abstract void insertFill(MetaObject metaObject);

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     * Created with IntelliJ IDEA.
     * Author:  Wu Yujie
     * Email:  coffee377@dingtalk.com
     * Time:  2017/04/16 15:03
     *
     * @param metaObject 元对象
     */
    public abstract void updateFill(MetaObject metaObject);

    /**
     * <p>
     * Common method to set value for java bean.
     * </p>
     * <p>
     * 如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);
     * </p>
     *
     * 从2.3.4版本开始，调用了
     * {@link #setInsertFieldValByName(String, Object, MetaObject)} / {@link #setUpdateFieldValByName(String, Object, MetaObject)}
     * 这2个方法时需注意：只有注解了fill = FieldFill.INSERT/UPDATE/INSERT_UPDATE的字段，才会自动填充。
     * 当前这个方法不推荐使用，因为不论是否有fill,都会被自动填充
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @deprecated since 2.3.4, refer to {@link #setInsertFieldValByName(String, Object, MetaObject)} / {@link #setUpdateFieldValByName(String, Object, MetaObject)}
     */
    @Deprecated
    public MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        if (fieldVal != null) {
            if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName)) {
                metaObject.setValue(fieldName, fieldVal);
            } else if (metaObject.hasGetter(META_OBJ_PREFIX)) {
                Object et = metaObject.getValue(META_OBJ_PREFIX);
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
     * Recommend to invoke {@code setInsertFieldValByName()} when auto fill for INSERT.
     * 在INSERT时的自动填充，推荐使用{@code setInsertFieldValByName()}
     *
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     * @return
     * @since 2.3.4
     */
    public MetaObjectHandler setInsertFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.INSERT);
    }

    /**
     * Recommend to invoke {@code setUpdateFieldValByName()} when auto fill for UPDATE.
     * 在UPDATE时的自动填充，推荐使用{@code setUpdateFieldValByName()}
     *
     * @param fieldName
     * @param fieldVal
     * @param metaObject
     * @return
     * @since 2.3.4
     */
    public MetaObjectHandler setUpdateFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return setFieldValByName(fieldName, fieldVal, metaObject, FieldFill.UPDATE);
    }

    public MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        if (fieldVal != null) {
            if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName)
                && isFill(fieldName, fieldVal, metaObject, fieldFill)) {
                metaObject.setValue(fieldName, fieldVal);
            } else if (metaObject.hasGetter(META_OBJ_PREFIX)) {
                Object et = metaObject.getValue(META_OBJ_PREFIX);
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
     * <p>
     * get value from java bean by propertyName
     * </p>
     * <p>
     * 如果包含前缀 et 使用该方法，否则可以直接 metaObject.setValue(fieldName, fieldVal);
     * </p>
     *
     * @param fieldName  java bean property name
     * @param metaObject parameter wrapper
     * @return
     */
    public Object getFieldValByName(String fieldName, MetaObject metaObject) {
        if (metaObject.hasGetter(fieldName)) {
            return metaObject.getValue(fieldName);
        } else if (metaObject.hasGetter(META_OBJ_PREFIX + "." + fieldName)) {
            return metaObject.getValue(META_OBJ_PREFIX + "." + fieldName);
        }
        return null;
    }

    /**
     * 填充判断
     * <li> 如果是主键,不填充 </li>
     * <li> 根据字段名找不到字段,不填充 </li>
     * <li> 字段类型与填充值类型不匹配,不填充 </li>
     * <li> 如果该字段没有注解配置FILL, 不填充 </li>
     *
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value
     * @param metaObject meta object parameter
     * @param fieldFill  填充策略枚举
     * @return 是否进行填充
     * @since 2.3.4
     */
    protected boolean isFill(String fieldName, Object fieldVal, MetaObject metaObject, FieldFill fieldFill) {
        TableInfo tableInfo = metaObject.hasGetter(MP_OPTLOCK_ET_ORIGINAL) ?
            TableInfoHelper.getTableInfo(metaObject.getValue(MP_OPTLOCK_ET_ORIGINAL).getClass())
            : TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
        if (tableInfo != null) {
            for (TableFieldInfo e : tableInfo.getFieldList()) {
                if (e.getProperty().equals(fieldName)
                    && e.getPropertyType().isAssignableFrom(fieldVal.getClass())) {//设置子类的值也可以通过
                    FieldFill fill = e.getFieldFill();
                    return fill.equals(fieldFill) || FieldFill.INSERT_UPDATE.equals(fill);
                }
            }
        }
        return false;
    }

    /**
     * 开启插入填充
     */
    public boolean openInsertFill() {
        return true;
    }

    /**
     * 开启更新填充
     */
    public boolean openUpdateFill() {
        return true;
    }

}
