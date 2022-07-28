package com.baomidou.mybatisplus.test.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseMapperTest  extends BaseDbTest<EntityMapper> {

    public EntityMapper entityMapper;


    @BeforeEach
    public void before(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        entityMapper = sqlSession.getMapper(EntityMapper.class);
    }

    @Test
    void test(){
        // 测试 existsById 方法
        assertThat(entityMapper.existsById(1L)).isTrue();
        assertThat(entityMapper.existsById(4L)).isFalse();

        // 测试 exists 方法
        LambdaQueryWrapper<Entity> wrapper = Wrappers.<Entity>lambdaQuery().eq(Entity::getName, "老王");
        assertThat(entityMapper.exists(wrapper)).isTrue();

        LambdaQueryWrapper<Entity> wrapper2 = Wrappers.<Entity>lambdaQuery().eq(Entity::getName, "test");
        assertThat(entityMapper.exists(wrapper2)).isFalse();
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'老王'),(2,'老李'),(3,'老赵')";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity",
            "CREATE TABLE IF NOT EXISTS entity (\n" +
                "id BIGINT(20) NOT NULL,\n" +
                "name VARCHAR(30) NULL DEFAULT NULL,\n" +
                "version integer NOT NULL DEFAULT 0,\n" +
                "PRIMARY KEY (id)" +
                ")");
    }

}
