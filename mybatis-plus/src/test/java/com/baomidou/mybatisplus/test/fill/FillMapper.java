package com.baomidou.mybatisplus.test.fill;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author nieqiurong 2023年9月29日
 */
public interface FillMapper extends BaseMapper<FillEntity> {

    void insertBatch1(List<FillEntity> entityList);

    void insertBatch2(@Param("mpList") List<FillEntity> entityList);

    void insertBatch3(@Param("mybatisList") List<FillEntity> entityList);

    void insertBatch4(@Param("list") List<FillEntity> entityList, String a, String b, String c);

}
