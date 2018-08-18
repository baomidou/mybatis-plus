package com.baomidou.mybatisplus.test.base.mapper.commons;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2018/6/7
 */
public interface CommonDataMapper extends BaseMapper<CommonData> {

    @SqlParser(filter = true)
    @ResultType(CommonData.class)
    @Select("select * from common_data")
    List<CommonData> getAllNoTenant();
}
