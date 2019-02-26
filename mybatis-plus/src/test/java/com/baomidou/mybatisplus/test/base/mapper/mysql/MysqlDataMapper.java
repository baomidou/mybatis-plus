package com.baomidou.mybatisplus.test.base.mapper.mysql;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.test.base.entity.mysql.MysqlData;
import com.baomidou.mybatisplus.test.base.mapper.MyBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2018-08-18
 */
public interface MysqlDataMapper extends MyBaseMapper<MysqlData> {

    @Select("select * from mysql_data ${ew.customSqlSegment}")
    List<MysqlData> getAll(@Param(Constants.WRAPPER) Wrapper<?> wrapper);
}
