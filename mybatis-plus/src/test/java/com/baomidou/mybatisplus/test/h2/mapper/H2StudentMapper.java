package com.baomidou.mybatisplus.test.h2.mapper;

import com.baomidou.mybatisplus.test.h2.entity.H2Student;
import com.baomidou.mybatisplus.test.h2.entity.H2User;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 学生Mapper层
 * @author nieqiurong 2018/7/27.
 */
public interface H2StudentMapper extends SuperMapper<H2Student> {

    long insertFillByCustomMethod1(H2User h2User);

    long insertFillByCustomMethod2(@Param("et") H2User h2User);

    long insertFillByCustomMethod3(@Param("et") H2User h2User, @Param("test") String test);

    long insertFillByCustomMethod4(Collection<H2User> h2User);

    long insertFillByCustomMethod5(@Param("collection") Collection<H2User> h2User);

    long insertFillByCustomMethod6(@Param("coll") Collection<H2User> h2User);

    long insertFillByCustomMethod7(@Param("list") List<H2User> h2User);

    long insertFillByCustomMethod8(H2User[] h2Users);

    long insertFillByCustomMethod9(@Param("array") H2User[] h2Users);

    long insertFillByCustomMethod10(Map<String, Object> paramMap);

    long insertFillByCustomMethod11(Map<String, Object> paramMap);

    long insertFillByCustomMethod12(Map<String, Object> paramMap);

    long insertFillByCustomMethod13(Map<String, Object> paramMap);

    long updateFillByCustomMethod1(Map<String, Object> paramMap);

    long updateFillByCustomMethod2(@Param("coll") Collection<Long> ids, @Param("et") H2User h2User);

    long updateFillByCustomMethod3(@Param("coll") Collection<Long> ids, @Param("user") H2User h2User);

    long updateFillByCustomMethod4(@Param("colls") Collection<Long> ids, @Param("et") H2User h2User);

}
