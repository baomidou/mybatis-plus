/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
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

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;
import com.baomidou.mybatisplus.test.h2.service.IH2UserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Service层测试
 * </p>
 *
 * @author hubin
 * @since 2017-01-30
 */
@Service
public class H2UserServiceImpl extends ServiceImpl<H2UserMapper, H2User> implements IH2UserService {

    @Autowired
    private H2UserMapper userMapper;
    
    @Override
    public int myInsert(String name, int version) {
        return userMapper.myInsertWithNameVersion(name, version);
    }

    @Override
    public int myInsertWithParam(String name, int version) {
        H2User user = new H2User();
        user.setName(name);
        user.setVersion(version);
        return userMapper.myInsertWithParam(user);
    }

    @Override
    public int myInsertWithoutParam(String name, int version) {
        H2User user = new H2User();
        user.setName(name);
        user.setVersion(version);
        return userMapper.myInsertWithoutParam(user);
    }

    @Override
    public int myUpdate(Long id, String name) {
        return userMapper.myUpdateWithNameId(id, name);
    }

    @Override
    public List<H2User> queryWithParamInSelectStatememt(Map<String, Object> param) {
        return userMapper.selectUserWithParamInSelectStatememt(param);
    }

    @Override
    public IPage<H2User> queryWithParamInSelectStatememt4Page(Map<String, Object> param, IPage<H2User> page) {
        //page.setSearchCount(true);
//        userMapper.selectUserWithParamInSelectStatememt4Page(param, page);
        return page;
    }

    @Override
    public int selectCountWithParamInSelectItems(Map<String, Object> param) {
        return userMapper.selectCountWithParamInSelectItems(param);
    }

    @Override
    public List<Map> mySelectMaps() {
        return userMapper.mySelectMaps();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testBatchTransactional() {
        saveBatch(Arrays.asList(new H2User("batch1",0),new H2User("batch2",0),new H2User("batch3",0)));
        saveBatch(Arrays.asList(new H2User("batch4",0),new H2User("batch5",0),new H2User("batch6",0)));
        throw new MybatisPlusException("测试批量插入事务回滚");
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSimpleTransactional() {
        save(new H2User("simple1",0));
        save(new H2User("simple2",0));
        throw new MybatisPlusException("测试普通插入事务回滚");
    }
    
    
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSaveOrUpdateBatchTransactional() {
        saveOrUpdateBatch(Arrays.asList(new H2User("savOrUpdate1",0),new H2User("savOrUpdate2",0),new H2User("savOrUpdate3",0)),1);
        saveOrUpdateBatch(Arrays.asList(new H2User("savOrUpdate4",0),new H2User("savOrUpdate5",0),new H2User("savOrUpdate6",0)),1);
        throw new MybatisPlusException("测试普通插入事务回滚");
    }
    
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void testSimpleAndBatchTransactional() {
        save(new H2User("simpleAndBatchTx1",0));
        saveBatch(Arrays.asList(new H2User("simpleAndBatchTx2",0),new H2User("simpleAndBatchTx3",0),new H2User("simpleAndBatchTx4",0)),1);
        saveOrUpdateBatch(Arrays.asList(new H2User("simpleAndBatchTx5",0),new H2User("simpleAndBatchTx6",0),new H2User("simpleAndBatchTx7",0)),1);
        throw new MybatisPlusException("测试事务回滚");
    }
}
