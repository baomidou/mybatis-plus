package com.baomidou.mybatisplus.test.autoconfigure.dynamic_strategy;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 张治保
 * @since 2023/12/29
 */
@Import(SampleUserServiceImpl.class)
@MybatisPlusTest
public class DynamicStrategySampleTest {

    @Autowired
    private SampleUserMapper sampleUserMapper;
    @Autowired
    private SampleUserService sampleUserService;


    @Test
    void testStrategyUpdate() {
        String username = "rowstop";
        LocalDateTime createTime = LocalDateTime.now();
        SampleUser sampleUser = new SampleUser()
            .setId(1)
            .setUsername(username)
            .setPassword(UUID.randomUUID().toString())
            .setCreateTime(createTime);
        //mapper test
        sampleUserMapper.insert(sampleUser);
        SampleUser user = sampleUserMapper.selectById(1);
        assertThat(user).isNotNull();
        int count = sampleUserMapper.strategyUpdateById(
            FieldStrategy.ALWAYS,
            new SampleUser().setId(1).setUsername("rowstart")
        );
        assertThat(count).isEqualTo(1);
        user = sampleUserMapper.selectById(1);
        //should be rowstart
        assertThat(user.getUsername()).isEqualTo("rowstart");
        //should be null
        assertThat(user.getPassword()).isNull();
        //FieldStrategy.NEVER should never update
        assertThat(user.getCreateTime()).isEqualTo(createTime);
        //service test
        String newPassword = UUID.randomUUID().toString();
        sampleUserService.strategyUpdateById(
            FieldStrategy.ALWAYS,
            new SampleUser().setId(1).setPassword(newPassword)
        );
        user = sampleUserService.getById(1);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isEqualTo(newPassword);
        assertThat(user.getCreateTime()).isEqualTo(createTime);

        sampleUserService.updateById(
            new SampleUser().setId(1).setUsername(username)
        );
        user = sampleUserService.getById(1);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(newPassword);
        assertThat(user.getCreateTime()).isEqualTo(createTime);

    }




}
