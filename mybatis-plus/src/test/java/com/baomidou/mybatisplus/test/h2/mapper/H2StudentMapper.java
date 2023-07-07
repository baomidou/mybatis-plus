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
