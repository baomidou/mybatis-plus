package com.baomidou.mybatisplus.test.base.mapper.children;

import com.baomidou.mybatisplus.test.base.entity.CommonData;
import com.baomidou.mybatisplus.test.base.mapper.commons.CommonDataMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * @author miemie
 * @since 2019-04-11
 */
public interface CommonDataChildrenMapper extends CommonDataMapper {

    @Select("select * from common_data where id = #{id}")
    Optional<CommonData> getByIdChildren(Long id);
}
