/*
 * Copyright (c) 2011-2019, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.test.mysql;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.test.mysql.entity.CommonData;
import com.baomidou.mybatisplus.test.mysql.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.mysql.entity.MysqlData;
import com.baomidou.mybatisplus.test.mysql.entity.ResultMapEntity;
import com.baomidou.mybatisplus.test.mysql.enums.TestEnum;
import com.baomidou.mybatisplus.test.mysql.mapper.MysqlDataMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.children.CommonDataChildrenMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.children.CommonLogicDataChildrenMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.commons.CommonDataCopyMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.commons.CommonDataMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.commons.CommonLogicDataMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.commons.ResultMapEntityMapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Mybatis Plus mysql Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@DirtiesContext
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:mysql/spring-test-mysql.xml"})
class MysqlTestDataMapperTest {

    protected final List<String> list = Arrays.asList("1", "2", "3");
    protected final Map<String, Object> map = list.parallelStream().collect(toMap(identity(), identity()));
    private final int success = 1;
    private final int fail = 0;
    @Resource(name = "commonDataMapper")
    protected CommonDataMapper commonDataMapper;
    @Resource(name = "commonDataChildrenMapper")
    protected CommonDataChildrenMapper commonDataChildrenMapper;
    @Resource(name = "commonLogicDataMapper")
    protected CommonLogicDataMapper commonLogicDataMapper;
    @Resource(name = "commonLogicDataChildrenMapper")
    protected CommonLogicDataChildrenMapper commonLogicDataChildrenMapper;
    @Resource
    protected CommonDataCopyMapper commonDataCopyMapper;
    @Resource
    protected ResultMapEntityMapper resultMapEntityMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private MysqlDataMapper mysqlMapper;

    @Test
    void a00() {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        assertThat(configuration).isInstanceOf(MybatisConfiguration.class);
        MappedStatement mappedStatement = configuration.getMappedStatement("com.baomidou.mybatisplus.test.mysql.mapper.MysqlDataMapper.getRandomOne");
        assertThat(mappedStatement).isNotNull();
    }

    @Test
    void a01_insertForeach() {
        for (int i = 1; i < 20; i++) {
            Long id = (long) i;
            String str = String.format("第%s条数据", i);
            commonDataMapper.insert(new CommonData().setTestInt(i).setTestStr(str).setId(id)
                .setTestEnum(TestEnum.ONE));
            commonLogicDataMapper.insert(new CommonLogicData().setTestInt(i).setTestStr(str).setId(id));
            resultMapEntityMapper.insert(new ResultMapEntity().setId(id).setList(list).setMap(map).setMapp(map));
            MysqlData data = new MysqlData().setOrder(i).setGroup(i).setTestStr(str).setYaHoStr(str);
            mysqlMapper.insert(data);
            assertThat(data.getId()).isNotNull();
        }
    }

    @Test
    void a02_insertBatch() {
        int size = 9;
        List<CommonData> commonDataList = new ArrayList<>();
        List<CommonLogicData> commonLogicDataList = new ArrayList<>();
        List<MysqlData> mysqlDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String str = i + "条";
            commonDataList.add(new CommonData().setTestInt(i).setTestEnum(TestEnum.TWO).setTestStr(str));
            commonLogicDataList.add(new CommonLogicData().setTestInt(i).setTestStr(str));
            mysqlDataList.add(new MysqlData().setOrder(i).setGroup(i).setTestStr(str).setYaHoStr(str));
        }
        assertEquals(size, commonDataMapper.insertBatchSomeColumn(commonDataList));
        assertEquals(size, commonLogicDataMapper.insertBatchSomeColumn(commonLogicDataList));
        assertEquals(size, mysqlMapper.insertBatchSomeColumn(mysqlDataList));
    }

    @Test
    void a03_deleteById() {
        long id = 1L;
        assertEquals(success, commonDataMapper.deleteById(id));
        assertEquals(success, commonLogicDataMapper.deleteById(id));
        assertEquals(success, mysqlMapper.deleteById(id));
    }

    @Test
    void a04_deleteByMap() {
        long id = 2L;
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("test_int", 5);
        assertEquals(fail, commonDataMapper.deleteByMap(map));
        assertEquals(fail, commonLogicDataMapper.deleteByMap(map));
        Map<String, Object> map2 = new HashMap<>();
        map2.put("id", id);
        map2.put("`order`", 5);
        assertEquals(fail, mysqlMapper.deleteByMap(map2));
    }
}
