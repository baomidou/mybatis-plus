package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Mybatis执行sql工具,主要为AR方式调用
 *
 * @author Caratacus
 * @since 2016-10-18
 */
public class SqlMapper {

	protected static final Logger logger = Logger.getLogger("SqlMapper");
	public static final String INSERT = "SqlMapper.Insert";
	public static final String DELETE = "SqlMapper.Delete";
	public static final String UPDATE = "SqlMapper.Update";
	public static final String SELECT = "SqlMapper.Select";
	private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();
	private final SqlSession sqlSession;

	/**
	 * 构造方法，默认缓存MappedStatement
	 *
	 * @param sqlSession
	 */
	public SqlMapper(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	/**
	 * 获取List中一条的数据
	 *
	 * @param list
	 *            List结果
	 * @param <T>
	 *            泛型类型
	 * @return
	 */
	private <T> T getOne(List<T> list) {

		if (CollectionUtil.isNotEmpty(list)) {
			int size = list.size();
			if (size > 1) {
				logger.warning("Warn: selectOne Method There are " + size + " results.");
			}
			return list.get(0);
		}
		return null;
	}

	/**
	 * 查询返回一个结果，多个结果时抛出异常
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public Map<String, Object> selectOne(String sql) {
		List<Map<String, Object>> list = selectList(sql);
		return getOne(list);
	}

	/**
	 * 查询返回List<Map<String, Object>>
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public List<Map<String, Object>> selectList(String sql) {
		sqlMap.put("sql", sql);
		return sqlSession.selectList(SELECT, sqlMap);
	}

	/**
	 * 插入数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public boolean insert(String sql) {
		sqlMap.put("sql", sql);
		return retBool(sqlSession.insert(INSERT, sqlMap));
	}

	/**
	 * 更新数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public boolean update(String sql) {
		sqlMap.put("sql", sql);
		return retBool(sqlSession.update(UPDATE, sqlMap));
	}

	/**
	 * 删除数据
	 *
	 * @param sql
	 *            执行的sql
	 * @return
	 */
	public boolean delete(String sql) {
		sqlMap.put("sql", sql);
		return retBool(sqlSession.delete(DELETE, sqlMap));
	}

	/**
	 * 判断数据库操作是否成功
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	private boolean retBool(int result) {
		return result >= 1;
	}

}