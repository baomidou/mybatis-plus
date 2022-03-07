package com.baomidou.mybatisplus.test.cache.xml;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;

/**
 * @author miemie
 * @since 2020-06-23
 */
@CacheNamespaceRef(XmlCacheMapper.class)
public interface XmlCacheMapper extends BaseMapper<XmlCache> {
}
