package com.baomidou.mybatisplus.test.pagination;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;

/**
 * @author miemie
 * @since 2020-06-23
 */
@CacheNamespace
public interface EntityMapper extends BaseMapper<Entity> {

    @Select("<script>select * from entity where <if test=\"tj.name\">name is not null</if></script>")
    Page<Entity> otherPage(Page<?> page, PaginationTest.Tj tj);

    @Select("<script>select count(0) from entity where <if test=\"tj.name\">name is not null</if></script>")
    Long otherCount(PaginationTest.Tj tj);
}
