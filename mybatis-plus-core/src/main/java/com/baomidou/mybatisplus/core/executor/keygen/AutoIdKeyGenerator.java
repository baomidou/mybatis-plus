/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.executor.keygen;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Key generator for {@code IdType.AUTO} that does not overwrite manually assigned ids.
 */
public class AutoIdKeyGenerator extends Jdbc3KeyGenerator {

    public static final AutoIdKeyGenerator INSTANCE = new AutoIdKeyGenerator();

    private static final String MSG_TOO_MANY_KEYS = "Too many keys are generated. There are only %d target objects. "
        + "You either specified a wrong 'keyProperty' or encountered a driver bug like #1523.";

    private final Map<Statement, ResultSet> generatedKeys = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // do nothing
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        processBatch(ms, stmt, parameter, executor instanceof BatchExecutor);
    }

    @Override
    public void processBatch(MappedStatement ms, Statement stmt, Object parameter) {
        processBatch(ms, stmt, parameter, true);
    }

    private void processBatch(MappedStatement ms, Statement stmt, Object parameter, boolean batch) {
        String[] keyProperties = ms.getKeyProperties();
        if (keyProperties == null || keyProperties.length == 0) {
            return;
        }
        try {
            ResultSet rs = getGeneratedKeys(stmt, batch);
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rsmd.getColumnCount() < keyProperties.length) {
                return;
            }
            Collection<?> parameters = collectionize(parameter);
            for (Object parameterObject : parameters) {
                if (!rs.next()) {
                    return;
                }
                assignKeys(ms.getConfiguration(), rs, rsmd, keyProperties, parameterObject);
            }
            if (!isSingleParameter(parameter) && rs.next()) {
                throw new ExecutorException(String.format(MSG_TOO_MANY_KEYS, parameters.size()));
            }
        } catch (Exception e) {
            throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
        }
    }

    private ResultSet getGeneratedKeys(Statement stmt, boolean batch) throws SQLException {
        if (!batch) {
            return stmt.getGeneratedKeys();
        }
        synchronized (generatedKeys) {
            ResultSet rs = generatedKeys.get(stmt);
            if (rs == null) {
                rs = stmt.getGeneratedKeys();
                generatedKeys.put(stmt, rs);
            }
            return rs;
        }
    }

    private void assignKeys(Configuration configuration, ResultSet rs, ResultSetMetaData rsmd, String[] keyProperties, Object parameter) throws SQLException {
        for (int i = 0; i < keyProperties.length; i++) {
            KeyTarget keyTarget = resolveKeyTarget(parameter, keyProperties[i]);
            if (keyTarget == null || keyTarget.target == null) {
                continue;
            }
            assignKey(configuration, rs, rsmd, i + 1, keyTarget);
        }
    }

    private void assignKey(Configuration configuration, ResultSet rs, ResultSetMetaData rsmd, int columnPosition, KeyTarget keyTarget) throws SQLException {
        MetaObject metaObject = configuration.newMetaObject(keyTarget.target);
        if (!metaObject.hasSetter(keyTarget.propertyName)) {
            throw new ExecutorException("No setter found for the keyProperty '" + keyTarget.propertyName + "' in '"
                + metaObject.getOriginalObject().getClass().getName() + "'.");
        }
        if (StringUtils.checkValNotNull(metaObject.getValue(keyTarget.propertyName))) {
            return;
        }
        Class<?> propertyType = metaObject.getSetterType(keyTarget.propertyName);
        TypeHandler<?> typeHandler = configuration.getTypeHandlerRegistry()
            .getTypeHandler(propertyType, JdbcType.forCode(rsmd.getColumnType(columnPosition)));
        if (typeHandler != null) {
            metaObject.setValue(keyTarget.propertyName, typeHandler.getResult(rs, columnPosition));
        }
    }

    private KeyTarget resolveKeyTarget(Object parameter, String keyProperty) {
        if (parameter instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameter;
            int dotIndex = keyProperty.indexOf('.');
            if (dotIndex > -1) {
                String paramName = keyProperty.substring(0, dotIndex);
                if (paramMap.containsKey(paramName)) {
                    return new KeyTarget(paramMap.get(paramName), keyProperty.substring(dotIndex + 1));
                }
            }
            if (paramMap.containsKey(Constants.ENTITY)) {
                return new KeyTarget(paramMap.get(Constants.ENTITY), keyProperty);
            }
        }
        return new KeyTarget(parameter, keyProperty);
    }

    private Collection<?> collectionize(Object parameter) {
        if (parameter instanceof Collection) {
            return (Collection<?>) parameter;
        }
        if (parameter instanceof Object[]) {
            return Arrays.asList((Object[]) parameter);
        }
        return Collections.singletonList(parameter);
    }

    private boolean isSingleParameter(Object parameter) {
        return !(parameter instanceof Collection) && !(parameter instanceof Object[]);
    }

    private static class KeyTarget {
        private final Object target;
        private final String propertyName;

        private KeyTarget(Object target, String propertyName) {
            this.target = target;
            this.propertyName = propertyName;
        }
    }
}
