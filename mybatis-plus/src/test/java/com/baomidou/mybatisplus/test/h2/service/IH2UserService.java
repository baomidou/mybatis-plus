package com.baomidou.mybatisplus.test.h2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.h2.entity.H2User;

import java.util.List;
import java.util.Map;

/**
 * Service层测试
 *
 * @author hubin
 * @since 2017-01-30
 */
public interface IH2UserService extends IService<H2User> {

    int myInsert(String name, int version);

    int myInsertWithParam(String name, int version);

    int myInsertWithoutParam(String name, int version);

    int myUpdate(Long id, String name);

    List<H2User> queryWithParamInSelectStatememt(Map<String, Object> param);

    IPage<H2User> queryWithParamInSelectStatememt4Page(Map<String, Object> param, IPage<H2User> page);

    int selectCountWithParamInSelectItems(Map<String, Object> param);

    List<Map<?, ?>> mySelectMaps();

    void testBatchTransactional();

    void testSimpleTransactional();

    void testSaveOrUpdateBatchTransactional();

    void testSimpleAndBatchTransactional();

    void testSaveBatchNoTransactional1();

    void testSaveBatchNoTransactional2();

    List<H2User> testCustomSqlSegment(Wrapper wrapper);

    void testSaveOrUpdateTransactional1(List<H2User> users);

    void testSaveOrUpdateTransactional2(List<H2User> users);

}
