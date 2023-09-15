package com.baomidou.mybatisplus.core.batch;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * <li>事务需要自行控制</li>
 * <li>批次数据尽量自行切割处理</li>
 * <li>返回值为批处理结果,如果对返回值比较关心的可接收判断处理</li>
 * <li>saveOrUpdate尽量少用把,保持批处理为简单的插入或更新</li>
 * <li>关于saveOrUpdate中的sqlSession,如果执行了select操作的话,BatchExecutor都会触发一次flushStatements,为了保证结果集,故使用包装了部分sqlSession查询操作</li>
 * <pre>
 *     Spring示例:
 * 		transactionTemplate.execute(new TransactionCallback<List<BatchResult>>() {
 *            {@code @Override}
 * 			public List<BatchResult> doInTransaction(TransactionStatus status) {
 * 				MybatisBatch.Method<Demo> method = new MybatisBatch.Method<>(DemoMapper.class);
 * 				return new MybatisBatch<>(sqlSessionFactory,demoList).execute(true, method.insert());
 *            }
 *        });
 * </pre>
 *
 * @author nieqiurong
 * @since 3.5.4
 */
public class MybatisBatch<T> {

    private final SqlSessionFactory sqlSessionFactory;

    private final List<T> dataList;

    public MybatisBatch(SqlSessionFactory sqlSessionFactory, List<T> dataList) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.dataList = dataList;
    }

    public List<BatchResult> execute(String statement) {
        return execute(false, statement, (entity) -> entity);
    }

    public List<BatchResult> execute(String statement, ParameterConvert<T> parameterConvert) {
        return execute(false, statement, parameterConvert);
    }

    public List<BatchResult> execute(boolean autoCommit, String statement) {
        return execute(autoCommit, statement, null);
    }

    public List<BatchResult> execute(BatchMethod<T> batchMethod) {
        return execute(false, batchMethod);
    }

    public List<BatchResult> execute(boolean autoCommit, BatchMethod<T> batchMethod) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, autoCommit)) {
            for (T data : dataList) {
                ParameterConvert<T> parameterConvert = batchMethod.getParameterConvert();
                sqlSession.update(batchMethod.getStatementId(), toParameter(parameterConvert, data));
            }
            return sqlSession.flushStatements();
        }
    }

    public List<BatchResult> execute(boolean autoCommit, String statement, ParameterConvert<T> parameterConvert) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, autoCommit)) {
            for (T data : dataList) {
                sqlSession.update(statement, parameterConvert != null ? parameterConvert.convert(data) : data);
            }
            return sqlSession.flushStatements();
        }
    }

    public List<BatchResult> saveOrUpdate(BatchMethod<T> insertMethod, BiPredicate<BatchSqlSession, T> insertPredicate, BatchMethod<T> updateMethod) {
        return saveOrUpdate(false, insertMethod, insertPredicate, updateMethod);
    }

    public List<BatchResult> saveOrUpdate(boolean autoCommit, BatchMethod<T> insertMethod, BiPredicate<BatchSqlSession, T> insertPredicate, BatchMethod<T> updateMethod) {
        List<BatchResult> resultList = new ArrayList<>();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, autoCommit)) {
            BatchSqlSession session = new BatchSqlSession(sqlSession);
            for (T data : dataList) {
                if (insertPredicate.test(session, data)) {
                    sqlSession.insert(insertMethod.getStatementId(), toParameter(insertMethod.getParameterConvert(), data));
                } else {
                    sqlSession.update(updateMethod.getStatementId(), toParameter(updateMethod.getParameterConvert(), data));
                }
            }
            resultList.addAll(sqlSession.flushStatements());
            resultList.addAll(session.getResultBatchList());
            return resultList;
        }
    }

    protected Object toParameter(ParameterConvert<T> parameterConvert, T data) {
        return parameterConvert != null ? parameterConvert.convert(data) : data;
    }

    public static class Method<T> {

        private final String namespace;

        public Method(Class<?> mapperClass) {
            this.namespace = mapperClass.getName();
        }

        public BatchMethod<T> insert() {
            return new BatchMethod<>(namespace + StringPool.DOT + SqlMethod.INSERT_ONE.getMethod());
        }

        public BatchMethod<T> updateById() {
            return new BatchMethod<>(namespace + StringPool.DOT + SqlMethod.UPDATE_BY_ID.getMethod(), (entity) -> {
                Map<String, Object> param = new HashMap<>();
                param.put(Constants.ENTITY, entity);
                return param;
            });
        }

        public BatchMethod<T> update(Function<T, Wrapper<T>> wrapperFunction) {
            return new BatchMethod<>(namespace + StringPool.DOT + SqlMethod.UPDATE.getMethod(), (entity) -> {
                Map<String, Object> param = new HashMap<>();
                param.put(Constants.ENTITY, entity);
                param.put(Constants.WRAPPER, wrapperFunction.apply(entity));
                return param;
            });
        }
    }

}
