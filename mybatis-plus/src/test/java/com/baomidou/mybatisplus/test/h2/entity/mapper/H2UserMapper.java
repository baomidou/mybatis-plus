package com.baomidou.mybatisplus.test.h2.entity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Addr;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;

/**
 * <p>
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
public interface H2UserMapper extends BaseMapper<H2User> {

    @Select(
            "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
                    " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    public List<H2Addr> getAddrListByUserId(@Param("userId") Long userId);

    @Select(
            "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
                    " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    public List<H2Addr> getAddrListByUserId(@Param("userId") Long userId, Page<H2Addr> page);

}
