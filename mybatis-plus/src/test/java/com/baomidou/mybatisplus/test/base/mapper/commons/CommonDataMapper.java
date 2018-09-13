package com.baomidou.mybatisplus.test.base.mapper.commons;

import java.util.List;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.mapper.MyBaseMapper;

/**
 * @author miemie
 * @since 2018/6/7
 */
public interface CommonDataMapper extends MyBaseMapper<CommonData> {

    @SqlParser(filter = true)
    @ResultType(CommonData.class)
    @Select("select * from common_data")
    List<CommonData> getAllNoTenant();
}
