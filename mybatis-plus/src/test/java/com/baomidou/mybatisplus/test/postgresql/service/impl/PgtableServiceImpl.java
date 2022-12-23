package com.baomidou.mybatisplus.test.postgresql.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.test.postgresql.entity.Pgtable;
import com.baomidou.mybatisplus.test.postgresql.mapper.PgtableMappper;
import com.baomidou.mybatisplus.test.postgresql.service.IPgtableService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class PgtableServiceImpl extends ServiceImpl<PgtableMappper, Pgtable> implements IPgtableService {

    @Override
    public void testTask() {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.schedule(() -> {
            try {
                this.updateById(Pgtable.builder().id(1L).compName("testTask01").age(11).build());
                baseMapper.updateById(Pgtable.builder().id(1L).compName("testTask02").age(12).build());
            } catch (Exception exception) {
                exception.getStackTrace();
            } finally {
                executorService.shutdown();
            }
        }, 3, TimeUnit.SECONDS);
    }
}
