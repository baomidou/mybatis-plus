package com.baomidou.mybatisplus.test;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

import com.baomidou.mybatisplus.toolkit.IdWorker;

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
	@Test
	@PerfTest(invocations = 200000000, threads = 16)
	public void testIdWorker() throws Exception {
		IdWorker.getId();
	}

}