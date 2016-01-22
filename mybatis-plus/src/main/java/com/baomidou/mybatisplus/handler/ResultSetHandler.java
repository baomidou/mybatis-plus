package com.baomidou.mybatisplus.handler;


import java.sql.ResultSet;

/**
 * 
 * 
 * @Description: 
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved
 * @Company: 北京福富软件有限公司
 * @author 黄君毅 2013-4-12
 * @version 1.00.00
 * @history:
 *
 */
public interface ResultSetHandler<T> {
	T handle(ResultSet rs) ;
}
