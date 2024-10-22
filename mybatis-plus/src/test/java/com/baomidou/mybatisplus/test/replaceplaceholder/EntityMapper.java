package com.baomidou.mybatisplus.test.replaceplaceholder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2020-06-23
 */
public interface EntityMapper extends BaseMapper<Entity> {

    @Select("select {@entity-name,id} from entity")
    List<Entity> selectAll();

    @Select("select {@entity:e},{@entity_sub-id:es:es} from entity e left join entity_sub es on e.id = es.id")
    List<Entity> selectAll2();
}
