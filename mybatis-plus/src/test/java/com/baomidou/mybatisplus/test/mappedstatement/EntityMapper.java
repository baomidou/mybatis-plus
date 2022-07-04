package com.baomidou.mybatisplus.test.mappedstatement;

import com.baomidou.mybatisplus.annotation.MappedStatementXML;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author 16K
 * @since 2022/07/03
 */

public interface EntityMapper extends BaseMapper<Entity> {

    List<Entity> selectEntityList();

    @MappedStatementXML(id = "selectEntityList")
    Page<Entity> selectEntityPage(Page<Entity> page);

    Entity selectZhangSan();

    @MappedStatementXML(id = "selectZhangSan")
    Entity selectLiSi();

    @MappedStatementXML(id = "selectZhangSan")
    Entity selectWangWu();

    @MappedStatementXML(id = "selectZhangSan", overlay = true)
    Entity selectZhaoLiu();

}
