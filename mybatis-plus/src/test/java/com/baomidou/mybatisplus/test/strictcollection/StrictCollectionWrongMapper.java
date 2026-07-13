package com.baomidou.mybatisplus.test.strictcollection;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * Mapper interface used to verify that strict collection type checking
 * rejects mismatched {@code ofType} declarations.
 *
 * @author nieqiurong
 * @since 3.5.18
 */
public interface StrictCollectionWrongMapper extends BaseMapper<StrictGroupEntity> {

    java.util.List<StrictGroupEntity> selectAllWithWrongUsers();
}
