package com.baomidou.mybatisplus.plugins;

import net.sf.jsqlparser.expression.Expression;

/**
 * 乐观锁处理器,底层接口
 *
 * @author TaoYu
 */
public interface VersionHandler {
	
	/**
	 * 返回需要处理的类型
	 */
	Class<?>[] handleType();

	/**
	 * 根据类型得到equalTo右侧的表达式
	 */
	Expression getRightExpression(Object param);

	/**
	 * 根据类型得到+1后的表达式
	 */
	Expression getPlusExpression(Object param);
}