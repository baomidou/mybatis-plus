package com.baomidou.mybatisplus.test.base.mapper.commons;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.mapper.MyBaseMapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * @author miemie
 * @since 2018/6/7
 */
public interface CommonDataMapper extends MyBaseMapper<CommonData> {

    @SqlParser(filter = true)
    @ResultType(CommonData.class)
    @Select("select * from common_data")
    List<CommonData> getAllNoTenant();

    @ResultType(CommonData.class)
    @Select("select * from common_data where id = #{ooxx}")
    Optional<CommonData> getById(Long id);

    //    @ResultType(CommonData.class)
    @Select("select * from common_data")
    IPage<CommonData> myPage(Page<CommonData> page);
}
