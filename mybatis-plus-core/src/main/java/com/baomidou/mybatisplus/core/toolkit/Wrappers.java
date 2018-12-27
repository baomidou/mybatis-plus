package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.Collections;
import java.util.Map;

/**
 * <p>
 * Wrapper 条件构造
 * </p>
 *
 * @author Caratacus
 */
public final class Wrappers {

    /**
     * 空的 EmptyWrapper
     */
    private static final QueryWrapper emptyWrapper = new EmptyWrapper<>();

    private Wrappers() {
        // ignore
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> query(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取 LambdaQueryWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper<T>
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 获取 LambdaQueryWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper<T>
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery(T entity) {
        return new LambdaQueryWrapper<>(entity);
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return UpdateWrapper<T>
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return UpdateWrapper<T>
     */
    public static <T> UpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity);
    }

    /**
     * 获取 LambdaUpdateWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper<T>
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate() {
        return new LambdaUpdateWrapper<>();
    }

    /**
     * 获取 LambdaUpdateWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper<T>
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        return new LambdaUpdateWrapper<>(entity);
    }

    /**
     * 获取 EmptyWrapper<T>
     *
     * @param <T> 任意泛型
     * @return EmptyWrapper<T>
     * @see EmptyWrapper
     */
    @SuppressWarnings("unchecked")
    public static <T> QueryWrapper<T> emptyWrapper() {
        return (QueryWrapper<T>) emptyWrapper;
    }

    /**
     * 一个空的QueryWrapper子类该类不包含任何条件
     *
     * @param <T>
     * @see com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
     */
    private static class EmptyWrapper<T> extends QueryWrapper<T> {

        private static final long serialVersionUID = -2515957613998092272L;

        @Override
        public T getEntity() {
            return null;
        }

        public EmptyWrapper<T> setEntity(T entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSqlSelect() {
            return null;
        }

        @Override
        public MergeSegments getExpression() {
            return null;
        }

        @Override
        public boolean isEmptyOfWhere() {
            return true;
        }

        @Override
        public boolean isEmptyOfNormal() {
            return true;
        }

        @Override
        public boolean nonEmptyOfEntity() {
            return !isEmptyOfEntity();
        }

        @Override
        public boolean isEmptyOfEntity() {
            return true;
        }

        protected void initEntityClass() {
        }

        protected Class<T> getCheckEntityClass() {
            throw new UnsupportedOperationException();
        }

        @Override
        public EmptyWrapper<T> last(boolean condition, String lastSql) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected EmptyWrapper<T> doIt(boolean condition, ISqlSegment... sqlSegments) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("EmptyMethod")
        @Override
        public String getParamAlias() {
            return super.getParamAlias();
        }

        @Override
        public String getSqlSegment() {
            return null;
        }

        @Override
        public Map<String, Object> getParamNameValuePairs() {
            return Collections.emptyMap();
        }

        @Override
        protected String columnsToString(String... columns) {
            return null;
        }

        @Override
        protected String columnToString(String column) {
            return null;
        }

        @Override
        protected EmptyWrapper<T> instance() {
            throw new UnsupportedOperationException();
        }
    }
}
