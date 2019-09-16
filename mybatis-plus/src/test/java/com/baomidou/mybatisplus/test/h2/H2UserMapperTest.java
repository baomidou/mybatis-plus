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
package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import com.baomidou.mybatisplus.test.h2.entity.SuperEntity;
import com.baomidou.mybatisplus.test.h2.enums.AgeEnum;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author hubin
 * @since 2018-06-05
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserMapperTest extends BaseTest {

    @Resource
    protected H2UserMapper userMapper;

    @Test
    @Order(1)
    void crudTest() {
        H2User h2User = new H2User();
        h2User.setName(NQQ);
        h2User.setAge(AgeEnum.ONE);
        h2User.setDeleted(0);
        h2User.setDesc("这是一个不错的小伙子");
        h2User.setTestType(1);
        Assertions.assertEquals(1, userMapper.insert(h2User));

        log(h2User.getTestId());

        // 新增一条自定义 ID = 1 的测试删除数据
        h2User.setTestId(1L);
        h2User.setName("测试");
        userMapper.insert(h2User);
        for (int i = 0; i < 10; i++) {
            userMapper.insert(new H2User("mp" + i, AgeEnum.ONE));
        }
        Assertions.assertEquals(1, userMapper.deleteById(1L));

        Map<String, Object> map = new HashMap<>();
        map.put("name", "mp0");
        map.put("age", AgeEnum.ONE);

        // 根据 map 查询
        h2User = userMapper.selectByMap(map).get(0);
        Assertions.assertSame(AgeEnum.ONE, h2User.getAge());

        // 根据 map 删除
        Assertions.assertEquals(1, userMapper.deleteByMap(map));

        // 查询列表
        LambdaQueryWrapper<H2User> wrapper = new QueryWrapper<H2User>().lambda().like(H2User::getName, "mp");
        log(wrapper.getSqlSegment());

        List<H2User> h2UserList = userMapper.selectList(wrapper);
        Assertions.assertTrue(CollectionUtils.isNotEmpty(h2UserList));

        // 查询总数
        int count = userMapper.selectCount(wrapper.clone());
        Assertions.assertTrue(count > 1);

        // 批量删除
        Assertions.assertEquals(count, userMapper.deleteBatchIds(h2UserList.stream().map(SuperEntity::getTestId).collect(toList())));

        // 更新
        h2User = new H2User();
        h2User.setAge(AgeEnum.TWO);
        h2User.setDesc("测试置空");
        Assertions.assertEquals(1, userMapper.update(h2User, new QueryWrapper<H2User>().eq("name", NQQ)));

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(AgeEnum.TWO))));

        h2User.setAge(AgeEnum.THREE);
        h2User.setDesc(null);
        Assertions.assertTrue(userMapper.update(h2User,
            new UpdateWrapper<H2User>().lambda()
                .set(H2User::getDesc, "")
                .eq(H2User::getName, "Jerry")) > 0);

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(AgeEnum.THREE))));

        Assertions.assertEquals(1, userMapper.insert(h2User));
        // 根据主键更新 age = 18
        h2User.setAge(AgeEnum.TWO);
        Assertions.assertEquals(1, userMapper.updateById(h2User));
        long testId = h2User.getTestId();
        // https://github.com/baomidou/mybatis-plus/issues/299
        Assertions.assertEquals(1, userMapper.updateById(new H2User() {{
            setTestId(testId);
            setAge(AgeEnum.TWO);
        }}));
        // 查询一条记录
        Assertions.assertNotNull(userMapper.selectOne(new QueryWrapper<>(new H2User().setName("Joe").setTestType(1))));

        log(h2User.toString());

        // 分页查询
        IPage<H2User> h2UserPage = userMapper.selectPage(new Page<>(1, 10), null);
        if (null != h2UserPage) {
            System.out.println(h2UserPage.getTotal());
            System.out.println(h2UserPage.getSize());
        }
        Assertions.assertNotNull(userMapper.selectPage(new Page<>(1, 10), new QueryWrapper<H2User>().orderByAsc("name")));

        // 查询结果集，测试 lambda 对象后 QueryWrapper 是否参数继续传递
        QueryWrapper<H2User> qw = new QueryWrapper<>();
        qw.lambda().eq(H2User::getName, NQQ);
        List<Map<String, Object>> mapList = userMapper.selectMaps(qw);
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> m : mapList) {
                System.out.println(m);
            }
        }
        Assertions.assertTrue(CollectionUtils.isNotEmpty(userMapper.selectMaps(new QueryWrapper<>(new H2User().setTestType(3)))));

        // 测试自定义注入方法
        h2User.setDesc("");
        h2User.setTestDate(new Date());
        Assertions.assertTrue(userMapper.alwaysUpdateSomeColumnById(h2User) > 0);
        Assertions.assertEquals("", userMapper.selectById(h2User.getTestId()).getDesc());
    }

    @Test
    void testCall() {
        Assertions.assertEquals("1", userMapper.testCall());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void delete() {
        userMapper.delete(new QueryWrapper<>(new H2User().setAge(AgeEnum.TWO))
            .eq("name", "Tony"));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    void sqlCommentTest() {
        userMapper.delete(new QueryWrapper<H2User>().comment("deleteAllUsers"));
        String name = "name1", nameNew = "name1New";
        int insertCount = userMapper.insert(new H2User().setName(name).setAge(AgeEnum.ONE));
        Assertions.assertEquals(1, insertCount);
        int updateCount = userMapper.update(new H2User(),
            new UpdateWrapper<H2User>().comment("updateUserName1").lambda()
                .set(H2User::getName, nameNew)
                .eq(H2User::getName, name)
        );
        Assertions.assertEquals(1, updateCount);
        H2User h2User = userMapper.selectOne(
            new QueryWrapper<H2User>().lambda().comment("getUserByUniqueName")
                .eq(H2User::getName, nameNew)
        );
        Assertions.assertNotNull(h2User);
        LambdaQueryWrapper<H2User> queryWrapper = new QueryWrapper<H2User>().lambda().ge(H2User::getAge, 1);
        int userCount = userMapper.selectCount(queryWrapper.comment("getUserCount"));
        Assertions.assertEquals(1, userCount);
        List<H2User> h2UserList = userMapper.selectList(queryWrapper.comment("getUserList"));
        Assertions.assertEquals(1, h2UserList.size());
        IPage<H2User> h2UserIPage = userMapper.selectPage(new Page<>(1, 10), queryWrapper.comment("getUserPage"));
        Assertions.assertEquals(1, h2UserIPage.getRecords().size());
        List<Map<String, Object>> selectMaps = userMapper.selectMaps(queryWrapper.comment("getUserMaps"));
        Assertions.assertEquals(1, selectMaps.size());
        IPage<Map<String, Object>> selectMapsPage = userMapper.selectMapsPage(new Page<>(1, 10), queryWrapper.comment("getUserMapsPage"));
        Assertions.assertEquals(1, selectMapsPage.getRecords().size());
        List<Object> selectObjs = userMapper.selectObjs(queryWrapper.comment("getUserObjs"));
        Assertions.assertEquals(1, selectObjs.size());
    }

    @Test
    void test(){
        Page<H2User> page = new Page<>();
        userMapper.testPage1(new H2User(), page);
        userMapper.testPage2(page, new H2User());
    }
}
