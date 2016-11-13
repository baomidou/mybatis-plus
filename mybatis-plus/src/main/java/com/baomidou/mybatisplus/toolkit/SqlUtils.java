package com.baomidou.mybatisplus.toolkit;


import com.baomidou.mybatisplus.plugins.entity.CountOptimize;

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
}
