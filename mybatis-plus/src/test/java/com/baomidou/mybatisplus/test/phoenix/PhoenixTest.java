package com.baomidou.mybatisplus.test.phoenix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.phoenix.entity.PhoenixTestInfo;
import com.baomidou.mybatisplus.test.phoenix.mapper.PhoenixTestInfoMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.baomidou.mybatisplus.core.enums.SqlMethod.UPSERT_ONE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author: fly <br>
 *          Created date: 2019/12/21 16:35
 */
@DirtiesContext
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:phoenix/spring-test-phoenix.xml"})
@Tag("external-db")
public class PhoenixTest {

    @Autowired
    PhoenixTestInfoMapper mapper;

    @Test
    void a00_upsertForeach() {
        int size = 10;
        for (int i = 0; i < 10; i++) {
            PhoenixTestInfo testInfo = new PhoenixTestInfo().setId(i).setName("test");
            assertThat(mapper.upsert(testInfo)).isEqualTo(1);
        }

        assertThat(mapper.selectList(Wrappers.emptyWrapper()).size()).isEqualTo(size);
    }

    @Test
    void a01_upsertBatch() {
        int size = 10000;
        try (SqlSession sqlSession = SqlHelper.sqlSessionBatch(PhoenixTestInfo.class)) {
            String sqlStatement = SqlHelper.table(PhoenixTestInfo.class).getSqlStatement(UPSERT_ONE.getMethod());
            for (int i = 0; i < size; i++) {
                PhoenixTestInfo testInfo = new PhoenixTestInfo().setId(i).setName("upsertBatch");
                sqlSession.insert(sqlStatement, testInfo);
            }
            sqlSession.commit();
            //            sqlSession.flushStatements();
        }
        assertThat(mapper.selectList(Wrappers.emptyWrapper()).size()).isEqualTo(size);
    }

    @Test
    void a02_page() {
        Page<PhoenixTestInfo> page = new Page<>(2, 100);
        IPage<PhoenixTestInfo> result = mapper.selectPage(page, Wrappers.emptyWrapper());

        assertThat(result.getTotal()).isEqualTo(10000);
        assertThat(result.getCurrent()).isEqualTo(2);
        assertThat(result.getPages()).isEqualTo(100);
        assertThat(result.getRecords().size()).isEqualTo(100);

    }

}
