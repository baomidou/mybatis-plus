package com.baomidou.mybatisplus.extension.plugins;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

/**
 * <p>
 * Optimistic Lock Light version<BR>
 * Intercept on {@link Executor}.update;<BR>
 * Support version types: int/Integer, long/Long, java.util.Date, java.sql.Timestamp<BR>
 * For extra types, please define a subclass and override {@code getUpdatedVersionVal}() method.<BR>
 * <BR>
 * How to use?<BR>
 * (1) Define an Entity and add {@link Version} annotation on one entity field.<BR>
 * (2) Add {@link OptimisticLockerInterceptor} into mybatis plugin.
 * <p>
 * How to work?<BR>
 * if update entity with version column=1:<BR>
 * (1) no {@link OptimisticLockerInterceptor}:<BR>
 * SQL: update tbl_test set name='abc' where id=100001;<BR>
 * (2) add {@link OptimisticLockerInterceptor}:<BR>
 * SQL: update tbl_test set name='abc',version=2 where id=100001 and version=1;
 * </p>
 *
 * @author yuxiaobin
 * @since 2017/5/24
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class OptimisticLockerInterceptor implements Interceptor {

    private final Map<Class<?>, EntityField> versionFieldCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<EntityField>> entityFieldsCache = new ConcurrentHashMap<>();

    public static final String MP_OPTLOCK_VERSION_ORIGINAL = "MP_OPTLOCK_VERSION_ORIGINAL";
    public static final String MP_OPTLOCK_VERSION_COLUMN = "MP_OPTLOCK_VERSION_COLUMN";
    public static final String MP_OPTLOCK_ET_ORIGINAL = "MP_OPTLOCK_ET_ORIGINAL";
    private static final String NAME_ENTITY = Constants.META_OBJ_PREFIX;
    private static final String NAME_ENTITY_WRAPPER = "ew";
    private static final String PARAM_UPDATE_METHOD_NAME = "update";

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        Object param = args[1];
        // JavaBean class
        Class<?> entityClass = null;
        // optimistic, Version Field
        Field versionField = null;
        // optimistic field annotated by Version
        String versionColumnName = null;
        // new version value, Long, Integer ...
        Object updatedVersionVal = null;

        // wrapper = ew
        Wrapper ew = null;
        // entity = et
        Object et = null;
        if (param instanceof Map) {
            Map map = (Map) param;
            if (map.containsKey(NAME_ENTITY_WRAPPER)) {
                // mapper.update(updEntity, QueryWrapper<>(whereEntity);
                ew = (Wrapper) map.get(NAME_ENTITY_WRAPPER);
            }
            //else updateById(entity) -->> change updateById(entity) to updateById(@Param("et") entity)

            // TODO 待验证逻辑
            // if mannual sql or updagteById(entity),unsupport OCC,proceed as usual unless use updateById(@Param("et") entity)
            //if(!map.containsKey(NAME_ENTITY)) {
            //    return invocation.proceed();
            //}
            if (map.containsKey(NAME_ENTITY)) {
                et = map.get(NAME_ENTITY);
            }
            if (ew != null) { // entityWrapper. baseMapper.update(et,ew);
                Object entity = ew.getEntity();
                if (entity != null) {
                    entityClass = ClassUtils.getUserClass(entity.getClass());
                    EntityField ef = getVersionField(entityClass);
                    versionField = ef == null ? null : ef.getField();
                    if (versionField != null) {
                        Object originalVersionVal = versionField.get(entity);
                        if (originalVersionVal != null) {
                            updatedVersionVal = getUpdatedVersionVal(originalVersionVal);
                            versionField.set(et, updatedVersionVal);
                        }
                    }
                }
            } else if (et != null) { // entity
                String methodId = ms.getId();
                String updateMethodName = methodId.substring(ms.getId().lastIndexOf(".") + 1);
                if (PARAM_UPDATE_METHOD_NAME.equals(updateMethodName)) {
                    // update(entityClass, null) -->> update all. ignore version
                    return invocation.proceed();
                }
                //invoke: baseMapper.updateById()
                entityClass = ClassUtils.getUserClass(et.getClass());
                EntityField entityField = this.getVersionField(entityClass);
                versionField = entityField == null ? null : entityField.getField();
                Object originalVersionVal;
                if (versionField != null && (originalVersionVal = versionField.get(et)) != null) {
                    TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                    // fixed github 299
                    while (null == tableInfo && null != entityClass) {
                        entityClass = ClassUtils.getUserClass(entityClass.getSuperclass());
                        tableInfo = TableInfoHelper.getTableInfo(entityClass);
                    }
                    Map<String, Object> entityMap = new HashMap<>();
                    List<EntityField> fields = getEntityFields(entityClass);
                    for (EntityField ef : fields) {
                        Field fd = ef.getField();
                        if (fd.isAccessible()) {
                            entityMap.put(fd.getName(), fd.get(et));
                            if (ef.isVersion()) {
                                versionField = fd;
                            }
                        }
                    }
                    String versionPropertyName = versionField.getName();
                    List<TableFieldInfo> fieldList = tableInfo.getFieldList();
                    versionColumnName = entityField.getColumnName();
                    if (versionColumnName == null) {
                        for (TableFieldInfo tf : fieldList) {
                            if (versionPropertyName.equals(tf.getProperty())) {
                                versionColumnName = tf.getColumn();
                            }
                        }
                    }
                    if (versionColumnName != null) {
                        entityField.setColumnName(versionColumnName);
                        updatedVersionVal = getUpdatedVersionVal(originalVersionVal);
                        entityMap.put(versionField.getName(), updatedVersionVal);
                        entityMap.put(MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
                        entityMap.put(MP_OPTLOCK_VERSION_COLUMN, versionColumnName);
                        entityMap.put(MP_OPTLOCK_ET_ORIGINAL, et);
                        map.put(NAME_ENTITY, entityMap);
                    }
                }
            }
        }

        Object resultObj = invocation.proceed();
        if (resultObj instanceof Integer) {
            Integer effRow = (Integer) resultObj;
            if (effRow != 0 && et != null && versionField != null && updatedVersionVal != null) {
                //updated version value set to entity.
                versionField.set(et, updatedVersionVal);
            }
        }
        return resultObj;
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Object originalVersionVal) {
        Class<?> versionValClass = originalVersionVal.getClass();
        if (long.class.equals(versionValClass)) {
            return ((long) originalVersionVal) + 1;
        } else if (Long.class.equals(versionValClass)) {
            return ((Long) originalVersionVal) + 1;
        } else if (int.class.equals(versionValClass)) {
            return ((int) originalVersionVal) + 1;
        } else if (Integer.class.equals(versionValClass)) {
            return ((Integer) originalVersionVal) + 1;
        } else if (Date.class.equals(versionValClass)) {
            return new Date();
        } else if (Timestamp.class.equals(versionValClass)) {
            return new Timestamp(System.currentTimeMillis());
        }
        return originalVersionVal;//not supported type, return original val.
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }

    private EntityField getVersionField(Class<?> parameterClass) {
        synchronized (parameterClass.getName()) {
            if (versionFieldCache.containsKey(parameterClass)) {
                return versionFieldCache.get(parameterClass);
            }
            // 缓存类信息
            EntityField field = this.getVersionFieldRegular(parameterClass);
            if (field != null) {
                versionFieldCache.put(parameterClass, field);
                return field;
            }
            return null;
        }
    }

    /**
     * <p>
     * 反射检查参数类是否启动乐观锁
     * </p>
     *
     * @param parameterClass 参数类
     * @return
     */
    private EntityField getVersionFieldRegular(Class<?> parameterClass) {
        if (parameterClass != Object.class) {
            for (Field field : parameterClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Version.class)) {
                    field.setAccessible(true);
                    return new EntityField(field, true);
                }
            }
            // 递归父类
            return this.getVersionFieldRegular(parameterClass.getSuperclass());
        }
        return null;
    }

    private List<EntityField> getEntityFields(Class<?> parameterClass) {
        if (entityFieldsCache.containsKey(parameterClass)) {
            return entityFieldsCache.get(parameterClass);
        }
        List<EntityField> fields = this.getFieldsFromClazz(parameterClass, null);
        entityFieldsCache.put(parameterClass, fields);
        return fields;
    }

    private List<EntityField> getFieldsFromClazz(Class<?> parameterClass, List<EntityField> fieldList) {
        if (fieldList == null) {
            fieldList = new ArrayList<>();
        }
        List<Field> fields = ReflectionKit.getFieldList(parameterClass);
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Version.class)) {
                fieldList.add(new EntityField(field, true));
            } else {
                fieldList.add(new EntityField(field, false));
            }
        }
        return fieldList;
    }
}

class EntityField {

    private Field field;
    private boolean version;
    private String columnName;

    public EntityField(Field field, boolean version) {
        this.field = field;
        this.version = version;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}

