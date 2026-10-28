package com.baomidou.mybatisplus.test.h2.issues.genericid;

import com.baomidou.mybatisplus.test.h2.issues.genericid.entity.LongEntity;
import com.baomidou.mybatisplus.test.h2.issues.genericid.entity.StringEntity;
import com.baomidou.mybatisplus.test.h2.issues.genericid.mapper.LongEntityMapper;
import com.baomidou.mybatisplus.test.h2.issues.genericid.mapper.StringEntityMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 泛型主键问题 <br>
 * https://gitee.com/baomidou/mybatis-plus/issues/I171CQ
 *
 * @author nieqiuqiu
 * @date 2019-12-20
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:issues/genericid/spring.xml"})
@Tag("integration")
class GenericIdTest {

    @Autowired
    private LongEntityMapper longEntityMapper;

    @Autowired
    private StringEntityMapper stringEntityMapper;

    /**
     * 特征化泛型主键:同一个父类分别以 Long 和 String 做主键,MP 要按主键字段的类型生成并回填 id。
     */
    @Test
    void test() {
        LongEntity longEntity = new LongEntity("zzGenericLong");
        assertThat(longEntityMapper.insert(longEntity)).isEqualTo(1);
        // 表里的 id 列没有自增,能插进去说明主键是 MP 生成的
        assertThat(longEntity.getId()).isNotNull().isPositive();
        assertThat(longEntityMapper.selectById(longEntity.getId())).isNotNull()
                .satisfies(back -> assertThat(back.getName()).isEqualTo("zzGenericLong"));

        StringEntity stringEntity = new StringEntity("zzGenericString");
        assertThat(stringEntityMapper.insert(stringEntity)).isEqualTo(1);
        assertThat(stringEntity.getId()).isNotBlank();
        assertThat(Long.parseLong(stringEntity.getId())).as("String 主键也要拿到雪花号").isPositive();
        assertThat(stringEntityMapper.selectById(stringEntity.getId())).isNotNull()
                .satisfies(back -> assertThat(back.getName()).isEqualTo("zzGenericString"));

        StringEntity second = new StringEntity("zzGenericString2");
        assertThat(stringEntityMapper.insert(second)).isEqualTo(1);
        assertThat(second.getId()).isNotEqualTo(stringEntity.getId());

        assertThat(stringEntityMapper.deleteById(stringEntity)).isEqualTo(1);
        assertThat(stringEntityMapper.selectById(stringEntity.getId())).isNull();
        assertThat(stringEntityMapper.deleteById(second)).isEqualTo(1);
        assertThat(longEntityMapper.deleteById(longEntity)).isEqualTo(1);
        assertThat(longEntityMapper.selectById(longEntity.getId())).isNull();
        assertThat(longEntityMapper.deleteById(987654321L)).as("删一条不存在的 id").isZero();
    }

}
