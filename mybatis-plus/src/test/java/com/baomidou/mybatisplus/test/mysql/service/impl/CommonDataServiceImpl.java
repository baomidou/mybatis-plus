package com.baomidou.mybatisplus.test.mysql.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.mysql.entity.CommonData;
import com.baomidou.mybatisplus.test.mysql.mapper.children.CommonDataChildrenMapper;
import com.baomidou.mybatisplus.test.mysql.service.ICommonDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dyu 2020/12/21 16:23
 */
@Service
public class CommonDataServiceImpl extends CopyServiceImpl<CommonDataChildrenMapper, CommonData> implements ICommonDataService {
    //@Transactional(transactionManager = "assignTm", readOnly = true)
    //@Override
    //public boolean saveOrUpdate(CommonData entity) {
    //    return super.saveOrUpdate(entity);
    //}
}
