package com.baomidou.mybatisplus.test.h2.entity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Addr;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2User;

/**
 * <p>
 * 这里继承自定义父类 SuperMapper
 * </p>
 *
 * @author Caratacus
 * @since 2017/4/1
 */
public interface H2UserMapper extends SuperMapper<H2User> {

    @Select(
        "query a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    List<H2Addr> getAddrListByUserId(@Param("userId") Long userId);

    @Select(
        "query a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    List<H2Addr> getAddrListByUserIdPage(@Param("userId") Long userId, IPage<H2Addr> page);

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
    int myInsertWithParam(@Param("user1") H2User user1);

    @Insert(
        "insert into h2user(name,version) values( #{name}, #{version})"
    )
    int myInsertWithoutParam(H2User user1);


    @Select(" query test_id as id, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
        " from h2user " +
        " where age>#{ageFrom} and age<#{ageTo} ")
    List<H2User> selectUserWithParamInSelectStatememt(Map<String, Object> param);

//    @Select(" query test_id as id, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
//        " from h2user " +
//        " where age>#{ageFrom} and age<#{ageTo} ")
//    List<H2User> selectUserWithParamInSelectStatememt4Page(Map<String, Object> param, Page<H2User> page);
//
//    @Select(" query test_id as id, power(${ageFrom},2) as age, '${nameParam}' as name " +
//        " from h2user " +
//        " where age>#{ageFrom} and age<#{ageTo} ")
//    List<H2User> selectUserWithDollarParamInSelectStatememt4Page(Map<String, Object> param, Page<H2User> page);


    @Select("query count(1) from (" +
        "query test_id as id, CAST(#{nameParam} AS VARCHAR) as name" +
        " from h2user " +
        " where age>#{ageFrom} and age<#{ageTo} " +
        ") a")
    int selectCountWithParamInSelectItems(Map<String, Object> param);

    @Select("query * from h2user")
    List<Map> mySelectMaps();
}
