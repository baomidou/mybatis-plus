package com.baomidou.mybatisplus.test.logicdel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author miemie
 * @since 2020-06-23
 */
public interface EntityMapper extends BaseMapper<Entity> {

    @Select("select * from entity where id = #{id}")
    Entity byId(Long id);

    int testDeleteBatch(@Param(Constants.COLL) List<Entity> entityList);


}
