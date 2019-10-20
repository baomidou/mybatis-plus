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
package com.baomidou.mybatisplus.extension.plugins;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.executor.MybatisBatchExecutor;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.support.Range;

/**
 * DML操作记录数预言拦截器。
 * <p>
 * 功能：
 * 检查 UPDATE / DELETE 语句影响的记录数是否符合预期，如果不符合预期，那就回滚事务、抛出运行时异常（大致逻辑），处理方式在不同情况下略有区别：<ul>
 * <li>如果是托管事务（数据库连接已经是手动提交），抛出运行时异常，让事务管理器接管异常，处理事务回滚之类的事情。</li>
 * <li>如果数据库连接是自动提交的，该拦截器会在开始的时候调整为手动提交，结果不符合预期的时候回滚事务、并抛出运行时异常，中断调用流程。</li>
 * </ul>
 * <p>
 * 设计动机：
 * 在操作记录数这一维度，保护数据不被意外情况破坏。
 * 数据被意外破坏的隐患一直存在，基于如下观察到的情况：<ul>
 *     <li>正确的做法就有限的几种，出错的可能性多种多样。再加上可能项目时间紧迫、人员不足等情况，出错可能性更大。</li>
 *     <li>事务代码写起来比较繁琐，包括：JDBC原生事务、Spring注解事务、Spring手动事务。对于单条SQL，大家不太使用事务的方式来写，如果多操作了数据，最多报个错，数据是不会回滚的。</li>
 *     <li>使用Spring事务容易踩坑，特别是在使用不熟练的情况下。</li>
 * </ul>
 * <p>
 * 支持范围：
 * <ol>
 *     <li>支持：Mybatis Plus Mapper 模式。使用{@link AbstractWrapper#setExpectedDmlRowCount(Range)}方法指定预期。</li>
 *     <li>支持：Mybatis Plus ActiveRecord 模式。使用{@link AbstractWrapper#setExpectedDmlRowCount(Range)}方法指定预期。</li>
 *     <li>暂不支持：Mybatis 原生 Mapper 模式（实际用到的场景较少）</li>
 * </ol>
 * <p>
 * Mybatis Plus Mapper 模式，写法示例：
 * <pre>{@code
 * userMapper.update(new H2User(),
 * 	new UpdateWrapper<H2User>().lambda().setExpectedDmlRowCount(Range.is(1))
 * 		.eq(H2User::getName, name)
 * 		.set(H2User::getName, nameNew)
 * );
 * ...
 * userMapper.delete(new QueryWrapper<H2User>().lambda().setExpectedDmlRowCount(Range.is(1))
 * 	.eq(H2User::getName, "")
 * );
 * ...
 * userMapper.delete(new QueryWrapper<H2User>().setExpectedDmlRowCount(Range.between(2, 10)));
 * }</pre>
 * <p>
 * Mybatis Plus ActiveRecord 模式，写法示例：
 * <pre>{@code
 * student.setName(nameNew).update(
 * 	new UpdateWrapper<H2Student>().lambda().setExpectedDmlRowCount(Range.is(1))
 * 		.eq(H2Student::getName, name)
 * );
 * ...
 * student.delete(new QueryWrapper<H2Student>().lambda().setExpectedDmlRowCount(Range.is(1))
 * 	.eq(H2Student::getName, "")
 * );
 * ...
 * student.delete(new QueryWrapper<H2Student>().setExpectedDmlRowCount(Range.between(2, 10)));
 * }</pre>
 * <p>
 * 配置示例：
 * <pre>{@code
 * MybatisConfiguration configuration = new MybatisConfiguration();
 * ...
 * configuration.setGlobalConfig(globalConfig);
 * mybatisSqlSessionFactoryBean.setConfiguration(configuration);
 * GlobalConfig.DmlRowCountPredictionConfig dmlRowCountPredictionConfig = globalConfig.getDmlRowCountPredictionConfig();
 * dmlRowCountPredictionConfig.setPredictionEnabled(true).setDefaultExpectedDmlRowCountEnabled(false)
 * 	.setDefaultExpectedDmlRowCount(Range.between(1, 1));
 * configuration.addInterceptor(new DmlRowCountPredictionInterceptor());
 * }</pre>
 *
 * @author sandynz
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DmlRowCountPredictionInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(DmlRowCountPredictionInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        switch (mappedStatement.getSqlCommandType()) {
            case UPDATE:
            case DELETE:
                break;
            default:
                return invocation.proceed();
        }

        Configuration configuration = mappedStatement.getConfiguration();
        if (!(configuration instanceof MybatisConfiguration)) {
            return invocation.proceed();
        }
        MybatisConfiguration mybatisConfiguration = (MybatisConfiguration) configuration;
        GlobalConfig.DmlRowCountPredictionConfig dmlRowCountPredictionConfig = mybatisConfiguration.getGlobalConfig().getDmlRowCountPredictionConfig();
        if (!dmlRowCountPredictionConfig.isPredictionEnabled()) {
            // 预言没开启
            return invocation.proceed();
        }

        if (!(args[1] instanceof Map)) {
            return invocation.proceed();
        }
        Map<String, Object> updateParamMap = (Map) args[1];
        Object wrapper = updateParamMap.getOrDefault(Constants.WRAPPER, null);
        if (!(wrapper instanceof Wrapper)) {
            return invocation.proceed();
        }

        Executor executor = (Executor) invocation.getTarget();
        if (executor instanceof MybatisBatchExecutor) {
            // BatchExecutor.update无法返回实际更新的记录数（返回的是一个负数），这种情况需要忽略；
            // 由于无法从Executor获取ExecutorType来判断，所以用类型来判断。
            return invocation.proceed();
        }
        Transaction transaction = executor.getTransaction();
        Connection connection = transaction.getConnection();

        final boolean isOriginAutoCommit = connection.getAutoCommit();
        if (isOriginAutoCommit) {
            // 如果原始连接没有开启手动事务，那自动开启
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                logger.warn("setAutoCommit to false failed, connection={}", connection, e);
                // 开启失败，忽略本拦截器功能
                return invocation.proceed();
            }
        }

        Object result;
        try {
            result = invocation.proceed();
        } catch (Throwable e) {
            if (isOriginAutoCommit) {
                rollbackForConn(connection, "invocationProceedFailed");
            }
            throw e;
        }

        // verify conn
        {
            Connection postConn = transaction.getConnection();
            if (postConn != connection) {
                // 理论上这两个连接是同一个，假如出现了意外情况：1）打印详细日志，2）提交之前开启的手动事务
                logger.warn("postConn != connection, postConn={}, connection={}", postConn, connection, new Exception("DEBUG_POSTCONN_NE_CONN"));
                if (isOriginAutoCommit) {
                    commitForConn(connection, "postConnNeConn");
                }
                return result;
            }
        }

        if (result instanceof Integer) {
            Wrapper ew = (Wrapper) wrapper;
            Range<Integer> expectedDmlRowCount = ew.getExpectedDmlRowCount();
            if (expectedDmlRowCount == null) {
                if (dmlRowCountPredictionConfig.isDefaultExpectedDmlRowCountEnabled()) {
                    expectedDmlRowCount = dmlRowCountPredictionConfig.getDefaultExpectedDmlRowCount();
                }
            }
            if (expectedDmlRowCount == null) {
                commitForConn(connection, "expectedDmlRowCountNull");
                return result;
            }

            int dmlRowCount = (Integer) result;
            if (expectedDmlRowCount.contains(dmlRowCount)) {
                if (isOriginAutoCommit) {
                    // 如果是前面开启的手动事务，手动提交
                    commitForConn(connection, "dmlRowCountMatched");
                }
            } else {
                logger.info("not match, dmlRowCount={}, expectedDmlRowCount={}", dmlRowCount, expectedDmlRowCount);
                if (isOriginAutoCommit) {
                    // 如果是前面开启的手动事务，手动回滚。然后抛出异常
                    rollbackForConn(connection, "dmlRowCountNotMatched");
                }
                //else, 抛出异常，让负责管理连接的组件来回滚事务（比如：Spring注解事务、Spring手动事务）
                throw new MybatisPlusException(String.format("实际操作记录数%d不在期望范围：%s", dmlRowCount, expectedDmlRowCount.toString()));
            }
        } else {
            if (isOriginAutoCommit) {
                commitForConn(connection, "resultNotInteger");
            }
        }

        return result;
    }

    private void commitForConn(Connection connection, String callFrom) {
        try {
            connection.commit();
        } catch (SQLException e) {
            logger.warn("commit failed, ignore, callFrom={}, connection={}", callFrom, connection, e);
        }
    }

    private void rollbackForConn(Connection connection, String callFrom) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.warn("rollback failed, ignore, callFrom={}, connection={}", callFrom, connection, e);
        }
    }

}
