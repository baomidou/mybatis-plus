package com.baomidou.mybatisplus.mapper;

import static java.lang.String.format;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;

/**
 * 
 */
public class SqlWhere {

	private final StringBuilder sqlWhere = new StringBuilder();


	public static void main( String[] args ) {
		System.err.println(new SqlWhere().eq("abc", 123).eq("uui", "'01'").or("userName", MatchType.EQ, "999").ge("oop", "-d-").toString());
	}


	public String toString() {
		return sqlWhere.toString().substring(3);
	}


	/**
	 * <p>
	 * criteria SQL 组装
	 * </p>
	 * @param andOr
	 * 				逻辑运算符
	 * @param column
	 * 				字段
	 * @param type
	 * 				匹配类型
	 * @param value
	 * 				查询内容
	 * @return
	 */
	protected SqlWhere criteria( AndOr andOr, String column, MatchType matchType, Object value ) {
		if ( column == null || value == null ) {
			throw new MybatisPlusException("assignment content is not allowed to empty.");
		}
		if ( matchType == MatchType.IN || matchType == MatchType.NI ) {
			sqlWhere.append(format("%s %s %s (%s) ", andOr.getMeta(), column, matchType.getMeta(), value));
		} else {
			sqlWhere.append(format("%s %s %s %s ", andOr.getMeta(), column, matchType.getMeta(), value));
		}
		return this;
	}


	/**
	 * <p>
	 * SQL ==
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere or( String column, MatchType matchType, Object value ) {
//		if ( matchTypes[i] == MatchType.IN || matchTypes[i] == MatchType.NI ) {
//			buf.append(format("(%s %s (:%s)) ", properties[i], matchTypes[i].asMeta(), key));
//		} else {
//			buf.append(format("(%s %s :%s) ", properties[i], matchTypes[i].asMeta(), key));
//		}
		return criteria(AndOr.OR, column, matchType, value);
	}

	/**
	 * <p>
	 * SQL ==
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere eq( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.EQ, value);
	}


	/**
	 * <p>
	 * SQL <>
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere ne( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.NE, value);
	}


	/**
	 * <p>
	 * SQL >
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere gt( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.GT, value);
	}


	/**
	 * <p>
	 * SQL <
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere lt( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.LT, value);
	}


	/**
	 * <p>
	 * SQL >=
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere ge( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.GE, value);
	}


	/**
	 * <p>
	 * SQL <=
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere le( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.LE, value);
	}


	/**
	 * <p>
	 * SQL LIKE
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere like( String column, String value ) {
		return criteria(AndOr.AND, column, MatchType.LIKE, value);
	}


	/**
	 * <p>
	 * SQL IN
	 * </p>
	 * @param column
	 * 				字段
	 * @param value
	 * 				查询内容
	 */
	public SqlWhere in( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.IN, value);
	}


	/**
	 * 
	 * @param column
	 * @param value
	 * @param andOr
	 * @return
	 */
	public SqlWhere ni( String column, Object value ) {
		return criteria(AndOr.AND, column, MatchType.NI, value);
	}


	/**
	 * 
	 * @param orderBy
	 * @return
	 */
	public SqlWhere orderBy( final String orderBy ) {
		sqlWhere.append(format("ORDER BY %s ", orderBy));
		return this;
	}


	/**
	 * 逻辑运算符
	 */
	public enum AndOr {
		AND("AND", "逻辑与"), OR("OR", "逻辑或");

		private final String meta;

		private final String desc;


		private AndOr( final String meta, final String desc ) {
			this.meta = meta;
			this.desc = desc;
		}


		public String getMeta() {
			return meta;
		}


		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 匹配类型
	 */
	public enum MatchType {
		EQ("=", "等于"), NE("<>", "等于"), LIKE("like", "模糊查询"), 
		GT(">", "大于"), LT("<", "大于"), GE(">=", "大于等于"), 
		LE("<=", "小于等于"), IN("IN", "在范围内"), NI("NOT IN", "不在范围内");

		private final String meta;

		private final String desc;


		private MatchType( final String meta, final String desc ) {
			this.meta = meta;
			this.desc = desc;
		}


		public String getMeta() {
			return meta;
		}


		public String getDesc() {
			return desc;
		}
	}
}
