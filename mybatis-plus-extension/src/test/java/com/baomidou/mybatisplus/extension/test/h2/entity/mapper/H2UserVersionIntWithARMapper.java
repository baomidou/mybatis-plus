package com.baomidou.mybatisplus.extension.test.h2.entity.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.extension.test.h2.entity.persistent.H2UserVersionIntWithAR;

/**
 * <p>
 * 这里继承自定义父类 SuperMapper
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
public interface H2UserVersionIntWithARMapper extends SuperMapper<H2UserVersionIntWithAR> {

    @Select(
        "select test_id as id, name, age, version, test_type as testType from h2user where test_id=#{id}"
    )
    public H2UserVersionIntWithAR selectByIdWithoutLogicDeleteLimit(@Param("id") Long id);

    @Update(
        "update h2user set version=1 where test_id=#{id}"
    )
    public void updateLogicDeletedRecord(@Param("id") Long id);
}
