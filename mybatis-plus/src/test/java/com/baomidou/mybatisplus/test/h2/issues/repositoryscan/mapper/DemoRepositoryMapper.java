package com.baomidou.mybatisplus.test.h2.issues.repositoryscan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.test.h2.issues.repositoryscan.entity.Demo;
import org.springframework.stereotype.Repository;

/**
 * @author nieqiurong
 */
@Repository
public interface DemoRepositoryMapper extends BaseMapper<Demo> {

}
