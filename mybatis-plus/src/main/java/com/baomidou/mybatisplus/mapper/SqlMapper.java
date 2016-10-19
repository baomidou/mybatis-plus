package com.baomidou.mybatisplus.mapper;

import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
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
	private final MSUtils msUtils;
	private final SqlSession sqlSession;

	/**
	 * 构造方法，默认缓存MappedStatement
	 *
	 * @param sqlSession
	 */
	public SqlMapper(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
		this.msUtils = new MSUtils(sqlSession.getConfiguration());
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

	private class MSUtils {
		private Configuration configuration;
		private LanguageDriver languageDriver;

		private MSUtils(Configuration configuration) {
			this.configuration = configuration;
			this.languageDriver = configuration.getDefaultScriptingLanuageInstance();
			init();
		}

		/**
		 * SqlMapper init
		 */
		private void init() {
			initSelect();
			initInsert();
			initUpdate();
			initDelete();
		}

		/**
		 * 创建MSID
		 *
		 * @param sql
		 *            执行的sql
		 * @param sql
		 *            执行的sqlCommandType
		 * @return
		 */
		private String newMsId(String sql, SqlCommandType sqlCommandType) {
			StringBuilder msIdBuilder = new StringBuilder(sqlCommandType.toString());
			msIdBuilder.append(".").append(sql.hashCode());
			return msIdBuilder.toString();
		}

		/**
		 * 是否已经存在该ID
		 *
		 * @param msId
		 * @return
		 */
		private boolean hasMappedStatement(String msId) {
			return configuration.hasStatement(msId, false);
		}

		/**
		 * 创建一个查询的MS
		 *
		 * @param msId
		 * @param sqlSource
		 *            执行的sqlSource
		 * @param resultType
		 *            返回的结果类型
		 */
		private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
			MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, SqlCommandType.SELECT).resultMaps(
					new ArrayList<ResultMap>() {
						{
							add(new ResultMap.Builder(configuration, "defaultResultMap", resultType,
									new ArrayList<ResultMapping>(0)).build());
						}
					}).build();
			// 缓存
			configuration.addMappedStatement(ms);
		}

		/**
		 * 创建一个简单的MS
		 *
		 * @param msId
		 * @param sqlSource
		 *            执行的sqlSource
		 * @param sqlCommandType
		 *            执行的sqlCommandType
		 */
		private void newUpdateMappedStatement(String msId, SqlSource sqlSource, SqlCommandType sqlCommandType) {
			MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType).resultMaps(
					new ArrayList<ResultMap>() {
						{
							add(new ResultMap.Builder(configuration, "defaultResultMap", int.class, new ArrayList<ResultMapping>(
									0)).build());
						}
					}).build();
			// 缓存
			configuration.addMappedStatement(ms);
		}

		/**
		 * initSelect
		 */
		private void initSelect() {
			if (hasMappedStatement(SELECT)) {
				logger.warning("SqlMapper Select Initialization exception");
				return;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, "${sql}", Map.class);
			newSelectMappedStatement(SELECT, sqlSource, Map.class);
		}

		/**
		 * initInsert
		 */
		private void initInsert() {
			if (hasMappedStatement(INSERT)) {
				logger.warning("SqlMapper Insert Initialization exception");
				return;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, "${sql}", Map.class);
			newUpdateMappedStatement(INSERT, sqlSource, SqlCommandType.INSERT);
		}

		/**
		 * initUpdate
		 */
		private void initUpdate() {
			if (hasMappedStatement(UPDATE)) {
				logger.warning("SqlMapper Update Initialization exception");
				return;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, "${sql}", Map.class);
			newUpdateMappedStatement(UPDATE, sqlSource, SqlCommandType.UPDATE);
		}

		/**
		 * initDelete
		 */
		private void initDelete() {
			if (hasMappedStatement(DELETE)) {
				logger.warning("SqlMapper Delete Initialization exception");
				return;
			}
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, "${sql}", Map.class);
			newUpdateMappedStatement(DELETE, sqlSource, SqlCommandType.DELETE);
		}

	}
}