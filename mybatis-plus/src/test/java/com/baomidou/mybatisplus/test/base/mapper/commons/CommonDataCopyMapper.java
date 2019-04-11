package com.baomidou.mybatisplus.test.base.mapper.commons;

import com.baomidou.mybatisplus.test.base.entity.CommonData;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * @author miemie
 * @since 2019-04-11
 */
public interface CommonDataCopyMapper {

    @Select("select * from common_data where id = #{id}")
    Optional<CommonData> selectById(Long id);
}
