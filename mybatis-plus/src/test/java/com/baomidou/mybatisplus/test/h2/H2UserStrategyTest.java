package com.baomidou.mybatisplus.test.h2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.test.h2.entity.H2UserStrategy;
import com.baomidou.mybatisplus.test.h2.mapper.H2UserStrategyMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mybatis Plus H2 Junit Test
 *
 * @author Caratacus
 * @since 2017/4/1
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:h2/spring-test-h2.xml"})
class H2UserStrategyTest extends H2TestSupport {

    @Autowired
    protected H2UserStrategyMapper userStrategyMapper;

    @Test
    @Order(1)
    void testNewlyAdded3Strategy() {
        H2UserStrategy insertUser = new H2UserStrategy();
        insertUser.setName("userStrategy").setDesc("updateStrategy=IGNORE").setVersion(1);
        int row = userStrategyMapper.insert(insertUser);
        Long id = insertUser.getTestId();
        assertThat(row).isEqualTo(1);
        assertThat(userStrategyMapper.selectById(id).getTestType()).as("autofilled with 3").isEqualTo(3);

        QueryWrapper<H2UserStrategy> wrapper = new QueryWrapper<>(
                new H2UserStrategy().setDesc("updateStrategy=IGNORE").setTestType(3));
        assertThat(userStrategyMapper.selectCount(wrapper))
                .as("name is whereStrategy=IGNORE, so should have where name=null which cause count=0").isEqualTo(0);

        H2UserStrategy updateUser = new H2UserStrategy().setName("update");
        updateUser.setTestId(id);
        assertThat(userStrategyMapper.updateById(updateUser)).isEqualTo(1);

        H2UserStrategy selectUser = userStrategyMapper.selectById(id);
        assertThat(selectUser.getName()).isEqualTo("update");
        assertThat(selectUser.getDesc())
                .as("desc is updateStrategy=IGNORE, so should have set desc=null when updateById").isNull();
        assertThat(selectUser.getTestType())
                .as("handle: strategy=IGNORED, should be set test_type=null when updateById ").isNull();

    }

}
