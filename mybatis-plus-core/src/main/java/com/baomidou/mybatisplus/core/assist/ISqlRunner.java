package com.baomidou.mybatisplus.core.assist;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.core.pagination.Page;

/**
 * <p>
 * </p>
 *
 * @author yuxiaobin
 * @date 2018/2/7
 */
public abstract class ISqlRunner {

    public static final String INSERT = "com.baomidou.mybatisplus.mapper.SqlRunner.Insert";
    public static final String DELETE = "com.baomidou.mybatisplus.mapper.SqlRunner.Delete";
    public static final String UPDATE = "com.baomidou.mybatisplus.mapper.SqlRunner.Update";
    public static final String SELECT_LIST = "com.baomidou.mybatisplus.mapper.SqlRunner.SelectList";
    public static final String SELECT_OBJS = "com.baomidou.mybatisplus.mapper.SqlRunner.SelectObjs";
    public static final String COUNT = "com.baomidou.mybatisplus.mapper.SqlRunner.Count";
    public static final String SQLScript = "${sql}";
    public static final String SQL = "sql";

    public static SqlSessionFactory FACTORY;

    public abstract boolean insert(String sql, Object... args);

    public abstract boolean delete(String sql, Object... args);

    public abstract boolean update(String sql, Object... args);

    public abstract List<Map<String, Object>> selectList(String sql, Object... args);

    public abstract List<Object> selectObjs(String sql, Object... args);

    public abstract Object selectObj(String sql, Object... args);

    public abstract int selectCount(String sql, Object... args);

    public abstract Map<String, Object> selectOne(String sql, Object... args);

    public abstract Page<Map<String, Object>> selectPage(Page page, String sql, Object... args);
}
