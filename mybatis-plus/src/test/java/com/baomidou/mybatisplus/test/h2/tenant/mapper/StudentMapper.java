package com.baomidou.mybatisplus.test.h2.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.h2.tenant.model.Student;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * @author nieqiuqiu 2019/12/8
 */
@CacheNamespace
public interface StudentMapper extends BaseMapper<Student> {

}
