package com.baomidou.mybatisplus.test.autoconfigure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author miemie
 * @since 2020-05-27
 */
@Mapper
public interface SampleMapper extends BaseMapper<Sample> {

    @Select({
        "<script>",
        "SELECT count(*) from sample ",
        "<where> <if test='name != null'> name = #{name}  </if> </where>",
        "</script>"})
    Long sampleList_customCOUNT(@Param("name") String name);

    @Select({
        "<script>",
            "SELECT count(*) from sample ",
            "<where> <if test='name != null'> name = #{name}  </if> </where>",
        "</script>"})
    Long sampleList_COUNT(@Param("name") String name);


    @Select({
        "<script>",
            "SELECT * from sample ",
            "<where> <if test='name != null'> name = #{name}  </if> </where>",
        "</script>"})
    Page<Sample> sampleList(Page<Sample> page, @Param("name") String name);
}
