/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;

/**
 * Service层测试
 *
 * @author hubin
 * @since 2017-01-30
 */
@Service
public class H2UserServiceImpl extends ServiceImpl<H2UserMapper, H2User> implements IH2UserService {

    @Override
    public int myInsert(String name, int version) {
        return baseMapper.myInsertWithNameVersion(name, version);
    }

    @Override
    public int myInsertWithParam(String name, int version) {
        H2User user = new H2User();
        user.setName(name);
        user.setVersion(version);
        return baseMapper.myInsertWithParam(user);
    }

    @Override
    public int myInsertWithoutParam(String name, int version) {
        H2User user = new H2User();
        user.setName(name);
        user.setVersion(version);
        return baseMapper.myInsertWithoutParam(user);
    }

    @Override
    public int myUpdate(Long id, String name) {
        return baseMapper.myUpdateWithNameId(id, name);
    }

    @Override
    public List<H2User> queryWithParamInSelectStatememt(Map<String, Object> param) {
        return baseMapper.selectUserWithParamInSelectStatememt(param);
    }

    @Override
    public IPage<H2User> queryWithParamInSelectStatememt4Page(Map<String, Object> param, IPage<H2User> page) {
        //page.setSearchCount(true);
//        userMapper.selectUserWithParamInSelectStatememt4Page(param, page);
        return page;
    }

    @Override
    public int selectCountWithParamInSelectItems(Map<String, Object> param) {
        return baseMapper.selectCountWithParamInSelectItems(param);
    }

    @Override
    public List<Map<?,?>> mySelectMaps() {
        Page<H2User> page = new Page<>(1,3);
        page.addOrder(OrderItem.asc("name"));
        return baseMapper.mySelectMaps(page);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testBatchTransactional() {
        saveBatch(Arrays.asList(new H2User("batch1", 0), new H2User("batch2", 0), new H2User("batch3", 0)));
        saveBatch(Arrays.asList(new H2User("batch4", 0), new H2User("batch5", 0), new H2User("batch6", 0)));
        throw new MybatisPlusException("测试批量插入事务回滚");
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSimpleTransactional() {
        save(new H2User("simple1", 0));
        save(new H2User("simple2", 0));
        throw new MybatisPlusException("测试普通插入事务回滚");
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSaveOrUpdateBatchTransactional() {
        saveOrUpdateBatch(Arrays.asList(new H2User("savOrUpdate1", 0), new H2User("savOrUpdate2", 0), new H2User("savOrUpdate3", 0)), 1);
        saveOrUpdateBatch(Arrays.asList(new H2User("savOrUpdate4", 0), new H2User("savOrUpdate5", 0), new H2User("savOrUpdate6", 0)), 1);
        throw new MybatisPlusException("测试普通插入事务回滚");
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSimpleAndBatchTransactional() {
        save(new H2User("simpleAndBatchTx1", 0));
        saveBatch(Arrays.asList(new H2User("simpleAndBatchTx2", 0), new H2User("simpleAndBatchTx3", 0), new H2User("simpleAndBatchTx4", 0)), 1);
        saveOrUpdateBatch(Arrays.asList(new H2User("simpleAndBatchTx5", 0), new H2User("simpleAndBatchTx6", 0), new H2User("simpleAndBatchTx7", 0)), 1);
        throw new MybatisPlusException("测试事务回滚");
    }

    @Override
    public void testSaveBatchNoTransactional1() {
        saveBatch(Arrays.asList(new H2User("testSaveBatchNoTransactional1", 0), new H2User("testSaveBatchNoTransactional1", 0), new H2User("testSaveBatchNoTransactional1", 0)), 1);
    }

    @Override
    public void testSaveBatchNoTransactional2() {
        //非事物下，制造一个批量主键冲突
        save(new H2User(1577431655447L, "testSaveBatchNoTransactional2"));
        saveBatch(Arrays.asList(new H2User("testSaveBatchNoTransactional2", 0), new H2User("testSaveBatchNoTransactional2", 0), new H2User(1577431655447L, "testSaveBatchNoTransactional2")), 1);
    }
}
