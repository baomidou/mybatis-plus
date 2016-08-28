/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * 数据库关键字替换
 * </p>
 * 
 * @author liupeng
 * @Date 2016-08-25
 */
public class DBKeywordsProcessor {
	protected static final Logger logger = Logger.getLogger("DBKeywordsProcessor");
	private static Set<String> KEYWORDS = null;

	static {
		BufferedReader br = null;
		try {
			InputStream in = DBKeywordsProcessor.class.getClassLoader().getResourceAsStream("database_keywords.dic");
			br = new BufferedReader(new InputStreamReader(in));
			if (null == KEYWORDS) {
				KEYWORDS = new HashSet<String>();
			}
			String keyword = null;
			while ((keyword = br.readLine()) != null) {
				KEYWORDS.add(keyword);
			}
		} catch (Exception e) {
			logger.warning("If you want to support the keyword query, must have database_keywords.dic. " + e.getMessage());
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					logger.severe(e.getMessage());
				}
			}
		}
	}

	/**
	 * <p>
	 * 数据库关键词转义
	 * </p>
	 * 
	 * @param keyword
	 *            数据库关键词
	 * @return
	 */
	public static String convert(String keyword) {
		if (null != KEYWORDS && KEYWORDS.contains(keyword)) {
			return String.format("`%s`", keyword);
		}
		return keyword;
	}

}
