package com.baomidou.mybatisplus.test.h2.tenant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.tenant.mapper.StudentMapper;
import com.baomidou.mybatisplus.test.h2.tenant.model.Student;
import com.baomidou.mybatisplus.test.h2.tenant.service.IStudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

}
