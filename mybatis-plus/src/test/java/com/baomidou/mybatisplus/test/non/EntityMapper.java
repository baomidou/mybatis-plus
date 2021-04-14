package com.baomidou.mybatisplus.test.non;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author miemie
 * @since 2020-06-23
 */
public interface EntityMapper extends BaseMapper<Entity> {

    @Select("select * from entity where id = #{id}")
    Entity byId(Long id);
}
