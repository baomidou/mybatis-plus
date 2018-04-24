package com.baomidou.mybatisplus.test.h2.entity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Addr;
import com.baomidou.mybatisplus.test.h2.entity.persistent.H2Person;

/**
 * <p>
 * 这里继承自定义父类 SuperMapper
 * </p>
 *
 * @author Caratacus
 * @date 2017/4/1
 */
public interface H2PersonCamelMapper extends SuperMapper<H2Person> {

    @Select(
        "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2person u on u.testId=a.test_id and u.testId=#{userId}"
    )
    List<H2Addr> getAddrListByUserId(@Param("userId") Long userId);

    @Select(
        "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2person u on u.testId=a.test_id and u.testId=#{userId}"
    )
    List<H2Addr> getAddrListByUserIdPage(@Param("userId") Long userId, Page<H2Addr> page);

    @Insert(
        "insert into h2person(name,version) values(#{name},#{version})"
    )
    int myInsertWithNameVersion(@Param("name") String name, @Param("version") int version);

    @Update(
        "update h2person set name=#{name} where testId=#{id}"
    )
    int myUpdateWithNameId(@Param("id") Long id, @Param("name") String name);


    @Insert(
        "insert into h2person(name,version) values( #{user1.name}, #{user1.version})"
    )
    int myInsertWithParam(@Param("user1") H2Person user1);

    @Insert(
        "insert into h2person(name,version) values( #{name}, #{version})"
    )
    int myInsertWithoutParam(H2Person user1);


    @Select(" select testId as id, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
        " from h2person " +
        " where age>#{ageFrom} and age<#{ageTo} ")
    List<H2Person> selectUserWithParamInSelectStatememt(Map<String, Object> param);

    @Select(" select testId as id, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
        " from h2person " +
        " where age>#{ageFrom} and age<#{ageTo} ")
    List<H2Person> selectUserWithParamInSelectStatememt4Page(Map<String, Object> param, Page<H2Person> page);

    @Select(" select testId as id, power(${ageFrom},2) as age, '${nameParam}' as name " +
        " from h2person " +
        " where age>#{ageFrom} and age<#{ageTo} ")
    List<H2Person> selectUserWithDollarParamInSelectStatememt4Page(Map<String, Object> param, Page<H2Person> page);


    @Select("select count(1) from (" +
        "select testId as id, CAST(#{nameParam} AS VARCHAR) as name" +
        " from h2person " +
        " where age>#{ageFrom} and age<#{ageTo} " +
        ") a")
    int selectCountWithParamInSelectItems(Map<String, Object> param);

    @Select("select * from h2person")
    List<Map> mySelectMaps();
}
