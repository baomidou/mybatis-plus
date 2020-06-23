package com.baomidou.mybatisplus.extension.plugins.inner;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author miemie
 * @since 2020-06-16
 */
@Data
@Accessors(chain = true)
@SuppressWarnings({"rawtypes"})
public class PaginationInnerInterceptor implements InnerInterceptor {

    protected static final Map<String, MappedStatement> countMsCache = new ConcurrentHashMap<>();
    protected final Log logger = LogFactory.getLog(this.getClass());

    /**
     * COUNT SQL 解析
     */
    protected ISqlParser countSqlParser;
    /**
     * 溢出总页数后是否进行处理
     */
    protected boolean overflow = false;
    /**
     * 单页限制 500 条，小于 0 如 -1 不受限制
     */
    protected long limit = 500L;
    /**
     * 数据库类型
     *
     * @since 3.3.1
     */
    private DbType dbType;
    /**
     * 方言实现类
     *
     * @since 3.3.1
     */
    private IDialect dialect;

    /**
     * 这里进行count,如果count为0这返回false(就是不再执行sql了)
     */
    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 判断参数里是否有page对象
        IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
        if (page == null || page.getSize() < 0 || !page.isSearchCount()) {
            return true;
        }

        BoundSql countSql;
        MappedStatement countMs = buildCountMappedStatement(ms, page);
        if (countMs != null) {
            countSql = countMs.getBoundSql(parameter);
        } else {
            countMs = buildAutoCountMappedStatement(ms);
            SqlInfo countSqlInfo = SqlParserUtils.getOptimizeCountSql(page.optimizeCountSql(), countSqlParser,
                boundSql.getSql(), SystemMetaObject.forObject(parameter));
            PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
            countSql = new BoundSql(countMs.getConfiguration(), countSqlInfo.getSql(), mpBoundSql.parameterMappings(), parameter);
            PluginUtils.setAdditionalParameter(countSql, mpBoundSql.additionalParameters());
        }

        CacheKey cacheKey = executor.createCacheKey(countMs, parameter, rowBounds, countSql);
        Object result = executor.query(countMs, parameter, rowBounds, resultHandler, cacheKey, countSql).get(0);
        page.setTotal(Long.parseLong(result.toString()));
        return continueLimit(page);
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 判断参数里是否有page对象
        IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
        /*
         * 不需要分页的场合，如果 size 小于 0 返回结果集
         */
        if (null == page || page.getSize() < 0) {
            return;
        }

        if (this.limit > 0 && this.limit <= page.getSize()) {
            //处理单页条数限制
            handlerLimit(page);
        }

        DbType dbType = this.dbType == null ? JdbcUtils.getDbType(executor) : this.dbType;
        IDialect dialect = Optional.ofNullable(this.dialect).orElseGet(() -> DialectFactory.getDialect(dbType));

        String originalSql = boundSql.getSql();
        String buildSql = this.concatOrderBy(originalSql, page);

        final Configuration configuration = ms.getConfiguration();
        DialectModel model = dialect.buildPaginationSql(buildSql, page.offset(), page.getSize());
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);

        List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
        Map<String, Object> additionalParameter = mpBoundSql.additionalParameters();
        model.consumers(mappings, configuration, additionalParameter);
        mpBoundSql.sql(model.getDialectSql());
        mpBoundSql.parameterMappings(mappings);
    }

    protected MappedStatement buildCountMappedStatement(MappedStatement ms, IPage<?> page) {
        String countId = page.countId();
        if (StringUtils.isNotBlank(countId)) {
            final String id = ms.getId();
            if (!countId.contains(StringPool.DOT)) {
                countId = id.substring(0, id.lastIndexOf(StringPool.DOT)) + countId;
            }
            final Configuration configuration = ms.getConfiguration();
            try {
                return countMsCache.computeIfAbsent(countId, key -> configuration.getMappedStatement(key, false));
            } catch (Exception e) {
                logger.warn(String.format("can not find this countId: [\"%s\"]", countId));
            }
        }
        return null;
    }

    protected MappedStatement buildAutoCountMappedStatement(MappedStatement ms) {
        final String countId = ms.getId() + "_mpCount";
        final Configuration configuration = ms.getConfiguration();
        return countMsCache.computeIfAbsent(countId, key -> {
            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, key, ms.getSqlSource(), ms.getSqlCommandType());
            builder.resource(ms.getResource());
            builder.fetchSize(ms.getFetchSize());
            builder.statementType(ms.getStatementType());
            builder.timeout(ms.getTimeout());
            builder.parameterMap(ms.getParameterMap());
            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, Constants.MYBATIS_PLUS, Long.class, Collections.emptyList()).build()));
            builder.resultSetType(ms.getResultSetType());
            builder.cache(ms.getCache());
            builder.flushCacheRequired(ms.isFlushCacheRequired());
            builder.useCache(ms.isUseCache());
            return builder.build();
        });
    }

    /**
     * 查询SQL拼接Order By
     *
     * @param originalSql 需要拼接的SQL
     * @param page        page对象
     * @return ignore
     */
    protected String concatOrderBy(String originalSql, IPage<?> page) {
        if (CollectionUtils.isNotEmpty(page.orders())) {
            try {
                List<OrderItem> orderList = page.orders();
                Select selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
                if (selectStatement.getSelectBody() instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
                    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addOrderByElements(orderList, orderByElements);
                    plainSelect.setOrderByElements(orderByElementsReturn);
                    return plainSelect.toString();
                } else if (selectStatement.getSelectBody() instanceof SetOperationList) {
                    SetOperationList setOperationList = (SetOperationList) selectStatement.getSelectBody();
                    List<OrderByElement> orderByElements = setOperationList.getOrderByElements();
                    List<OrderByElement> orderByElementsReturn = addOrderByElements(orderList, orderByElements);
                    setOperationList.setOrderByElements(orderByElementsReturn);
                    return setOperationList.toString();
                } else if (selectStatement.getSelectBody() instanceof WithItem) {
                    // todo: don't known how to resole
                    return originalSql;
                } else {
                    return originalSql;
                }

            } catch (JSQLParserException e) {
                logger.warn("failed to concat orderBy from IPage, exception=" + e.getMessage());
            }
        }
        return originalSql;
    }

    protected List<OrderByElement> addOrderByElements(List<OrderItem> orderList, List<OrderByElement> orderByElements) {
        orderByElements = CollectionUtils.isEmpty(orderByElements) ? new ArrayList<>(orderList.size()) : orderByElements;
        List<OrderByElement> orderByElementList = orderList.stream()
            .filter(item -> StringUtils.isNotBlank(item.getColumn()))
            .map(item -> {
                OrderByElement element = new OrderByElement();
                element.setExpression(new Column(item.getColumn()));
                element.setAsc(item.isAsc());
                element.setAscDescPresent(true);
                return element;
            }).collect(Collectors.toList());
        orderByElements.addAll(orderByElementList);
        return orderByElements;
    }

    /**
     * 判断是否继续执行 Limit 逻辑
     *
     * @param page 分页对象
     * @return 是否
     */
    protected boolean continueLimit(IPage<?> page) {
        if (page.getTotal() <= 0) {
            return false;
        }
        if (page.getCurrent() > page.getPages()) {
            if (this.overflow) {
                //溢出总页数处理
                handlerOverflow(page);
            } else {
                // 超过最大范围，未设置溢出逻辑中断 list 执行
                return false;
            }
        }
        return true;
    }

    /**
     * 处理超出分页条数限制,默认归为限制数
     *
     * @param page IPage
     */
    protected void handlerLimit(IPage<?> page) {
        page.setSize(this.limit);
    }

    /**
     * 处理页数溢出,默认设置为第一页
     *
     * @param page IPage
     */
    protected void handlerOverflow(IPage<?> page) {
        page.setCurrent(1);
    }
}
