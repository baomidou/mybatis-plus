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
package com.baomidou.mybatisplus.extension.parser;

import com.baomidou.mybatisplus.extension.parser.cache.JsqlParseCache;
import com.baomidou.mybatisplus.jsqlparser.JsqlParserThreadPool;
import lombok.Setter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;

import java.util.concurrent.ExecutorService;

/**
 * @author miemie
 * @since 2023-08-05
 */
public class JsqlParserGlobal {


    /**
     * 默认线程数大小
     *
     * @since 3.5.6
     * @deprecated {@link JsqlParserThreadPool#DEFAULT_THREAD_SIZE}
     */
    @Deprecated
    public static final int DEFAULT_THREAD_SIZE = (Runtime.getRuntime().availableProcessors() + 1) / 2;

    /**
     * 默认解析处理线程池
     * <p>注意: 由于项目情况,机器配置等不一样因素,请自行根据情况创建指定线程池.</p>
     *
     * @see java.util.concurrent.ThreadPoolExecutor
     * @see #setExecutorService(ExecutorService)
     * @see #setExecutorService(ExecutorService, boolean)
     * @since 3.5.6
     * @deprecated 3.5.11 后面不再公开此属性
     */
    @Deprecated
    public static ExecutorService executorService;

    @Setter
    private static JsqlParserFunction<String, Statement> parserSingleFunc = sql -> CCJSqlParserUtil.parse(sql, getExecutorService(), null);

    @Setter
    private static JsqlParserFunction<String, Statements> parserMultiFunc = sql -> CCJSqlParserUtil.parseStatements(sql, getExecutorService(), null);

    @Setter
    private static JsqlParseCache jsqlParseCache;

    /**
     * 设置解析线程池
     *
     * @param executorService 线程池 (自行控制线程池关闭)
     * @since 3.5.11
     */
    public static void setExecutorService(ExecutorService executorService) {
        JsqlParserGlobal.executorService = executorService;
    }

    /**
     * 设置解析线程池
     *
     * @param executorService 线程池 (自行控制线程池关闭)
     * @param addShutdownHook 是否注册退出关闭钩子
     * @since 3.5.11
     * @deprecated 3.5.12 推荐使用 {@link #setExecutorService(ExecutorService, Thread)}
     */
    @Deprecated
    public static void setExecutorService(ExecutorService executorService, boolean addShutdownHook) {
        JsqlParserGlobal.executorService = executorService;
        if (addShutdownHook) {
            JsqlParserThreadPool.addShutdownHook(executorService);
        }
    }

    /**
     * 设置解析线程池
     * @param executorService 线程池 (自行控制线程池关闭)
     * @param shutdownHook 关闭钩子
     * @since 3.5.12
     */
    public static void setExecutorService(ExecutorService executorService, Thread shutdownHook) {
        JsqlParserGlobal.executorService = executorService;
        if (shutdownHook != null) {
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
    }

    /**
     * 获取解析线程池(如果未自定义则返回默认的解析线程池)
     *
     * @return 解析线程池
     * @since 3.5.11
     */
    public static ExecutorService getExecutorService() {
        return JsqlParserGlobal.executorService == null ? JsqlParserThreadPool.getDefaultThreadPoolExecutor() : JsqlParserGlobal.executorService;
    }

    public static Statement parse(String sql) throws JSQLParserException {
        if (jsqlParseCache == null) {
            return parserSingleFunc.apply(sql);
        }
        Statement statement = jsqlParseCache.getStatement(sql);
        if (statement == null) {
            statement = parserSingleFunc.apply(sql);
            jsqlParseCache.putStatement(sql, statement);
        }
        return statement;
    }

    public static Statements parseStatements(String sql) throws JSQLParserException {
        if (jsqlParseCache == null) {
            return parserMultiFunc.apply(sql);
        }
        Statements statements = jsqlParseCache.getStatements(sql);
        if (statements == null) {
            statements = parserMultiFunc.apply(sql);
            jsqlParseCache.putStatements(sql, statements);
        }
        return statements;
    }
}
