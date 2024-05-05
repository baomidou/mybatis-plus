package com.baomidou.mybatisplus.test.h2.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.h2.entity.H2Addr;
import com.baomidou.mybatisplus.test.h2.entity.H2User;

/**
 * 这里继承自定义父类 SuperMapper
 *
 * @author Caratacus
 * @since 2017/4/1
 */
public interface H2UserMapper extends SuperMapper<H2User> {

    @Select(
        "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    List<H2Addr> getAddrListByUserId(@Param("userId") Long userId);

    @Select(
        "select a.addr_id as addrId, a.addr_name as addrName from h2address a" +
            " join h2user u on u.test_id=a.test_id and u.test_id=#{userId}"
    )
    List<H2Addr> getAddrListByUserIdPage(@Param("userId") Long userId, IPage<H2Addr> page);

    @Insert(
        "insert into h2user(name,version) values(#{name},#{version})"
    )
    int myInsertWithNameVersion(@Param("name") String name, @Param("version") int version);

    @Update(
        "update h2user set version=version+1, name=#{name} where test_id=#{id} and test_type=1"
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

    @Select(" select test_id as testId, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
        " from h2user " +
        " where age>#{ageFrom} and age<#{ageTo} ")
    List<H2User> selectUserWithParamInSelectStatememt(Map<String, Object> param);

    @Select("select * from h2user ${ew.customSqlSegment}")
    List<H2User> selectTestCustomSqlSegment(@Param(Constants.WRAPPER) Wrapper wrapper);

    @Select("select count(1) from (" +
        "select test_id as id, CAST(#{nameParam} AS VARCHAR) as name" +
        " from h2user " +
        " where age>#{ageFrom} and age<#{ageTo} " +
        ") a")
    int selectCountWithParamInSelectItems(Map<String, Object> param);

    @Select("select age,name,count(age) from h2user group by age,name order by age")
    List<Map<?, ?>> mySelectMaps(IPage<H2User> page);

    @Select("call 1")
    @Options(statementType = StatementType.CALLABLE)
    String testCall();

    @Select("select * from h2user")
    IPage<H2User> testPage1(@Param(value = "user") H2User h2User, @Param(value = "page") Page page);

    @Select("select * from h2user")
    IPage<H2User> testPage2(@Param(value = "user") Page page, @Param(value = "page") H2User h2User);

    @Select("select count(*) from h2user")
    Long selectCountLong();

    @Select("select * from h2user where test_id = #{id}")
    H2User getById(@Param("id") Long id);
}
