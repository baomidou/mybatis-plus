/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.toolkit;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * String 工具类
 * </p>
 *
 * @author D.Yang
 * @Date 2016-08-18
 */
public class StringUtils {

	/**
	 * 下划线字符
	 */
	public static final char UNDERLINE = '_';

	/**
	 * 空字符串
	 */
	public static final String EMPTY_STRING = "";
	/**
	 * 占位符
	 */
	public static final String PLACE_HOLDER = "{%s}";

	/**
	 * <p>
	 * 判断字符串是否为空
	 * </p>
	 *
	 * @param cs
	 *            需要判断字符串
	 * @return 判断结果
	 */
	public static boolean isEmpty(final CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * 判断字符串是否不为空
	 * </p>
	 *
	 * @param cs
	 *            需要判断字符串
	 * @return 判断结果
	 */
	public static boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}

	/**
	 * <p>
	 * 字符串驼峰转下划线格式
	 * </p>
	 *
	 * @param param
	 *            需要转换的字符串
	 * @return 转换好的字符串
	 */
	public static String camelToUnderline(String param) {
		if (isEmpty(param)) {
			return EMPTY_STRING;
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				sb.append(UNDERLINE);
			}
			sb.append(Character.toLowerCase(c));
		}
		return sb.toString();
	}

	/**
	 * <p>
	 * 字符串下划线转驼峰格式
	 * </p>
	 *
	 * @param param
	 *            需要转换的字符串
	 * @return 转换好的字符串
	 */
	public static String underlineToCamel(String param) {
		if (isEmpty(param)) {
			return EMPTY_STRING;
		}
		int len = param.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = param.charAt(i);
			if (c == UNDERLINE) {
				if (++i < len) {
					sb.append(Character.toUpperCase(param.charAt(i)));
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * <p>
	 * 判断字符串是否为纯大写字母
	 * </p>
	 *
	 * @param str
	 *            要匹配的字符串
	 * @return
	 */
	public static boolean isUpperCase(String str) {
		return match("^[A-Z]+$", str);
	}

	/**
	 * <p>
	 * 正则表达式匹配
	 * </p>
	 *
	 * @param regex
	 *            正则表达式字符串
	 * @param str
	 *            要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	public static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * <p>
	 * SQL 参数填充
	 * </p>
	 *
	 * @param content
	 *            填充内容
	 * @param args
	 *            填充参数
	 * @return
	 */
	public static String sqlArgsFill(String content, Object... args) {
		if (null == content) {
			return null;
		}
		if (args != null) {
			int length = args.length;
			if (length >= 1) {
				for (int i = 0; i < length; i++) {
					content = content.replace(String.format(PLACE_HOLDER, i), sqlParam(args[i]));
				}
			}
		}
		return content;
	}

	/**
	 * 获取SQL PARAMS字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String sqlParam(Object obj) {
		String repStr;
		if (obj instanceof Collection) {
			repStr = StringUtils.quotaMarkList((Collection<?>) obj);
		} else {
			repStr = StringUtils.quotaMark(obj);
		}
		return repStr;
	}

	/**
	 * <p>
	 * 使用单引号包含字符串
	 * </p>
	 *
	 * @param obj
	 *            原字符串
	 * @return 单引号包含的原字符串
	 */
	public static String quotaMark(Object obj) {
		String srcStr = String.valueOf(obj);
		if (obj instanceof String) {
			// fix #79
			return StringEscape.escapeString(srcStr);
		}
		return srcStr;
	}

	/**
	 * <p>
	 * 使用单引号包含字符串
	 * </p>
	 *
	 * @param coll
	 *            集合
	 * @return 单引号包含的原字符串的集合形式
	 */
	public static String quotaMarkList(Collection<?> coll) {
		StringBuilder sqlBuild = new StringBuilder();
		sqlBuild.append("(");
		int _size = coll.size();
		int i = 0;
		Iterator<?> iterator = coll.iterator();
		while (iterator.hasNext()) {
			String tempVal = StringUtils.quotaMark(iterator.next());
			if (i + 1 == _size) {
				sqlBuild.append(tempVal);
			} else {
				sqlBuild.append(tempVal);
				sqlBuild.append(",");
			}
			i++;
		}
		sqlBuild.append(")");
		return sqlBuild.toString();
	}

	/**
	 * <p>
	 * 拼接字符串第二个字符串第一个字母大写
	 *
	 * @param concatStr
	 * @param str
	 * @return
	 */
	public static String concatCapitalize(String concatStr, final String str) {
		if (isEmpty(concatStr)) {
			concatStr = EMPTY_STRING;
		}
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}

		final char firstChar = str.charAt(0);
		if (Character.isTitleCase(firstChar)) {
			// already capitalized
			return str;
		}

		return new StringBuilder(strLen).append(concatStr).append(Character.toTitleCase(firstChar)).append(str.substring(1))
				.toString();
	}

	/**
	 * <p>
	 * 字符串第一个字母大写
	 * </p>
	 *
	 * @param str
	 * @return
	 */
	public static String capitalize(final String str) {
		return concatCapitalize(null, str);
	}

	/**
	 * <p>
	 * 判断对象是否为空
	 * </p>
	 *
	 * @param object
	 * @return
	 */
	public static boolean checkValNotNull(Object object) {
		if (object instanceof CharSequence) {
			return isNotEmpty((CharSequence) object);
		}
		return object == null ? false : true;
	}
	/**
	 * <p>
	 * 判断对象是否为空
	 * </p>
	 *
	 * @param object
	 * @return
	 */
	public static boolean checkValNull(Object object) {
		return !checkValNotNull(object);
	}

}
