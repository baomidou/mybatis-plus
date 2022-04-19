package com.baomidou.mybatisplus.extension.plugins.handler;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import lombok.Data;

/**
 * 用于分页时传入自定义ResultHandler场景
 *
 * @author VampireAchao
 * @since 2022-04-19
 */
@Data
public abstract class PageResultHandler<T> implements ResultHandler<T> {

    /**
     * 分页时返回count条数
     */
    protected long count;

    @Override
    public void handleResult(ResultContext<? extends T> resultContext) {
        if (resultContext.getResultObject() instanceof Long) {
            this.count = Long.parseLong(resultContext.getResultObject().toString());
        }
    }
}
