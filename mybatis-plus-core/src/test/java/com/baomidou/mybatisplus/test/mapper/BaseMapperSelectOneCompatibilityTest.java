package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Regression coverage for the 3.5.17 cursor-based selectOne change and its #7177 follow-up: the mapping now runs over
 * the ResultHandler path and stops as soon as a second row shows up.
 */
class BaseMapperSelectOneCompatibilityTest {

    @Test
    void selectOneReturnsTheSingleMappedRow() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        Object only = new Object();
        feedRows(mapper, List.of(only));

        assertThat(mapper.selectOne(new QueryWrapper<>())).isSameAs(only);
    }

    @Test
    void resultHandlerPathStillPreservesTooManyResultsContract() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        AtomicBoolean stopped = feedRows(mapper, List.of(new Object(), new Object()));

        assertThatThrownBy(() -> mapper.selectOne(new QueryWrapper<>())).isInstanceOf(TooManyResultsException.class);
        assertThat(stopped).as("mapping must stop once a second row shows up").isTrue();
    }

    @Test
    void selectOneReturnsFirstResultWhenThrowExIsFalse() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        Object first = new Object();
        AtomicBoolean stopped = feedRows(mapper, List.of(first, new Object()));

        assertThat(mapper.selectOne(new QueryWrapper<>(), false)).isSameAs(first);
        assertThat(stopped).as("throwEx=false must stop mapping after the second row too").isTrue();
    }

    @Test
    void selectOneReturnsNullWhenNothingIsMapped() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        feedRows(mapper, List.of());

        assertThat(mapper.selectOne(new QueryWrapper<>())).isNull();
    }

    /**
     * Feeds the given rows through the ResultHandler overload, like the real DefaultResultSetHandler would, and records
     * whether {@link ResultContext#stop()} was called.
     */
    private static AtomicBoolean feedRows(BaseMapper<Object> mapper, List<Object> rows) {
        AtomicBoolean stopped = new AtomicBoolean(false);
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            ResultHandler<Object> handler = (ResultHandler<Object>) invocation.getArgument(1);
            for (Object row : rows) {
                @SuppressWarnings("unchecked")
                ResultContext<Object> context = Mockito.mock(ResultContext.class);
                Mockito.when(context.getResultObject()).thenReturn(row);
                doAnswer(stop -> {
                    stopped.set(true);
                    return null;
                }).when(context).stop();
                handler.handleResult(context);
            }
            return null;
        }).when(mapper).selectList(any(Wrapper.class), any(ResultHandler.class));
        return stopped;
    }
}
