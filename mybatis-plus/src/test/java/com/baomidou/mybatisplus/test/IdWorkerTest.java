package com.baomidou.mybatisplus.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

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
		int count = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		final List<Long> results = new ArrayList<>();
		CompletionService<Long> cs = new ExecutorCompletionService<Long>(executorService);

		for (int i = 1; i < count; i++) {
			cs.submit(new Callable<Long>() {
				public Long call() throws Exception {
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
		int odd = 0;
		int even = 0;
		List<Long> ttt = new ArrayList<>();
		for (Long id : results) {

			if (ttt.contains(id)) {
				System.err.println("ssss");
			}
			ttt.add(id);
			if (id % 2 != 0) {
				odd++;
			} else {
				even++;
			}
		}
		System.err.println("奇数:" + odd);
		System.err.println("偶数:" + even);
		Assert.assertTrue(odd >= 450 && odd <= 550);
		Assert.assertTrue(even >= 450 && even <= 550);
	}

}
