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
package com.baomidou.mybatisplus.test.h2.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.h2.entity.H2User;

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
}
