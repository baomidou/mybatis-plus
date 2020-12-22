package com.baomidou.mybatisplus.test.mysql.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.test.mysql.entity.CommonData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dyu 2020/12/21 16:22
 */
@Service
@Transactional(value = "assignTm", readOnly = true)
public interface ICommonDataService extends IService<CommonData> {
}
