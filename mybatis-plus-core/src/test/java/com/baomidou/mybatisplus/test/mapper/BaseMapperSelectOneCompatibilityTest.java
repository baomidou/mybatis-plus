package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regression coverage for the 3.5.17 cursor-based selectOne change.
 */
class BaseMapperSelectOneCompatibilityTest {

    @Test
    void selectOneUsesTheListBackedQueryPath() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        Object first = new Object();
        when(mapper.selectList(any(Wrapper.class))).thenReturn(List.of(first));

        assertThat(mapper.selectOne(new QueryWrapper<>())).isSameAs(first);
        verify(mapper).selectList(any(Wrapper.class));
    }

    @Test
    void listBackedPathStillPreservesTooManyResultsContract() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        when(mapper.selectList(any(Wrapper.class))).thenReturn(List.of(new Object(), new Object()));

        assertThatThrownBy(() -> mapper.selectOne(new QueryWrapper<>()))
            .isInstanceOf(TooManyResultsException.class);
    }

    @Test
    void selectOneReturnsFirstResultWhenThrowExIsFalse() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        Object first = new Object();
        when(mapper.selectList(any(Wrapper.class))).thenReturn(List.of(first, new Object()));

        assertThat(mapper.selectOne(new QueryWrapper<>(), false)).isSameAs(first);
    }

    @Test
    void selectOneReturnsNullForAnEmptyList() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        when(mapper.selectList(any(Wrapper.class))).thenReturn(List.of());

        assertThat(mapper.selectOne(new QueryWrapper<>())).isNull();
    }
}
