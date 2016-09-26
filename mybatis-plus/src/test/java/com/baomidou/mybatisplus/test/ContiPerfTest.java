/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;

/**
 * <p>
 * 并发测试
 * </p>
 * 
 * @author hubin
 * @date 2016-08-18
 */
public class ContiPerfTest {
	
	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	/**
	 * samples: 200000000
	 * max: 82
	 * average: 0.0039698
	 * median: 0
	 */
	/*@Test
	@PerfTest(invocations = 200000000, threads = 16)
	public void testIdWorker() throws Exception {
		IdWorker.getId();
	}*/

}