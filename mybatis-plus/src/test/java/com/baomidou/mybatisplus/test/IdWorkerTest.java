package com.baomidou.mybatisplus.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.test.plugins.RandomUtils;
import com.baomidou.mybatisplus.toolkit.IdWorker;

/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * <p>
 * IdWorker 并发测试
 * </p>
 *
 * @author hubin
 * @date 2016-08-01
 */
public class IdWorkerTest {

	@Test
	public void test() throws Exception {
		double wucha = 0.05;
		int count = 1000;
		int wuchaNum = (int) (count * wucha);
		int high = count + wuchaNum;
		int low = count - wuchaNum;
		System.err.println("共有" + count + "个数参与测试,误差系数为" + wucha + "误差值为" + wuchaNum);

		ExecutorService executorService = Executors.newFixedThreadPool(20);
		final List<Long> results = new ArrayList<>();
		CompletionService<Long> cs = new ExecutorCompletionService<Long>(executorService);
		for (int i = 1; i < count; i++) {
			cs.submit(new Callable<Long>() {
				public Long call() throws Exception {
					Thread.sleep(RandomUtils.nextInt(1, 2000));
					return IdWorker.getId();
				}
			});
		}
		for (int i = 0; i < count; i++) {
			Future<Long> future = executorService.submit(new Callable<Long>() {
				@Override
				public Long call() throws Exception {
					return IdWorker.getId();
				}
			});
			results.add(future.get());
		}
		executorService.shutdown();
		HashSet<Long> set = new HashSet<>(results);
		// 判断是否有重复
		Assert.assertEquals(count, set.size());
		int odd = 0;
		int even = 0;
		for (Long id : results) {
			if (id % 2 != 0) {
				odd++;
			} else {
				even++;
			}
		}
		System.err.println("奇数:" + odd);
		System.err.println("偶数:" + even);
		Assert.assertTrue(odd >= low && odd <= high);
		Assert.assertTrue(even >= low && even <= high);
	}

}
