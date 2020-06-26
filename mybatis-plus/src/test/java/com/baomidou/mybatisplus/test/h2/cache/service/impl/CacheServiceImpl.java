package com.baomidou.mybatisplus.test.h2.cache.service.impl;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.h2.cache.mapper.CacheMapper;
import com.baomidou.mybatisplus.test.h2.cache.model.CacheModel;
import com.baomidou.mybatisplus.test.h2.cache.service.ICacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
public class CacheServiceImpl extends ServiceImpl<CacheMapper, CacheModel> implements ICacheService {

    //手动撸一个批量删除.
    private void removeBatchById(Collection<Long> idList) {
        String sqlStatement = sqlStatement(SqlMethod.DELETE_BY_ID);
        executeBatch(idList, (sqlSession, id) -> sqlSession.delete(sqlStatement, id));
    }

    @Override
    @Transactional
    public long testBatchTransactionalClear1() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        getById(model.getId());
        updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        return model.getId();
    }

    @Override
    @Transactional
    public long testBatchTransactionalClear2() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        getById(model.getId());
        updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        model.setName("小红");
        updateById(model);
        return model.getId();
    }

    @Override
    @Transactional
    public long testBatchTransactionalClear3() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        model.setName("小红");
        updateById(model);
        getById(model.getId());
        return model.getId();
    }

    @Override
    public long testBatchTransactionalClear4() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        return model.getId();
    }

    @Override
    public long testBatchTransactionalClear5() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        removeBatchById(Collections.singletonList(model.getId()));
        removeById(model.getId());
        return model.getId();
    }

    @Override
    @Transactional
    public long testBatchTransactionalClear6() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        removeBatchById(Collections.singletonList(model.getId()));
        return model.getId();
    }

    @Override
    @Transactional
    public long testBatchTransactionalClear7() {
        CacheModel model = new CacheModel("靓仔");
        save(model);
        updateBatchById(Collections.singletonList(new CacheModel(model.getId(), "旺仔")));
        removeBatchById(Collections.singletonList(model.getId()));
        return model.getId();
    }
}
