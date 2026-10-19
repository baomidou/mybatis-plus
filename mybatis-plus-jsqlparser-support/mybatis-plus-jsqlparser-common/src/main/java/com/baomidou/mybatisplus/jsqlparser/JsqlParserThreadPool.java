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
package com.baomidou.mybatisplus.jsqlparser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * JsqlParser解析线程池
 * <p>
 * 当没指定解析线程池时,默认使用一个固定长度的线程作为解析线程池.
 * </p>
 *
 * @author nieqiurong
 * @since 3.5.11
 */
public class JsqlParserThreadPool {

    /**
     * 默认线程数大小
     */
    public static final int DEFAULT_THREAD_SIZE = (Runtime.getRuntime().availableProcessors() + 1) / 2;

    /**
     * 获取默认解析线程池(固定大小)
     *
     * @return 解析线程池
     */
    public static ExecutorService getDefaultThreadPoolExecutor() {
        return DefaultJsqlParserFixedThreadPool.INSTANCE.getDefaultThreadPoolExecutor();
    }

    /**
     * 注册Jvm退出钩子
     *
     * @param executorService 线程池
     * @deprecated 3.5.12
     */
    @Deprecated
    public static void addShutdownHook(ExecutorService executorService) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        }, "mybatis-plus-jsqlParser-shutdown-hook"));
    }

    private static class DefaultJsqlParserFixedThreadPool {

        private static final DefaultJsqlParserFixedThreadPool INSTANCE = new DefaultJsqlParserFixedThreadPool();

        public static ExecutorService executorService = new ThreadPoolExecutor(DEFAULT_THREAD_SIZE, DEFAULT_THREAD_SIZE,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
                    Thread thread = new Thread(r);
                    thread.setName("mybatis-plus-jsqlParser-" + thread.getId());
                    thread.setDaemon(true);
                    return thread;
                });

        /**
         * 默认解析线程池(固定大小,默认大小{@link #DEFAULT_THREAD_SIZE})
         *
         * @return 线程池
         */
        public ExecutorService getDefaultThreadPoolExecutor() {
            return executorService;
        }

    }

}
