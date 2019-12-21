package com.baomidou.mybatisplus.test.phoenix.mapper;

import com.baomidou.mybatisplus.test.phoenix.PhoenixBaseMapper;
import com.baomidou.mybatisplus.test.phoenix.entity.PhoenixTestInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author fly
 * @date 2019/5/22 17:55
 * description:
 */
public interface PhoenixTestInfoMapper extends PhoenixBaseMapper<PhoenixTestInfo> {

        @Insert(
                "UPSERT INTO TEST_INFO " +
                "(ID, NAME) " +
                "VALUES(#{t.id},#{t.name})"
        )
        void addTestInfo(@Param("t") PhoenixTestInfo t);
}
