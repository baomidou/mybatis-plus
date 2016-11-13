package com.baomidou.mybatisplus.toolkit;

import com.baomidou.mybatisplus.plugins.entity.CountOptimize;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

/**
 * <p>
 * SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-13
 */
public class SqlUtils {
	/**
	 * 获取CountOptimize
	 * 
	 * @param originalSql
	 *            需要计算Count SQL
	 * @param isOptimizeCount
	 *            是否需要优化Count
	 * @return CountOptimize
	 */
	public static CountOptimize getCountOptimize(String originalSql, boolean isOptimizeCount) {
		CountOptimize countOptimize = CountOptimize.newInstance();
		StringBuffer countSql = new StringBuffer("SELECT COUNT(1) AS TOTAL ");
		if (isOptimizeCount) {
			String tempSql = originalSql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY");
			String indexOfSql = tempSql.toUpperCase();
			if (!indexOfSql.contains("DISTINCT")) {
				int formIndex = indexOfSql.indexOf("FROM");
				int orderByIndex = indexOfSql.lastIndexOf("ORDER BY");
				if (formIndex > -1) {
					// 有排序情况
					if (orderByIndex > -1) {
						tempSql = tempSql.substring(0, orderByIndex);
						countSql.append(tempSql.substring(formIndex));
						countOptimize.setOrderBy(false);
						// 无排序情况
					} else {
						countSql.append(tempSql.substring(formIndex));
					}
				} else {
					countSql.append("FROM (").append(originalSql).append(") A");
				}
			} else {
				countSql.append("FROM (").append(originalSql).append(") A");
			}
		} else {
			countSql.append("FROM (").append(originalSql).append(") A");
		}
		countOptimize.setCountSQL(countSql.toString());
		return countOptimize;
	}

	/**
	 * 查询SQL拼接Order By
	 * 
	 * @param originalSql
	 *            需要拼接的SQL
	 * @param page
	 *            page对象
	 * @param orderBy
	 *            是否需要拼接Order By
	 * @return
	 */
	public static String concatOrderBy(String originalSql, Pagination page, boolean orderBy) {
		StringBuffer buildSql = new StringBuffer(originalSql);
		if (orderBy && StringUtils.isNotEmpty(page.getOrderByField())) {
			buildSql.append(" ORDER BY ").append(page.getOrderByField());
			buildSql.append(page.isAsc() ? " ASC " : " DESC ");
		}
		return buildSql.toString();
	}
}
