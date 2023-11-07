package com.baomidou.mybatisplus.test.h2.issues.repositoryscan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.entity.Demo;
import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.mapper.DemoRepositoryMapper;
import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.service.IDemoRepositoryService;
import org.springframework.stereotype.Service;

/**
 * @author nieqiurong
 */
@Service
public class DemoRepositoryServiceImpl extends ServiceImpl<DemoRepositoryMapper, Demo> implements IDemoRepositoryService {

}
