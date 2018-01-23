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
package com.baomidou.mybatisplus.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.test.mysql.entity.NotPK;
import com.baomidou.mybatisplus.test.mysql.entity.Test;
import com.baomidou.mybatisplus.test.mysql.mapper.NotPKMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.RoleMapper;
import com.baomidou.mybatisplus.test.mysql.mapper.TestMapper;

/**
 * <p>
 * 全局配置测试
 * </p>
 *
 * @author Caratacus
 * @Date 2016-12-22
 */
public class GlobalConfigurationTest extends CrudTest {

    /**
     * 全局配置测试
     */
    @org.junit.Test
    public void testGlobalConfig() {
        SqlSession session = this.sqlSessionFactory().openSession(false);
        TestMapper testMapper = session.getMapper(TestMapper.class);
        /*Wrapper type = Condition.instance().eq("id",1).or().in("type", new Object[]{1, 2, 3, 4, 5, 6});
        List list = testMapper.selectList(type);
        System.out.println(list.toString());*/
        Test test = new Test();
        test.setCreateTime(new Date());
        // 开启全局校验字符串会忽略空字符串
        test.setType("");
        testMapper.insert(test);

        SqlSession sqlSession = this.sqlSessionFactory().openSession(false);
        NotPKMapper pkMapper = sqlSession.getMapper(NotPKMapper.class);
        NotPK notPK = new NotPK();
        notPK.setUuid(UUID.randomUUID().toString());
        notPK.setIs(true);
        notPK.setIsis(false);
        int num = pkMapper.insert(notPK);
        Assert.assertTrue(num > 0);
        NotPK notPK1 = pkMapper.selectOne(notPK);
        Assert.assertNotNull(notPK1);
        List<NotPK> notPKS = pkMapper.selectPage(RowBounds.DEFAULT, new EntityWrapper<>(notPK));
        System.out.println(notPKS);
        Wrapper type = Condition.create().eq("type", 12121212);
        Assert.assertFalse(type.isEmptyOfWhere());
        System.out.println(type.getSqlSegment());
        Assert.assertFalse(type.isEmptyOfWhere());
        pkMapper.selectPage(RowBounds.DEFAULT, type);
        NotPK notPK2 = null;
        try {
            notPK2 = pkMapper.selectById("1");
        } catch (Exception e) {
            System.out.println("因为没有主键,所以没有注入该方法");
        }
        Assert.assertNull(notPK2);
        int count = pkMapper.selectCount(Condition.EMPTY);
        pkMapper.selectList(Condition.<NotPK>wrapper().orderBy("uuid"));
        pkMapper.selectList(Condition.<NotPK>wrapper().eq("uuid", "uuid").orderBy("uuid"));
        Assert.assertTrue(count > 0);
        int deleteCount = pkMapper.delete(null);
        Assert.assertTrue(deleteCount > 0);
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        RoleMapper mapper = sqlSession.getMapper(RoleMapper.class);
        System.out.println(mapper.selectBatchIds(list));
        System.out.println(mapper.selectBatchIds(new HashSet<>(list)));
        sqlSession.commit();
    }

}
