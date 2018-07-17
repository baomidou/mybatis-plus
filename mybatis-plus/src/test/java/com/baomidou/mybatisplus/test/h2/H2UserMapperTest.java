package com.baomidou.mybatisplus.test.h2;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.TestData;
import com.baomidou.mybatisplus.test.h2.config.H2Db;
import com.baomidou.mybatisplus.test.h2.entity.mapper.H2UserMapper;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;

/**
 * <p>
 * Mybatis Plus H2 Junit Test
 * </p>
 *
 * @author hubin
 * @since 2018-06-05
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
public class H2UserMapperTest extends BaseTest {

    @Resource
    protected H2UserMapper userMapper;

    @BeforeClass
    public static void InitDB() throws SQLException, IOException {
        H2Db.initH2User();
    }

    @Test
    public void crudTest() {
        H2User h2User = new H2User();
        h2User.setName(NQQ);
        h2User.setAge(1);
        h2User.setDesc("这是一个不错的小伙子");
        Assert.assertTrue(1 == userMapper.insert(h2User));

        log(h2User.getTestId());

        // 新增一条自定义 ID = 1 的测试删除数据
        h2User.setTestId(1L);
        h2User.setName("测试");
        userMapper.insert(h2User);
        for (int i = 0; i < 10; i++) {
            userMapper.insert(new H2User("mp" + i, i));
        }
        Assert.assertTrue(1 == userMapper.deleteById(1L));

        Map<String, Object> map = new HashMap<>();
        map.put("name", "mp0");
        map.put("age", 0);

        // 根据 map 查询
        h2User = userMapper.selectByMap(map).get(0);
        Assert.assertTrue(0 == h2User.getAge());

        // 根据 map 删除
        Assert.assertTrue(1 == userMapper.deleteByMap(map));

        // 查询列表
        Wrapper<H2User> wrapper = new QueryWrapper<H2User>().lambda().like(H2User::getName, "mp");
        log(wrapper.getSqlSegment());

        List<H2User> h2UserList = userMapper.selectList(wrapper);
        Assert.assertTrue(CollectionUtils.isNotEmpty(h2UserList));

        // 查询总数
        int count = userMapper.selectCount(wrapper.clone());
        Assert.assertTrue(count > 1);

        // 批量删除
        Assert.assertTrue(count == userMapper.deleteBatchIds(h2UserList
            .stream().map(u -> u.getTestId()).collect(toList())));

        // 更新
        h2User = new H2User();
        h2User.setAge(2);
        h2User.setDesc("测试置空");
        Assert.assertTrue(1 == userMapper.update(h2User,
            new QueryWrapper<H2User>().eq("name", NQQ)));

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(2))));

        h2User.setAge(3);
        h2User.setDesc(null);
        Assert.assertTrue(userMapper.update(h2User,
            new UpdateWrapper<H2User>().lambda()
                .set(H2User::getDesc, "")
                .eq(H2User::getName, "Jerry")) > 0);

        log(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ).setAge(3))));

        // 根据主键更新 age = 18
        h2User.setAge(18);
        Assert.assertNotNull(1 == userMapper.updateById(h2User));

        // 查询一条记录
        Assert.assertNotNull(userMapper.selectOne(new QueryWrapper<>(new H2User().setName(NQQ))));

        log(h2User.toString());

        // 分页查询
        IPage<H2User> h2UserPage = userMapper.selectPage(new Page<>(1, 10), null);
        if (null != h2UserPage) {
            System.out.println(h2UserPage.getTotal());
            System.out.println(h2UserPage.getSize());
        }
        Assert.assertTrue(null != userMapper.selectPage(new Page<>(1, 10),
            new QueryWrapper<H2User>().orderByAsc("name")));

        // 查询结果集，测试 lambda 对象后 QueryWrapper 是否参数继续传递
        QueryWrapper<H2User> qw = new QueryWrapper<>();
        qw.lambda().eq(H2User::getName, NQQ);
        List<Map<String, Object>> mapList = userMapper.selectMaps(qw);
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map m : mapList) {
                System.out.println(m);
            }
        }
        Assert.assertTrue(CollectionUtils.isNotEmpty(userMapper.selectMaps(new QueryWrapper<>(new H2User().setAge(18)))));
    }


    @Test
    public void update() {
        UpdateWrapper uw = new UpdateWrapper<TestData>();
        uw.set("age", 1);
        uw.eq("test_id", 101L);
        userMapper.update(new H2User().setName("咩咩"), uw);
    }


    @Test
    public void delete() {
        userMapper.delete(new QueryWrapper<>(new H2User().setAge(2))
            .eq("name", "Tony"));
    }
}
