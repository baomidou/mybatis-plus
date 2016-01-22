package com.baomidou.mybatisplus.handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 */
public final class PreparedStatementHandler {
	private static final PreparedStatementHandler psh = new PreparedStatementHandler();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static PreparedStatementHandler getInstance() {
		return psh;
	}

	public void pager(boolean sequence, StringBuffer sql, List<Object> params,
			int pageSize, int pageNo) {
	
		if (sequence) {
			String format = "select * from (select t.*, rownum rn from (%s) t where rownum <= ?) where rn >= ?";
			sql.replace(0, sql.length(), String.format(format, sql));
			
			params.add(pageSize * pageNo);
			params.add(pageSize * (pageNo - 1) + 1);
		} else {
			sql.append(" limit ?, ?");
			params.add(pageSize * (pageNo - 1));
			params.add(pageSize);
		}
	}

	/**
	 * operator: and/or/where
	 */
	public <T> void in(boolean sequence, StringBuffer sql, List<Object> params, String operator,
			String field, List<T> values){
		if (values == null || values.size() == 0) {
			return;
		}
		
		sql.append(" ");
		sql.append(operator);
		
		if (sequence) {
			sql.append("(");
			int size = values.size();
			int period = 1000;
	
			int n = (size % period == 0) ? size / period :  size / period + 1; 		
			
			// 解决ORA-01795问题
			for (int i = 0; i < n; i++) {
				sql.append(" ");
				if (i > 0) {
					sql.append("or ");
				}
				sql.append(field);
				sql.append(" in (");
				int k = period;
				if (i == n - 1 && size%period !=0) {
					k = size % period ;
				}
				sql.append(generateQuestionMarks(k));
				sql.append(")");

			}	
			sql.append(")");
		} else {		
			sql.append(" ");
			sql.append(field);
			sql.append(" in (");
			sql.append(generateQuestionMarks(values.size()));
			sql.append(")");	
		}	
		for (T value : values) {
			params.add(value);
		}
	}

	
	/* 生成占位符 */
	public String generateQuestionMarks(int n) {
		if (n < 1)
			return "";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < n; i++) {
			buf.append("?,");
		}
		buf.deleteCharAt(buf.length() - 1);
		return buf.toString();
	}

	/* 调整语句 */
	public String adjustSQL(boolean sequence, String sql, Object... params) {
		if (!sequence)
			return sql;
		int cols = params.length;
		Object[] args = new Object[cols];
		boolean found = false; // 包含时间参数
		for (int i = 0; i < cols; i++) {
			Object value = params[i];
			if (value instanceof Date) {
				args[i] = "to_date(?,'yyyy-mm-dd hh24:mi:ss')";
				found = true;
			} else {
				args[i] = "?";
			}
		}
		if (found) {
			String format = sql.replaceAll("\\?", "%s");
			sql = String.format(format, args);
		}
		return sql;
	}

	/* 调整参数 */
	public void adjustParams(Object... params) {
		for (int i = 0, cols = params.length; i < cols; i++) {
			Object value = params[i];
			if (value == null)
				continue;
			if (value instanceof Date) {
				params[i] = sdf.format(value);
			} else if (value.getClass().isEnum()) {
				params[i] = params[i].toString();
			} else if (value instanceof Boolean) {
				params[i] = (Boolean) value ? 1 : 0;
			}
		}
	}

	/* 调整参数和语句 */
	public String adjust(boolean sequence, String sql, Object[] params) {
		int cols = params.length;
		Object[] args = new Object[cols];
		boolean found = false; // 包含时间参数
		for (int i = 0; i < cols; i++) {
			args[i] = "?";
			Object value = params[i];
			if (value == null)
				continue;
			if (value instanceof Date) {
				if (sequence) {
					args[i] = "to_date(?,'yyyy-mm-dd hh24:mi:ss')";
					found = true;
				}
				params[i] = sdf.format(value);
			} else if (value.getClass().isEnum()) {
				params[i] = value.toString();
			}
		}
		if (found) {
			String format = sql.replaceAll("\\?", "%s");
			sql = String.format(format, args);
		}
		return sql;
	}

	public String camel2underscore(String camel) {
		camel = camel.replaceAll("([a-z])([A-Z])", "$1_$2");
		return camel.toLowerCase();
	}
	
	public String underscore2camel(String underscore) {
		if (!underscore.contains("_")) {
			return underscore;
		}
		StringBuffer buf = new StringBuffer();
		underscore = underscore.toLowerCase();
		Matcher m = Pattern.compile("_([a-z])").matcher(underscore);
		while (m.find()) {
			m.appendReplacement(buf, m.group(1).toUpperCase());
		}
		return m.appendTail(buf).toString();
	}

	/* 语句和参数已经经过调整 */
	public void print(final String sql, final Object[] params) {
		if (!match(sql, params)) {
			System.out.println(sql);
			return;
		}

		int cols = params.length;
		Object[] values = new Object[cols];
		System.arraycopy(params, 0, values, 0, cols);

		for (int i = 0; i < cols; i++) {
			Object value = values[i];
			if (value instanceof Date) {
				values[i] = toQuote(sdf.format(value));
			} else if (value instanceof String) {
				values[i] = toQuote(value);
			} else if (value instanceof Boolean) {
				values[i] = (Boolean) value ? 1 : 0;
			}
		}
		String statement = String.format(sql.replaceAll("\\?", "%s"), values);
		System.out.println(statement);
	}

	/* ?和参数的实际个数是否匹配 */
	private boolean match(String sql, Object[] params) {
		Matcher m = Pattern.compile("(\\?)").matcher(sql);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count == params.length;
	}

	private String toQuote(Object value) {
		return "'" + value + "'";
	}
}
