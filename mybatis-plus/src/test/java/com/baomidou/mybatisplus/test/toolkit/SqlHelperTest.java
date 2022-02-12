package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.BaseDbTest;
import com.baomidou.mybatisplus.test.rewrite.Entity;
import com.baomidou.mybatisplus.test.rewrite.EntityMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * SqlHelper 工具类测试
 *
 * @author <achao1441470436@gmail.com>
 * @since 2020-09-15
 */
public class SqlHelperTest extends BaseDbTest<EntityMapper> {

    @Test
    public void testGetMapper() {
        SqlSession sqlSession = SqlHelper.sqlSession(Entity.class);
        try {
            BaseMapper<Entity> mapper = SqlHelper.getMapper(Entity.class, sqlSession);
            System.out.println(mapper.selectList(Wrappers.lambdaQuery()));
        } finally {
            SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(Entity.class));
        }

    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'ruben'),(2,'a chao');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }

}
