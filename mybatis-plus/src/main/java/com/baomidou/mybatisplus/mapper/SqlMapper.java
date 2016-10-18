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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Mybatis执行sql工具,主要为AR方式调用
 *
 * @author Caratacus
 * @since 2016-10-18
 */
public class SqlMapper {

    protected static final Logger logger = Logger.getLogger("SqlMapper");
    private Map<String, Object> sqlMap = new HashMap<String, Object>();
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
     * @param list List结果
     * @param <T>  泛型类型
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
     * @param sql 执行的sql
     * @return
     */
    public Map<String, Object> selectOne(String sql) {
        List<Map<String, Object>> list = selectList(sql);
        return getOne(list);
    }

    /**
     * 查询返回List<Map<String, Object>>
     *
     * @param sql 执行的sql
     * @return
     */
    public List<Map<String, Object>> selectList(String sql) {
        sqlMap.put("sql", sql);
        String msId = msUtils.select();
        return sqlSession.selectList(msId, sqlMap);
    }

    /**
     * 插入数据
     *
     * @param sql 执行的sql
     * @return
     */
    public boolean insert(String sql) {
        sqlMap.put("sql", sql);
        String msId = msUtils.insert();
        return retBool(sqlSession.insert(msId, sqlMap));
    }

    /**
     * 更新数据
     *
     * @param sql 执行的sql
     * @return
     */
    public boolean update(String sql) {
        sqlMap.put("sql", sql);
        String msId = msUtils.update();
        return retBool(sqlSession.update(msId, sqlMap));
    }

    /**
     * 删除数据
     *
     * @param sql 执行的sql
     * @return
     */
    public boolean delete(String sql) {
        sqlMap.put("sql", sql);
        String msId = msUtils.delete();
        return retBool(sqlSession.delete(msId, sqlMap));
    }

    /**
     * 判断数据库操作是否成功
     *
     * @param result 数据库操作返回影响条数
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
            languageDriver = configuration.getDefaultScriptingLanuageInstance();
        }

        /**
         * 创建MSID
         *
         * @param sql 执行的sql
         * @param sql 执行的sqlCommandType
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
         * @param sqlSource  执行的sqlSource
         * @param resultType 返回的结果类型
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
         * @param sqlSource      执行的sqlSource
         * @param sqlCommandType 执行的sqlCommandType
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

        private String select() {
            String msId = newMsId(getMsName("select"), SqlCommandType.SELECT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, "${sql}", Map.class);
            newSelectMappedStatement(msId, sqlSource, Map.class);
            return msId;
        }

        private String insert() {
            String msId = newMsId(getMsName("insert"), SqlCommandType.INSERT);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, "${sql}", String.class);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
            return msId;
        }

        private String update() {
            String msId = newMsId(getMsName("update"), SqlCommandType.UPDATE);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, "<script>${sql}</script>", String.class);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
            return msId;
        }

        private String delete() {
            String msId = newMsId(getMsName("delete"), SqlCommandType.DELETE);
            if (hasMappedStatement(msId)) {
                return msId;
            }
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, "<script>${sql}</script>", String.class);
            newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
            return msId;
        }

        /**
         * 获取Ms名称
         *
         * @param name
         * @return
         */
        private String getMsName(String name) {
            return new StringBuilder(getClass().getName()).append(name).toString();
        }

    }
}