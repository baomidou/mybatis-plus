package com.baomidou.mybatisplus.extension.p6spy;

/**
 * <p>
 * 输出 SQL 日志
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
public class StdoutLogger extends com.p6spy.engine.spy.appender.StdoutLogger {

    @Override
    public void logText(String text) {
        // 打印红色 SQL 日志
        System.err.println(text);
    }
}
