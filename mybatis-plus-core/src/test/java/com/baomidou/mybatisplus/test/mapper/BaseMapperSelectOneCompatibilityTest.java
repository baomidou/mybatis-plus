package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * BaseMapper.selectOne must remain list-backed so drivers without cursor support are usable.
 */
class BaseMapperSelectOneCompatibilityTest {

    @Test
    void selectOneUsesTheListBackedQueryPath() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        Object first = new Object();
        when(mapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(first));

        assertThat(mapper.selectOne(new QueryWrapper<>())).isSameAs(first);
        verify(mapper).selectList(any(QueryWrapper.class));
    }

    @Test
    void listBackedPathStillPreservesTooManyResultsContract() {
        BaseMapper<Object> mapper = Mockito.mock(BaseMapper.class, Mockito.CALLS_REAL_METHODS);
        when(mapper.selectList(any(QueryWrapper.class))).thenReturn(List.of(new Object(), new Object()));

        assertThatThrownBy(() -> mapper.selectOne(new QueryWrapper<>()))
            .isInstanceOf(TooManyResultsException.class);
    }
}
