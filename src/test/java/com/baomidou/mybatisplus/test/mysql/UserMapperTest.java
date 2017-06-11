/**
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
package com.baomidou.mybatisplus.test.mysql;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.test.mysql.entity.Role;
import com.baomidou.mybatisplus.test.mysql.entity.User;
import com.baomidou.mybatisplus.test.mysql.mapper.UserMapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * <p>
 * MybatisPlus 测试类
 * </p>
 * <p>
 * 自动提交了事务问题：<br>
 * http://www.mybatis.org/spring/transactions.html#programmatic<br>
 * https://github.com/mybatis/spring/issues/39<br>
 * </p>
 *
 * @author hubin sjy
 * @Date 2016-01-23
 */
public class UserMapperTest {

    /**
     * RUN 测试
     * <p>
     * <p>
     * MybatisPlus 加载 SQL 顺序：
     * </p>
     * 1、加载XML中的SQL<br>
     * 2、加载sqlProvider中的SQL<br>
     * 3、xmlSql 与 sqlProvider不能包含相同的SQL<br>
     * <br>
     * 调整后的SQL优先级：xmlSql > sqlProvider > crudSql <br>
     */
    public static void main(String[] args) {

        // 加载配置文件
        InputStream in = UserMapperTest.class.getClassLoader().getResourceAsStream("mysql-config.xml");

		/*
         * 此处采用 MybatisSessionFactoryBuilder 构建
		 * SqlSessionFactory，目的是引入BaseMapper功能
		 */
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();

		/*
		 * 1、数据库字段驼峰命名不需要任何设置 2、当前演示是驼峰下划线混合命名 3、如下开启，表示数据库字段使用下划线命名，该设置是全局的。
		 * 开启该设置实体可无 @TableId(value = "test_id") 字段映射
		 */
        // mf.setDbColumnUnderline(true);

        /**
         * 设置，自定义 SQL 注入器
         */
        GlobalConfiguration gc = new GlobalConfiguration(new MySqlInjector());
        /**
         * 设置，自定义 元对象填充器，实现公共字段自动写入
         */
        gc.setMetaObjectHandler(new MyMetaObjectHandler());
        // gc.setCapitalMode(true);
        gc.setDbColumnUnderline(true);
        mf.setGlobalConfig(gc);

        SqlSessionFactory sessionFactory = mf.build(in);
        SqlSession session = sessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        System.err.println(" debug run 查询执行 user 表数据变化！ ");
        userMapper.deleteAll();

        /**
         * sjy 测试@TableField的el属性
         */
        Role role = new Role();
        role.setId(IdWorker.getId());
        role.setName("admin");

        User userA = new User();
        userA.setId(IdWorker.getId());
        userA.setName("junyu_shi");
        userA.setAge(1);
        userA.setDesc("测试一把");
        userA.setTestType(1);
        userA.setRole(role);

        int rlt = userMapper.insert(userA);
        User whereUser = userMapper.selectOne(userA);
        print(whereUser);

        userA.setAge(18);
        userMapper.updateById(userA);
        userMapper.delete(new EntityWrapper<>(userA));
        System.err.println("--------- @TableField el() --------- " + rlt);

        /**
         * 注解插件测试
         */
        rlt = userMapper.insertInjector(new User(1L, "1", 1, 1));
        System.err.println("--------- insertInjector --------- " + rlt);

        /**
         * ehcache 缓存测试
         */
        User cacheUser = userMapper.selectOne(new User(1L, 1));
        print(cacheUser);
        cacheUser = userMapper.selectOne(new User(1L, 1));
        print(cacheUser);

        /**
         * 插入
         */
        long id = IdWorker.getId();
        rlt = userMapper.insert(new User(id, "abc", 18, 1));
        System.err.println("\n--------------insert-------" + rlt);
        sleep();

        rlt = userMapper.insert(new User(18));
        System.err.println("\n----------测试 name 字段忽略验证----insert-------" + rlt);
        sleep();

        List<User> ul = new ArrayList<>();

		/* 手动输入 ID */
        ul.add(new User(11L, "1", 1, 0));
        ul.add(new User(12L, "2", 2, 1));
        ul.add(new User(13L, "3", 3, 1));
        ul.add(new User(14L, "delname", 4, 0));
        ul.add(new User(15L, "5", 5, 1));
        ul.add(new User(16L, "6", 6, 0));

		/* 测试 name test_type 填充 */
        ul.add(new User(17L, 7));
        ul.add(new User(18L, 8));
        ul.add(new User(19L, 9));
        ul.add(new User(7));
        ul.add(new User(20L, "deleteByMap", 7, 0));

		/* 使用 ID_WORKER 自动生成 ID */
        ul.add(new User("8", 8, 1));
        ul.add(new User("9", 9, 1));
        for (User u : ul) {
            rlt = userMapper.insert(u);
        }
        System.err.println("\n--------------insertBatch----------------" + rlt + "\n\n");

        /**
         * 提交，往下操作在一个事物中！！！
         */
        session.commit();

		/*
		 * 删除
		 */
        rlt = userMapper.deleteById(id);
        System.err.println("---------deleteById------- delete id=" + id + " ,result=" + rlt + "\n\n");
        sleep();

        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("name", "deleteByMap");
        columnMap.put("age", null);
        rlt = userMapper.deleteByMap(columnMap);
        System.err.println("---------deleteByMap------- result=" + rlt + "\n\n");
        sleep();

        List<Long> il = new ArrayList<>();
        il.add(16L);
        il.add(17L);
        rlt = userMapper.deleteBatchIds(il);
        System.err.println("---------deleteBatchIds------- delete id=" + id + " ,result=" + rlt + "\n\n");
        sleep();

        rlt = userMapper.delete(new EntityWrapper<>(new User(14L, "delname")));
        System.err.println("--------------delete------------------ result=" + rlt + "\n\n");
        sleep();

		/*
		 * <p> 修改 </p>
		 *
		 * updateById 是从 BaseMapper 中继承而来的，UserMapper.xml中并没有申明改sql
		 */
        rlt = userMapper.updateById(new User(12L, "MybatisPlus"));
        System.err.println("------------------updateById---------------------- result=" + rlt + "\n\n");
        sleep();

        rlt = userMapper.updateById(new User(12L, "update all column", 12, 12));
        System.err.println("------------------updateById---------------------- result=" + rlt + "\n\n");
        sleep();

        rlt = userMapper.update(new User("55", 55, 5), new EntityWrapper<>(new User(15L, "5")));
        System.err.println("------------------update---------------------- result=" + rlt + "\n\n");
        sleep();

        EntityWrapper<User> ew1 = new EntityWrapper<>();
        ew1.addFilter("test_id={0} AND name={1}", 15L, "55");
        rlt = userMapper.update(new User("00"), ew1);
        System.err.println("------------------update---------------------- result=" + rlt + "\n\n");
        sleep();

		/* 无条件选择更新 */
        // userMapper.update(new User("11"), null);

        List<User> userList = new ArrayList<>();
        userList.add(new User(11L, "updateBatchById-1", 1, 1));
        userList.add(new User(12L, "updateBatchById-2", 2, 1));
        userList.add(new User(13L, "updateBatchById-3", 3, 1));
        for (User u : userList) {
            rlt = userMapper.updateById(u);
        }
        System.err.println("------------------updateBatchById---------------------- result=" + rlt + "\n\n");
        sleep();

		/*
		 * <p> 查询 </p>
		 */
        System.err.println("\n------------------selectById----------------------");
        User user = userMapper.selectById(12L);
        print(user);

        System.err.println("\n------------------selectBatchIds----------------------");
        List<Long> idList = new ArrayList<>();
        idList.add(11L);
        idList.add(12L);
        List<User> ul0 = userMapper.selectBatchIds(idList);
        for (User anUl0 : ul0) {
            print(anUl0);
        }

        System.err.println("\n------------------selectByMap-----满足 map 条件的数据----");
        Map<String, Object> cm = new HashMap<>();
        cm.put("test_type", 1);
        cm.put("1", 1);
        List<User> ul1 = userMapper.selectByMap(cm);
        for (User anUl1 : ul1) {
            print(anUl1);
        }

        System.err.println("\n------------------selectOne----------------------");
        User userOne = userMapper.selectOne(new User("abc"));
        print(userOne);

        System.err.println("\n------------------selectCount----------------------");
        System.err.println("查询 type=1 总记录数：" + userMapper.selectCount(new EntityWrapper<>(new User(1))));
        System.err.println("总记录数：" + userMapper.selectCount(null));

        System.err.println("\n------------------selectList-----所有数据----id--DESC--排序----");
        List<User> ul2 = userMapper.selectList(new EntityWrapper<User>(null, "age,name"));
        for (User anUl2 : ul2) {
            print(anUl2);
        }

        System.err.println("\n------------------list 分页查询 ----查询 testType = 1 的所有数据--id--DESC--排序--------");
        Page<User> page = new Page<>(1, 2);
        EntityWrapper<User> ew = new EntityWrapper<>(new User(1));

		/*
		 * 查询字段
		 */
        ew.setSqlSelect("age,name");

		/*
		 * 查询条件，SQL 片段(根据常用的写SQL的方式按顺序添加相关条件即可)
		 */
        ew.where("name like {0}", "'%dateBatch%'").and("age={0}", 3).orderBy("age,name", true);
        List<User> paginList = userMapper.selectPage(page, ew);
        page.setRecords(paginList);
        for (int i = 0; i < page.getRecords().size(); i++) {
            print(page.getRecords().get(i));
        }
        System.err.println(" 翻页：" + page.toString());

        System.err.println("\n---------------xml---selectListRow 分页查询，不查询总数（此时可自定义 count 查询）----无查询条件--------------");
        // TODO 查询总数传 Page 对象即可
        List<User> rowList = userMapper.selectListRow(new Pagination(0, 2, false));
        for (User aRowList : rowList) {
            print(aRowList);
        }

		/*
		 * 用户列表
		 */
        System.err.println(" selectList EntityWrapper == null \n");
        paginList = userMapper.selectList(null);
        for (User aPaginList : paginList) {
            print(aPaginList);
        }

        /**
         * 自定义方法，删除测试数据
         */
        rlt = userMapper.deleteAll();
        System.err.println("清空测试数据！ rlt=" + rlt);

        /**
         * 提交
         */
        session.commit();
    }

    /*
     * 打印测试信息
     */
    private static void print(User user) {
        sleep();
        if (user != null) {
            System.out.println("\n user: id=" + user.getId() + ", name=" + user.getName() + ", age=" + user.getAge()
                    + ", testType=" + user.getTestType());
        } else {
            System.out.println("\n user is null.");
        }
    }

    /*
     * 慢点打印
     */
    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
