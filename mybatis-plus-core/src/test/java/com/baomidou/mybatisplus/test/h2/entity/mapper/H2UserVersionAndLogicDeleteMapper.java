package com.baomidou.mybatisplus.test.h2.entity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Addr;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2UserVersionAndLogicDeleteEntity;

/**
 * <p>
 * 带version和逻辑删除
 * </p>
 *
 * @author yuxiaobin
 * @date 2017/6/29
 */
public interface H2UserVersionAndLogicDeleteMapper extends BaseMapper<H2UserVersionAndLogicDeleteEntity> {


    @Select(
        "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    List<H2Addr> getAddrListByUserId(@Param("userId") Long userId);

    @Select(
        "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    List<H2Addr> getAddrListByUserIdPage(@Param("userId") Long userId, Page<H2Addr> page);

    @Insert(
        "insert into h2user(name,version) values(#{name},#{version})"
    )
    int myInsertWithNameVersion(@Param("name") String name, @Param("version") int version);

    @Update(
        "update h2user set name=#{name} where test_id=#{id}"
    )
    int myUpdateWithNameId(@Param("id") Long id, @Param("name") String name);


    @Insert(
        "insert into h2user(name,version) values( #{user1.name}, #{user1.version})"
    )
    int myInsertWithParam(@Param("user1") H2UserVersionAndLogicDeleteEntity user1);

    @Insert(
        "insert into h2user(name,version) values( #{name}, #{version})"
    )
    int myInsertWithoutParam(H2UserVersionAndLogicDeleteEntity user1);

    @Select("select test_id as id, version, last_updated_dt from h2user where test_id=#{id}")
    H2UserVersionAndLogicDeleteEntity selectMyRecordById(@Param("id") Long id);
}
