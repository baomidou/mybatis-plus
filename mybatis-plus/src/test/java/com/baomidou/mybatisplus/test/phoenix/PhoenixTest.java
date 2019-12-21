package com.baomidou.mybatisplus.test.phoenix;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.test.phoenix.entity.PhoenixTestInfo;
import com.baomidou.mybatisplus.test.phoenix.mapper.PhoenixTestInfoMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.List;

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

        for (int i = 0; i < 10; i++) {
            PhoenixTestInfo testInfo = new PhoenixTestInfo()
                .setId(i)
                .setName("upsert");
            mapper.upsert(testInfo);
        }
    }

    @Test
    void a01_select() {
        int size = 10;

        List<PhoenixTestInfo> l = mapper.selectList(Wrappers.emptyWrapper());
        assert (l.size() == size);
    }


}
