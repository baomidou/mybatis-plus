package com.baomidou.mybatisplus.test.replaceplaceholder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2020-06-23
 */
public interface EntityMapper extends BaseMapper<Entity> {

    @Select("select {@entity} from entity")
    List<Entity> selectAll();

    @Select("select {@entity:e} from entity e")
    List<Entity> selectAll2();
}
