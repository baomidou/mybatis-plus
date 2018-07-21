package com.baomidou.mybatisplus.test.mysql.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.base.entity.LogicTestData;
import com.baomidou.mybatisplus.test.base.mapper.LogicTestDataMapper;
import com.baomidou.mybatisplus.test.mysql.service.ILogicTestDataService;
import org.springframework.stereotype.Service;


@Service
public class LogicTestDataServiceImpl extends ServiceImpl<LogicTestDataMapper, LogicTestData> implements ILogicTestDataService {

}
