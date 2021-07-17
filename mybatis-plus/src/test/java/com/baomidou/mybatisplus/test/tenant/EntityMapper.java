package com.baomidou.mybatisplus.test.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * @author miemie
 * @since 2020-06-24
 */
@CacheNamespace
public interface EntityMapper extends BaseMapper<Entity> {
}
