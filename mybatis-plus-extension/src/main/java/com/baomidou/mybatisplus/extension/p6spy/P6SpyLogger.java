package com.baomidou.mybatisplus.extension.p6spy;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * <p>
 * P6spy SQL 打印策略
 * </p>
 *
 * @author hubin
 * @since 2019-02-20
 */
public class P6SpyLogger implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                                String prepared, String sql, String url) {
        return StringUtils.isNotEmpty(sql) ? new StringBuilder().append(" Consume Time：").append(elapsed).append(" ms ").append(now)
            .append("\n Execute SQL：").append(sql.replaceAll("[\\s]+", " "))
            .append("\n").toString() : null;
    }
}
