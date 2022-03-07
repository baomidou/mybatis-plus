package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author miemie
 * @since 2022-03-07
 */
public class CompositeEnumTypeHandler<E extends Enum<E>> implements TypeHandler<E> {

    private static final Map<Class<?>, Boolean> MP_ENUM_CACHE = new ConcurrentHashMap<>();
    @Setter
    private static Class<? extends TypeHandler> defaultEnumTypeHandler = EnumTypeHandler.class;
    private final TypeHandler<E> delegate;

    public CompositeEnumTypeHandler(Class<E> enumClassType) {
        if (enumClassType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        if (CollectionUtils.computeIfAbsent(MP_ENUM_CACHE, enumClassType, MybatisEnumTypeHandler::isMpEnums)) {
            delegate = new MybatisEnumTypeHandler<>(enumClassType);
        } else {
            delegate = getInstance(enumClassType, defaultEnumTypeHandler);
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        delegate.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getResult(ResultSet rs, String columnName) throws SQLException {
        return delegate.getResult(rs, columnName);
    }

    @Override
    public E getResult(ResultSet rs, int columnIndex) throws SQLException {
        return delegate.getResult(rs, columnIndex);
    }

    @Override
    public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delegate.getResult(cs, columnIndex);
    }

    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }
}
