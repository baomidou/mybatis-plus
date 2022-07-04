package com.baomidou.mybatisplus.test.mappedstatement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.BaseDbTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 16K
 * @since 2022/07/03
 */

public class MappedStatementXMLTest extends BaseDbTest<EntityMapper> {

    @Test
    void mappedStatementXML(){
        doTest(it -> {
            Entity zhangSan = it.selectZhangSan();
            Entity liSi = it.selectLiSi();
            Entity wangWu = it.selectWangWu();
            Entity zhaoLiu = it.selectZhaoLiu();

            assertThat(liSi).isEqualTo(zhangSan);
            assertThat(wangWu).isNotEqualTo(zhangSan);
            assertThat(zhaoLiu).isEqualTo(zhangSan);
        });

        doTest(it -> {
            List<Entity> entities = it.selectEntityList();
            Page<Entity> entityPage = it.selectEntityPage(new Page<Entity>(0, Integer.MAX_VALUE));

            assertThat(entityPage.getRecords().size()).isEqualTo(entities.size());
        });
    }

}
