package com.baomidou.mybatisplus.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	/**
	 * 测试
	 */
	public static void main(String[] args) {
		int count = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(count);
		for (int i = 0; i < count; i++) {
			executorService.execute(new IdWorkerTest().new Task());
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class Task implements Runnable {

		public void run() {
			try {
				long id = IdWorker.getId();
				System.err.println(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
