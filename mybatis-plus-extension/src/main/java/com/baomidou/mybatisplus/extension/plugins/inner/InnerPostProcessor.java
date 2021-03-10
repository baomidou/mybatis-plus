package com.baomidou.mybatisplus.extension.plugins.inner;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 后置处理器，可以扩展手机号，身份证 脱敏
 * @author liyabin
 * @date 2021/3/10
 */
public interface InnerPostProcessor {

    <E> boolean postProcessorBefore(List<E> dataList, Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql);

    <E> List<E> postProcessorAfter(List<E> dataList, Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

}
