package com.baomidou.mybatisplus.test.h2.fillperformance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.fillperformance.mapper.PerformanceModelMapper;
import com.baomidou.mybatisplus.test.h2.fillperformance.model.PerformanceModel;
import com.baomidou.mybatisplus.test.h2.fillperformance.service.IPerformanceModelService;
import org.springframework.stereotype.Service;

@Service
public class PerformanceModelServiceImpl extends ServiceImpl<PerformanceModelMapper, PerformanceModel> implements IPerformanceModelService {

}
