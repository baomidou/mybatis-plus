package com.baomidou.mybatisplus.test.base.mapper.mysql;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.test.base.entity.mysql.MysqlData;
import com.baomidou.mybatisplus.test.base.mapper.MyBaseMapper;

/**
 * @author miemie
 * @since 2018-08-18
 */
public interface MysqlDataMapper extends MyBaseMapper<MysqlData> {

    @ResultType(MysqlData.class)
    @Select("select * from mysql_data ${ew.customSqlSegment}")
    List<MysqlData> getAll(@Param(Constants.WRAPPER) Wrapper wrapper);
}
