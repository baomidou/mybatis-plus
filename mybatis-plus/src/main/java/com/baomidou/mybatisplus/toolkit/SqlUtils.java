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
package com.baomidou.mybatisplus.toolkit;

import com.alibaba.druid.sql.PagerUtils;
import com.baomidou.mybatisplus.plugins.SQLFormatter;
import com.baomidou.mybatisplus.plugins.entity.CountOptimize;
import com.baomidou.mybatisplus.plugins.entity.Optimize;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * SqlUtils工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-11-13
 */
public class SqlUtils {
	private final static SQLFormatter sqlFormatter = new SQLFormatter();
	private static final String SQL_BASE_COUNT = "SELECT COUNT(1) FROM ( %s )";
	private static List<SelectItem> countSelectItem = null;

	/**
	 * 获取CountOptimize
	 * 
	 * @param originalSql
	 *            需要计算Count SQL
	 * @param optimizeType
	 *            count优化方式
	 * @param isOptimizeCount
	 *            是否需要优化Count
	 * @return CountOptimize
	 */
	public static CountOptimize getCountOptimize(String originalSql, String optimizeType, String dialectType,
			boolean isOptimizeCount) {
		CountOptimize countOptimize = CountOptimize.newInstance();
		if (isOptimizeCount) {
			String tempSql = originalSql.replaceAll("(?i)ORDER[\\s]+BY", "ORDER BY").replaceAll("(?i)GROUP[\\s]+BY", "GROUP BY");
			String indexOfSql = tempSql.toUpperCase();
			// 有排序情况
			int orderByIndex = indexOfSql.lastIndexOf("ORDER BY");
			// 只针对 ALI_DRUID DEFAULT 这2种情况
			if (orderByIndex > -1) {
				countOptimize.setOrderBy(false);
			}
			Optimize opType = Optimize.getOptimizeType(optimizeType);
			switch (opType) {
			case ALI_DRUID:
				/**
				 * 调用ali druid方式 插件dbType一定要设置为小写与JdbcConstants保持一致
				 * 
				 * @see com.alibaba.druid.util.JdbcConstants
				 */
				String aliCountSql = PagerUtils.count(originalSql, dialectType);
				countOptimize.setCountSQL(aliCountSql);
				break;
			case JSQLPARSER:
				jsqlparserCount(countOptimize, originalSql);
				break;
			default:
				StringBuffer countSql = new StringBuffer("SELECT COUNT(1) AS TOTAL ");
				boolean optimize = false;
				if (!indexOfSql.contains("DISTINCT") && !indexOfSql.contains("GROUP BY")) {
					int formIndex = indexOfSql.indexOf("FROM");
					if (formIndex > -1) {
						if (orderByIndex > -1) {
							tempSql = tempSql.substring(0, orderByIndex);
							countSql.append(tempSql.substring(formIndex));
							// 无排序情况
						} else {
							countSql.append(tempSql.substring(formIndex));
						}
						// 执行优化
						optimize = true;
					}
				}
				if (!optimize) {
					// 无优化SQL
					countSql.append("FROM (").append(originalSql).append(") A");
				}
				countOptimize.setCountSQL(countSql.toString());
				;
			}

		}

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
		if (orderBy && StringUtils.isNotEmpty(page.getOrderByField())) {
			StringBuffer buildSql = new StringBuffer(originalSql);
			buildSql.append(" ORDER BY ").append(page.getOrderByField());
			buildSql.append(page.isAsc() ? " ASC " : " DESC ");
			return buildSql.toString();
		}
		return originalSql;
	}

	/**
	 * 格式sql
	 * 
	 * @param boundSql
	 * @param format
	 * @return
	 */
	public static String sqlFormat(String boundSql, boolean format) {
		if (format) {
			return sqlFormatter.format(boundSql);
		} else {
			return boundSql.replaceAll("[\\s]+", " ");
		}
	}

	/**
	 * jsqlparser方式获取select的count语句
	 *
	 * @param originalSql
	 *            selectSQL
	 * @return
	 */
	public static CountOptimize jsqlparserCount(CountOptimize countOptimize, String originalSql) {
		String sqlCount;
		try {
			Select selectStatement = (Select) CCJSqlParserUtil.parse(originalSql);
			PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
			Distinct distinct = plainSelect.getDistinct();
			List<Expression> groupBy = plainSelect.getGroupByColumnReferences();
			// 包含 distinct、groupBy不优化
			if (distinct != null || CollectionUtil.isNotEmpty(groupBy)) {
				sqlCount = String.format(SQL_BASE_COUNT, originalSql);
			}
			// 优化Order by
			List<OrderByElement> orderBy = plainSelect.getOrderByElements();
			if (CollectionUtil.isNotEmpty(orderBy)) {
				plainSelect.setOrderByElements(null);
				countOptimize.setOrderBy(false);
			}
			List<SelectItem> selectCount = countSelectItem();
			plainSelect.setSelectItems(selectCount);
			sqlCount = selectStatement.toString();
		} catch (Exception e) {
			sqlCount = String.format(SQL_BASE_COUNT, originalSql);
		}
		countOptimize.setCountSQL(sqlCount);
		return countOptimize;
	}

	/**
	 * 获取jsqlparser中count的SelectItem
	 *
	 * @return
	 */
	private static List<SelectItem> countSelectItem() {
		if (CollectionUtil.isNotEmpty(countSelectItem)) {
			return countSelectItem;
		}
		Function function = new Function();
		function.setName("COUNT");
		List<Expression> expressions = new ArrayList<Expression>();
		LongValue longValue = new LongValue(1);
		ExpressionList expressionList = new ExpressionList();
		expressions.add(longValue);
		expressionList.setExpressions(expressions);
		function.setParameters(expressionList);
		countSelectItem = new ArrayList<SelectItem>();
		SelectExpressionItem selectExpressionItem = new SelectExpressionItem(function);
		countSelectItem.add(selectExpressionItem);
		return countSelectItem;
	}
}
