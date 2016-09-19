package com.baomidou.mybatisplus.toolkit;

import java.util.Collection;

/**
 * Collection工具类
 *
 * @author Caratacus
 * @version 1.0
 * @date 2016/6/24 0024
 */
public class CollectionUtil {

	/**
	 * 校验集合是否为空
	 *
	 * @param coll
	 * @return boolean
	 * @throws
	 * @author Caratacus
	 * @date 2016/8/26 0026
	 * @version 1.0
	 */
	public static boolean isEmpty(Collection coll) {
		return (coll == null || coll.isEmpty());
	}

	/**
	 * 校验集合是否不为空
	 *
	 * @param coll
	 * @return boolean
	 * @throws
	 * @author Caratacus
	 * @date 2016/8/26 0026
	 * @version 1.0
	 */
	public static boolean isNotEmpty(Collection coll) {
		return !isEmpty(coll);
	}

}
