package com.baomidou.mybatisplus.test.pagecache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;

/**
 * @author miemie
 * @since 2020-06-23
 */
@CacheNamespace
public interface PageCacheMapper extends BaseMapper<PageCache> {

    @Select("<script>select * from page_cache where <if test=\"tj.name\">name is not null</if></script>")
    Page<PageCache> otherPage(Page<?> page, PageCacheTest.Tj tj);

    @Select("<script>select count(0) from page_cache where <if test=\"tj.name\">name is not null</if></script>")
    Long otherCount(PageCacheTest.Tj tj);
}
