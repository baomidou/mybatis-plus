package com.baomidou.mybatisplus.test.base.mapper.commons;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.test.base.entity.CommonLogicData;
import com.baomidou.mybatisplus.test.base.mapper.MyBaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2018-07-06
 */
public interface CommonLogicDataMapper extends MyBaseMapper<CommonLogicData> {

    @Select("select * from common_logic_data ${ew.customSqlSegment}")
    List<CommonLogicData> getByWrapper(@Param(Constants.WRAPPER) LambdaQueryWrapper wrapper);
}
