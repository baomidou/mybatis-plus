package com.baomidou.mybatisplus.test.strictcollection;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author nieqiurong
 * @since 3.5.18
 */
@Mapper
public interface StrictCollectionMapper extends BaseMapper<StrictGroupEntity> {

    List<StrictGroupEntity> selectAllWithUsers();
}
