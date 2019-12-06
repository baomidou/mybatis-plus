/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test.h2.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import com.baomidou.mybatisplus.core.metadata.IPage;
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


    @Select(" select test_id as testId, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
        " from h2user " +
        " where age>#{ageFrom} and age<#{ageTo} ")
    List<H2User> selectUserWithParamInSelectStatememt(Map<String, Object> param);

//    @Select(" select test_id as id, power(#{ageFrom},2), 'abc?zhazha', CAST(#{nameParam} AS VARCHAR) as name " +
//        " from h2user " +
//        " where age>#{ageFrom} and age<#{ageTo} ")
//    List<H2User> selectUserWithParamInSelectStatememt4Page(Map<String, Object> param, Page<H2User> page);
//
//    @Select(" select test_id as id, power(${ageFrom},2) as age, '${nameParam}' as name " +
//        " from h2user " +
//        " where age>#{ageFrom} and age<#{ageTo} ")
//    List<H2User> selectUserWithDollarParamInSelectStatememt4Page(Map<String, Object> param, Page<H2User> page);


    @Select("select count(1) from (" +
        "select test_id as id, CAST(#{nameParam} AS VARCHAR) as name" +
        " from h2user " +
        " where age>#{ageFrom} and age<#{ageTo} " +
        ") a")
    int selectCountWithParamInSelectItems(Map<String, Object> param);

    @Select("select age,name,count(age) from h2user group by age,name order by age")
    List<Map<?,?>> mySelectMaps(IPage<H2User> page);

    @Select("call 1")
    @Options(statementType = StatementType.CALLABLE)
    String testCall();

    @Select("select * from h2user")
    IPage<H2User> testPage1(@Param(value = "user") H2User h2User, @Param(value = "page") Page page);

    @Select("select * from h2user")
    IPage<H2User> testPage2(@Param(value = "user") Page page, @Param(value = "page") H2User h2User);
}
