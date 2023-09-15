package com.baomidou.mybatisplus.core.batch;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 当使用Batch混合查询时,每次都会将原来的结果集清空,建议使用Batch时就不要混合使用select了 (后面看看要不要改成动态代理把...)
 *
 * @author nieqiurong
 * @since 3.5.4
 */
public class BatchSqlSession {

    private final SqlSession sqlSession;

    private final List<BatchResult> resultBatchList = new ArrayList<>();

    public BatchSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public <T> T selectOne(String statement) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectOne(statement);
    }

    public <T> T selectOne(String statement, Object parameter) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectOne(statement, parameter);
    }

    public <E> List<E> selectList(String statement) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectList(statement);
    }

    public <E> List<E> selectList(String statement, Object parameter) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectList(statement, parameter);
    }

    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectList(statement, parameter, rowBounds);
    }

    public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectMap(statement, mapKey);
    }

    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectMap(statement, parameter, mapKey);
    }

    public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
        resultBatchList.addAll(sqlSession.flushStatements());
        return sqlSession.selectMap(statement, parameter, mapKey, rowBounds);
    }

    public List<BatchResult> getResultBatchList() {
        return resultBatchList;
    }

}
