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
package com.baomidou.mybatisplus.core.plugins;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.PropertyMapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author miemie
 * @since 3.4.0
 */
@Slf4j
@SuppressWarnings({"rawtypes"})
@Intercepts(
    {
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = StatementHandler.class, method = "getBoundSql", args = {}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    }
)
public class MybatisPlusInterceptor implements Interceptor {

    /**
     * Mapper 方法反射缓存。
     * key = MappedStatement.getId()（格式：com.example.UserMapper.selectPage）
     * 通过 ConcurrentHashMap 保证同一 MappedStatement 的反射解析仅执行一次。
     */
    protected static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    @Setter
    private List<InnerInterceptor> interceptors = new ArrayList<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            final Executor executor = (Executor) target;
            Object mapperMethodParameter = args[1];
            boolean isUpdate = args.length == 2;
            MappedStatement ms = (MappedStatement) args[0];
            SqlCommandType sqlCommandType = ms.getSqlCommandType();
            // 提前解析 Mapper 方法（带缓存），同一 Executor 内所有 afterIntercept 调用共享
            Method mapperMethod = resolveMapperMethod(ms.getId());
            if (!isUpdate && sqlCommandType == SqlCommandType.SELECT) {
                RowBounds rowBounds = (RowBounds) args[2];
                ResultHandler resultHandler = (ResultHandler) args[3];
                BoundSql boundSql;
                if (args.length == 4) {
                    boundSql = ms.getBoundSql(mapperMethodParameter);
                } else {
                    // 几乎不可能走进这里面,除非使用Executor的代理对象调用query[args[6]]
                    boundSql = (BoundSql) args[5];
                }

                for (InnerInterceptor query : interceptors) {
                    if (!query.willDoQuery(executor, ms, mapperMethodParameter, rowBounds, resultHandler, boundSql)) {
                        return afterIntercept(sqlCommandType, invocation, mapperMethodParameter,
                            Collections.emptyList(), mapperMethod, false);
                    }
                    query.beforeQuery(executor, ms, mapperMethodParameter, rowBounds, resultHandler, boundSql);
                }
                CacheKey cacheKey = executor.createCacheKey(ms, mapperMethodParameter, rowBounds, boundSql);
                Object queryResult = executor.query(ms, mapperMethodParameter, rowBounds, resultHandler, cacheKey, boundSql);
                return afterIntercept(sqlCommandType, invocation, mapperMethodParameter, queryResult, mapperMethod, true);
            } else if (isUpdate) {
                for (InnerInterceptor update : interceptors) {
                    if (!update.willDoUpdate(executor, ms, mapperMethodParameter)) {
                        return afterIntercept(sqlCommandType, invocation, mapperMethodParameter, -1, mapperMethod, false);
                    }
                    update.beforeUpdate(executor, ms, mapperMethodParameter);
                }
                return afterIntercept(sqlCommandType, invocation, mapperMethodParameter, invocation.proceed(), mapperMethod, true);
            }
            return afterIntercept(sqlCommandType, invocation, mapperMethodParameter, invocation.proceed(), mapperMethod, true);
        } else {
            // StatementHandler
            final StatementHandler sh = (StatementHandler) target;
            // 目前只有StatementHandler.getBoundSql方法args才为null
            if (null == args) {
                for (InnerInterceptor innerInterceptor : interceptors) {
                    innerInterceptor.beforeGetBoundSql(sh);
                }
            } else {
                Connection connections = (Connection) args[0];
                Integer transactionTimeout = (Integer) args[1];
                for (InnerInterceptor innerInterceptor : interceptors) {
                    innerInterceptor.beforePrepare(sh, connections, transactionTimeout);
                }
            }
            return afterIntercept(null, invocation, null, invocation.proceed(), null, true);
        }
    }

    /**
     * 拦截器后置处理，覆盖 {@link #intercept(Invocation)} 的所有返回路径。
     * 默认实现仅对 SELECT 结果做 IPage 自动包装，其余类型原样返回。
     * <p>
     * 当 Mapper 方法返回类型为 IPage 时，MyBatis 底层会走 {@code selectOne}，
     * 而 {@code selectOne} 内部实际调用 {@code selectList} 获取 List 结果。
     * 为了让 {@code selectOne} 能正确返回 IPage 对象，此处将查询结果 List
     * 载入 IPage 后包装为单元素 List（{@code Collections.singletonList(page)}），
     * {@code selectOne} 取 {@code get(0)} 即得到完整的 IPage 对象。
     * </p>
     * <p>子类可重写此方法实现自定义的结果加工。</p>
     *
     * @param sqlCommandType SQL 命令类型（StatementHandler 路径时为 null）
     * @param invocation     原始 Invocation（兜底）
     * @param mapperMethodParameter Mapper 方法的调用实参（StatementHandler 路径时为 null）
     * @param result               Executor 操作或 invocation.proceed() 的返回结果
     * @param mapperMethod         Mapper 方法（带缓存，SELECT 外为 null）
     * @param executed 是否实际执行了底层操作（false 表示被 InnerInterceptor 短路跳过）
     * @return 加工后的结果。IPage 场景返回单元素 List（元素为 IPage），由 selectOne 解包后返回 IPage 对象；非 IPage 场景原样返回
     */
    protected Object afterIntercept(SqlCommandType sqlCommandType, Invocation invocation, Object mapperMethodParameter,
                                     Object result, Method mapperMethod, boolean executed) {
        // 默认仅处理 SELECT 返回 IPage 的场景：将查询结果 List 载入 IPage 后包装为单元素
        // List，使调用方 selectOne 解包后拿到完整的 IPage 对象。其他定制需求（如 UPDATE
        // 后置校验、INSERT 审计日志、DELETE 缓存失效等）可通过继承重写本方法实现。
        if (sqlCommandType == SqlCommandType.SELECT
            && mapperMethod != null && IPage.class.isAssignableFrom(mapperMethod.getReturnType())) {
            return processPageResult(mapperMethodParameter, result, mapperMethod, executed);
        }
        // 非目标场景，原样返回
        return result;
    }

    /**
     * Page 返回值处理。从参数中查找 IPage 实例，将查询结果 List 写入后包装为
     * 单元素 List（{@code Collections.singletonList(page)}）。
     * <p>
     * IPage 返回值经 {@code selectOne} 获取——{@code selectOne} 内部调用
     * {@code selectList} 拿到 List 后按 {@code size==1 取 get(0)} 解包，
     * 此处将 List 载入 IPage 并包装为单元素 List，使解包后得到完整的 IPage 对象。
     * </p>
     * <p>子类可重写此方法自定义 Page 结果的加工逻辑。</p>
     *
     * @param mapperMethodParameter Mapper 方法的调用实参
     * @param queryResult          executor.query() 的返回结果
     * @param mapperMethod         Mapper 方法
     * @param executed             是否实际执行了底层操作（false 表示被 InnerInterceptor 短路跳过）
     * @return 单元素 List（元素为 IPage），由 selectOne 解包后返回 IPage 对象；非 List 或未找到 IPage 参数时原样返回
     */
    @SuppressWarnings("unchecked")
    protected Object processPageResult(Object mapperMethodParameter, Object queryResult,
                                        Method mapperMethod, boolean executed) {
        if (!(queryResult instanceof List)) {
            return queryResult;
        }
        IPage page = ParameterUtils.findPage(mapperMethodParameter).orElse(null);
        if (page != null) {
            page.setRecords((List) queryResult);
            return Collections.singletonList(page);
        }
        return queryResult;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    public void addInnerInterceptor(InnerInterceptor innerInterceptor) {
        this.interceptors.add(innerInterceptor);
    }

    public List<InnerInterceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

    /**
     * 使用内部规则,拿分页插件举个栗子:
     * <p>
     * - key: "@page" ,value: "com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor"
     * - key: "page:limit" ,value: "100"
     * <p>
     * 解读1: key 以 "@" 开头定义了这是一个需要组装的 `InnerInterceptor`, 以 "page" 结尾表示别名
     * value 是 `InnerInterceptor` 的具体的 class 全名
     * 解读2: key 以上面定义的 "别名 + ':'" 开头指这个 `value` 是定义的该 `InnerInterceptor` 属性需要设置的值
     * <p>
     * 如果这个 `InnerInterceptor` 不需要配置属性也要加别名
     */
    @Override
    public void setProperties(Properties properties) {
        PropertyMapper pm = PropertyMapper.newInstance(properties);
        Map<String, Properties> group = pm.group(StringPool.AT);
        group.forEach((k, v) -> {
            InnerInterceptor innerInterceptor = ClassUtils.newInstance(k);
            innerInterceptor.setProperties(v);
            addInnerInterceptor(innerInterceptor);
        });
    }

    @Override
    public String toString() {
        return "MybatisPlusInterceptor{" +
            "interceptors=" + interceptors +
            '}';
    }

    /**
     * 从 MappedStatement ID 反查 Mapper 方法（带缓存）。
     *
     * @param statementId MappedStatement.getId()，格式：com.example.UserMapper.selectPage
     * @return 对应的 Method 对象，解析失败时返回 null
     */
    protected Method resolveMapperMethod(String statementId) {
        return METHOD_CACHE.computeIfAbsent(statementId, key -> {
            try {
                String className = key.substring(0, key.lastIndexOf('.'));
                String methodName = key.substring(key.lastIndexOf('.') + 1);
                Class<?> clazz = Class.forName(className);
                for (Method m : clazz.getMethods()) {
                    if (m.getName().equals(methodName)) {
                        return m;
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to resolve mapper method for statement: {} - {}: {}", statementId,
                    e.getClass().getSimpleName(), e.getMessage());
            }
            return null;
        });
    }
}
