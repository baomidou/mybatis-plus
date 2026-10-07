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
package com.baomidou.mybatisplus.extension.ddl;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.sql.SQLException;


/**
 * 错误处理器
 *
 * @author nieqiurong
 * @since 3.5.11
 */
@FunctionalInterface
public interface DdlScriptErrorHandler {

    /**
     * 错误处理
     *
     * @param sqlFile   执行的sql文件
     * @param exception 异常信息 {@link org.apache.ibatis.jdbc.RuntimeSqlException}
     * @throws SQLException SQLException
     */
    void handle(String sqlFile, Exception exception) throws SQLException;

    /**
     * 打印错误日志 (继续执行)
     */
    class PrintlnLogErrorHandler implements DdlScriptErrorHandler {

        public static final PrintlnLogErrorHandler INSTANCE = new PrintlnLogErrorHandler();

        public static final Log log = LogFactory.getLog(PrintlnLogErrorHandler.class);

        @Override
        public void handle(String sqlFile, Exception throwable) {
            log.error("run script sql:" + sqlFile + ", error: ", throwable);
        }
    }

    /**
     * 抛出错误 (中断后续文件执行)
     */
    class ThrowsErrorHandler implements DdlScriptErrorHandler {

        public static final ThrowsErrorHandler INSTANCE = new ThrowsErrorHandler();

        @Override
        public void handle(String sqlFile, Exception throwable) throws SQLException {
            throw new SQLException("Execute " + sqlFile + " fail. ", throwable);
        }

    }

}
