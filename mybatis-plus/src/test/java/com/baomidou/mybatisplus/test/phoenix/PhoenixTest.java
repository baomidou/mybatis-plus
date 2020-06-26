package com.baomidou.mybatisplus.test.phoenix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.injector.methods.Upsert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.phoenix.entity.PhoenixTestInfo;
import com.baomidou.mybatisplus.test.phoenix.mapper.PhoenixTestInfoMapper;
import com.mysql.cj.xdevapi.SqlStatement;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.List;

import static com.baomidou.mybatisplus.core.enums.SqlMethod.UPSERT_ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author: fly
 * Created date: 2019/12/21 16:35
 */
@DirtiesContext
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:phoenix/spring-test-phoenix.xml"})
public class PhoenixTest {

    @Resource(name = "phoenixTestInfoMapper")
    PhoenixTestInfoMapper mapper;

    @Test
    void a00_upsertForeach() {
        int size = 10;
        for (int i = 0; i < 10; i++) {
            PhoenixTestInfo testInfo = new PhoenixTestInfo()
                .setId(i)
                .setName("test");
            assertEquals (1, mapper.upsert(testInfo));
        }

        assertEquals(size, mapper.selectList(Wrappers.emptyWrapper()).size());
    }


    @Test
    void a01_upsertBatch() {
        int size = 10000;
        try (SqlSession sqlSession = SqlHelper.sqlSessionBatch(PhoenixTestInfo.class)) {
            String sqlStatement = SqlHelper.table(PhoenixTestInfo.class).getSqlStatement(UPSERT_ONE.getMethod());
            for (int i = 0; i < size; i++) {
                PhoenixTestInfo testInfo = new PhoenixTestInfo()
                    .setId(i)
                    .setName("upsertBatch");
                sqlSession.insert(sqlStatement, testInfo);
            }
            sqlSession.commit();
//            sqlSession.flushStatements();
        }
        assertEquals(size, mapper.selectList(Wrappers.emptyWrapper()).size());
    }

    @Test
    void a02_page() {
        Page<PhoenixTestInfo> page = new Page<>(2, 100);
        IPage<PhoenixTestInfo> result = mapper.selectPage(page, Wrappers.emptyWrapper());

        assertEquals(10000, result.getTotal());
        assertEquals(2, result.getCurrent());
        assertEquals(100, result.getPages());
        assertEquals(100, result.getRecords().size());

    }

}
