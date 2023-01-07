package com.baomidou.mybatisplus.test.association;

import com.baomidou.mybatisplus.test.BaseDbTest;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yangbo
 * @since 2023-01-07
 */
public class AssociationTest extends BaseDbTest<EntityMapper> {

    @Override
    protected Class<? extends Log> logImpl() {
        return StdOutImpl.class;
    }

    @Override
    protected List<Class<?>> otherMapper() {
        return Collections.singletonList(SubEntityMapper.class);
    }

    /**
     * 测试 ParameterMap 能从关联实体对象的id中读取值。
     */
    @Test
    void testReadIdFromSubEntity() {
        long id = 123L;
        long subId = 1L;

        try (SqlSession sqlSession = sqlSession(null)) {
            EntityMapper parentMapper = sqlSession.getMapper(EntityMapper.class);
            // 主表记录
            Entity entity = new Entity();
            entity.setId(id);
            entity.setName("parent entity");
            parentMapper.insert(entity);

            // 创建2条从表记录
            SubEntityMapper subMapper = sqlSession.getMapper(SubEntityMapper.class);
            SubEntity subEntity = new SubEntity();
            subEntity.setParent(entity);
            subEntity.setId(subId);
            subEntity.setName("sub-entity1");
            subMapper.insert(subEntity);

            subEntity = new SubEntity();
            subEntity.setParent(entity);
            subEntity.setId(subId + 1);
            subEntity.setName("sub-entity2");
            subMapper.insert(subEntity);

            sqlSession.commit();
        }

        // 验证是否成功插入
        try (SqlSession sqlSession = sqlSession(null)) {
            EntityMapper parentMapper = sqlSession.getMapper(EntityMapper.class);
            // 主表记录
            Entity entity = parentMapper.selectById(id);
            assertThat(entity).isNotNull();

            // 从表记录
            SubEntityMapper subMapper = sqlSession.getMapper(SubEntityMapper.class);
            SubEntity subEntity = subMapper.selectById(subId);
            assertThat(subEntity.getParent().getId()).isNotNull();
            assertThat(subEntity.getParent().getId()).isEqualTo(123L);

            subEntity = subMapper.selectById(subId + 1);
            assertThat(subEntity.getParent().getId()).isNotNull();
            assertThat(subEntity.getParent().getId()).isEqualTo(123L);
            assertThat(subEntity.getParent().getName()).isNull();

            sqlSession.commit();
        }
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
            "drop table if exists entity",
            "drop table if exists sub_entity",
            "CREATE TABLE IF NOT EXISTS entity (" +
                "id BIGINT NOT NULL," +
                "name VARCHAR(30) NULL DEFAULT NULL," +
                "PRIMARY KEY (id))",
            "CREATE TABLE IF NOT EXISTS sub_entity (" +
                "id BIGINT NOT NULL," +
                "name VARCHAR(30) NULL DEFAULT NULL," +
                "parent_id BIGINT NOT NULL," +
                "PRIMARY KEY (id))"
        );
    }
}
