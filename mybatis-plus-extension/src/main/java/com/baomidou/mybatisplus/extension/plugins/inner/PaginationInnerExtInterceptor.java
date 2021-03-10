package com.baomidou.mybatisplus.extension.plugins.inner;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;

/**
 * @author liyabin
 * @date 2021/3/10
 */
public class PaginationInnerExtInterceptor extends PaginationInnerInterceptor implements InnerPostProcessor {

    private boolean directSelect;

    public PaginationInnerExtInterceptor(DbType dbType) {
        super(dbType);
    }

    public PaginationInnerExtInterceptor(IDialect dialect) {
        super(dialect);
    }

    public PaginationInnerExtInterceptor(IDialect dialect, boolean directSelect) {
        super(dialect);
        this.directSelect = directSelect;
    }

    public PaginationInnerExtInterceptor(DbType dbType, boolean directSelect) {
        super(dbType);
        this.directSelect = directSelect;
    }

    /**
     * 如果directSelect 为true 那么直接使用父类方法
     */
    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        return !directSelect || super.willDoQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }

    @Override
    public <E> boolean postProcessorBefore(List<E> dataList, Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) {
        return !directSelect;
    }

    @Override
    public <E> List<E> postProcessorAfter(List<E> dataList, Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
        if (page == null || page.getSize() < 0 || !page.isSearchCount()) {
            return dataList;
        }
        long dataSize = Optional.ofNullable(dataList).map(List::size).orElse(0);
        long total;
        // 如果查询出来的数据集小于 pageSize ,这个时候total 可以利用数学方法计算得出
        if (dataSize < page.getSize() && dataSize > 0) {
            total = (page.getCurrent() - 1) * page.getSize() + dataSize;
        } else if (dataSize == 0 && page.getCurrent() == 1) {
            // 这表示这条SQL结果就是空
            total = 0;
        } else {
            // 这个时候才应该查DB库
            total = getTotal(page, executor, ms, parameter, rowBounds, resultHandler, boundSql);
            // 只是最坏的情况，溢出总数而且配置溢出默认查第一页的情况
            if (dataSize == 0 && continuePage(page)) {
                dataList = executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
            }
        }
        page.setTotal(total);

        return dataList;
    }

    /**
     * count 查询之后,是否继续执行分页
     *
     * @param page 分页对象
     * @return 是否
     */
    @Override
    protected boolean continuePage(IPage<?> page) {
        if (!overflow || page.getCurrent() <= page.getPages()) {
            return false;
        }
        handlerOverflow(page);
        return true;
    }
}
