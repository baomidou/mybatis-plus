package com.baomidou.mybatisplus.plugins;

import net.sf.jsqlparser.expression.Expression;

/**
 * 乐观锁处理器,底层接口
 * 
 * @author TaoYu
 */
public interface VersionHandler {
	/**
	 * <pre>
	 * 根据类型得到equalTo右侧的表达式
	 * 
	 * </pre>
	 */
	Expression getRightExpression(Object param);

	/**
	 * 根据类型得到+1后的值
	 */
	Expression getPlusExpression(Object param);
}