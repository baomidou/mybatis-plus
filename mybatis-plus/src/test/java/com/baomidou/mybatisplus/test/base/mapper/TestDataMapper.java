package com.baomidou.mybatisplus.test.base.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.base.entity.TestData;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2018/6/7
 */
public interface TestDataMapper extends BaseMapper<TestData> {

    @SqlParser(filter = true)
    @ResultType(TestData.class)
    @Select("select * from tb_test_data")
    List<TestData> getAllNoTenant();
}
