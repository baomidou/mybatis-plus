package com.baomidou.mybatisplus.activerecord;

import com.baomidou.mybatisplus.activerecord.dialect.Dialect;
import com.baomidou.mybatisplus.activerecord.exception.DBOpenException;
import com.baomidou.mybatisplus.activerecord.exception.IllegalTableNameException;
import com.baomidou.mybatisplus.activerecord.exception.SqlExecuteException;
import com.baomidou.mybatisplus.activerecord.exception.TransactionException;
import com.baomidou.mybatisplus.activerecord.exception.UnsupportedDatabaseException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 数据库对象。
 * 
 * @since 1.0
 * @author redraiment
 */
public class DB {

	private static final ServiceLoader<Dialect> dialects;
	static {
		dialects = ServiceLoader.load(Dialect.class);
	}

	public static DB open(DataSource pool) {
		if (null == pool) {
			return null;
		}
		try {
			Connection base = pool.getConnection();
			for (Dialect dialect : dialects) {
				if (dialect.accept(base)) {
					base.close();
					return new DB(pool, dialect);
				}
			}

			DatabaseMetaData meta = base.getMetaData();
			String version = String.format("%s %dialect.%dialect/%s", meta.getDatabaseProductName(), meta.getDatabaseMajorVersion(),
					meta.getDatabaseMinorVersion(), meta.getDatabaseProductVersion());
			throw new UnsupportedDatabaseException(version);
		} catch (SQLException e) {
			throw new DBOpenException(e);
		}
	}

	private final DataSource pool;
	private final InheritableThreadLocal<Connection> base;
	private final Dialect dialect;
	private final Map<String, Map<String, Integer>> columns;
	private final Map<String, Map<String, Association>> relations;
	private final Map<String, Map<String, Lambda>> hooks;

	public DB(DataSource pool, Dialect dialect) {
		this.pool = pool;
		this.base = new InheritableThreadLocal<Connection>();
		this.columns = new HashMap<String, Map<String, Integer>>();
		this.relations = new HashMap<String, Map<String, Association>>();
		this.dialect = dialect;
		this.hooks = new HashMap<String, Map<String, Lambda>>();
	}

	private Connection getConnection() {
		try {
			return base.get() == null ? pool.getConnection() : base.get();
		} catch (SQLException e) {
			throw new DBOpenException(e);
		}
	}

	void close(Connection c) {
		if (c != null && base.get() != c) {
			try {
				c.close();
			} catch (SQLException e) {
				throw new RuntimeException("close Connection fail", e);
			}
		}
	}

	void close(Statement s) {
		if (s != null) {
			try {
				Connection c = s.getConnection();
				s.close();
				close(c);
			} catch (SQLException e) {
				throw new RuntimeException("close Statement fail", e);
			}
		}
	}

	void close(ResultSet rs) {
		if (rs != null) {
			try {
				Statement s = rs.getStatement();
				rs.close();
				close(s);
			} catch (SQLException e) {
				throw new RuntimeException("close ResultSet fail", e);
			}
		}
	}

	public Set<String> getTableNames() {
		Set<String> tables = new HashSet<String>();
		try {
			Connection c = pool.getConnection();
			DatabaseMetaData db = c.getMetaData();
			ResultSet rs = db.getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next()) {
				tables.add(rs.getString("table_name"));
			}
		} catch (SQLException e) {
			throw new DBOpenException(e);
		}
		return tables;
	}

	public Set<Table> getTables() {
		Set<Table> tables = new HashSet<Table>();
		for (String name : getTableNames()) {
			tables.add(active(name));
		}
		return tables;
	}

	private Map<String, Integer> getColumns(String name) throws SQLException {
		if (!columns.containsKey(name)) {
			synchronized (columns) {
				if (!columns.containsKey(name)) {
					String catalog, schema, table;
					String[] patterns = name.split("\\.");
					if (patterns.length == 1) {
						catalog = null;
						schema = null;
						table = patterns[0];
					} else if (patterns.length == 2) {
						catalog = null;
						schema = patterns[0];
						table = patterns[1];
					} else if (patterns.length == 3) {
						catalog = patterns[0];
						schema = patterns[1];
						table = patterns[2];
					} else {
						throw new IllegalArgumentException(String.format("Illegal table name: %s", name));
					}

					Map<String, Integer> column = new LinkedHashMap<String, Integer>();
					Connection c = pool.getConnection();
					DatabaseMetaData db = c.getMetaData();
					ResultSet rs = db.getColumns(catalog, schema, table, null);
					while (rs.next()) {
						String columnName = rs.getString("column_name");
						if (columnName.equalsIgnoreCase("id") || columnName.equalsIgnoreCase("created_at")
								|| columnName.equalsIgnoreCase("updated_at")) {
							continue;
						}
						column.put(parseKeyParameter(columnName), rs.getInt("data_type"));
					}
					columns.put(name, column);
				}
			}
		}
		return columns.get(name);
	}

	public Table active(String name) {
		name = dialect.getCaseIdentifier(name);

		if (!relations.containsKey(name)) {
			synchronized (relations) {
				if (!relations.containsKey(name)) {
					relations.put(name, new HashMap<String, Association>());
				}
			}
		}

		if (!hooks.containsKey(name)) {
			synchronized (hooks) {
				if (!hooks.containsKey(name)) {
					hooks.put(name, new HashMap<String, Lambda>());
				}
			}
		}

		try {
			return new Table(this, name, getColumns(name), relations.get(name), hooks.get(name));
		} catch (SQLException e) {
			throw new IllegalTableNameException(name, e);
		}
	}

	public PreparedStatement prepare(String sql, Object[] params, int[] types) {
		Connection c = getConnection();
		try {
			PreparedStatement call;
			if (sql.trim().toLowerCase().startsWith("insert")) {
				call = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				call = c.prepareStatement(sql);
			}

			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					if (params[i] != null) {
						call.setObject(i + 1, params[i]);
					} else {
						call.setNull(i + 1, types[i]);
					}
				}
			}
			return call;
		} catch (SQLException e) {
			throw new SqlExecuteException(sql, e);
		}
	}

	public void execute(String sql, Object[] params, int[] types) {
		PreparedStatement call = prepare(sql, params, types);
		try {
			call.executeUpdate();
		} catch (SQLException e) {
			throw new SqlExecuteException(sql, e);
		} finally {
			close(call);
		}
	}

	public void execute(String sql) {
		execute(sql, null, null);
	}

	public ResultSet query(String sql, Object... params) {
		try {
			PreparedStatement call = prepare(sql, params, null);
			return call.executeQuery();
		} catch (SQLException e) {
			throw new SqlExecuteException(sql, e);
		}
	}

	public Table createTable(String name, String... columns) {
		String template = "create table %s (id %s, %s, created_at timestamp, updated_at timestamp)";
		execute(String.format(template, name, dialect.getIdentity(), Seq.join(Arrays.asList(columns), ", ")));
		return active(name);
	}

	public void dropTable(String name) {
		execute(String.format("drop table if exists %s", name));
	}

	public void createIndex(String name, String table, String... columns) {
		execute(String.format("create index %s on %s(%s)", name, table, Seq.join(Arrays.asList(columns), ", ")));
	}

	public void dropIndex(String name, String table) {
		execute(String.format("drop index %s", name));
	}

	/* Transaction */
	public void batch(Runnable transaction) {
		// TODO: 不支持嵌套事务
		try {
			Connection c = pool.getConnection();
			boolean commit = c.getAutoCommit();
			try {
				c.setAutoCommit(false);
			} catch (SQLException e) {
				throw new TransactionException("transaction setAutoCommit(false)", e);
			}
			base.set(c);

			try {
				transaction.run();
			} catch (RuntimeException e) {
				try {
					c.rollback();
					c.setAutoCommit(commit);
				} catch (SQLException ex) {
					throw new TransactionException("transaction rollback: " + ex.getMessage(), e);
				}
				throw e;
			}

			try {
				c.commit();
			} catch (SQLException e) {
				throw new TransactionException("transaction commit", e);
			}
			c.setAutoCommit(commit);
		} catch (SQLException e) {
			throw new DBOpenException(e);
		} finally {
			base.set(null);
		}
	}

	public boolean tx(Runnable transaction) {
		try {
			batch(transaction);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	/* Utility */
	public static Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	static String parseKeyParameter(String name) {
		name = name.toLowerCase();
		if (name.endsWith(":")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}
}
