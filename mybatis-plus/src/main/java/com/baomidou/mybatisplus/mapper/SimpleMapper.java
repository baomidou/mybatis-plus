package com.baomidou.mybatisplus.mapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import com.baomidou.mybatisplus.handler.BeanHandler;
import com.baomidou.mybatisplus.handler.PreparedStatementHandler;
import com.baomidou.mybatisplus.handler.ResultSetHandler;


/**
 * 
 */
public class SimpleMapper {

	private DataSource ds;

	private boolean sequence;

	private PreparedStatementHandler psh;


	public SimpleMapper( DataSource ds ) {
		this.ds = ds;
		this.psh = PreparedStatementHandler.getInstance();
		sequence();
	}


	/**
	 * 判断是否使用序列（Oracle使用序列做主键，MySQL使用自增主键)
	 */
	private void sequence() {
		Connection conn = this.getConnection();
		try {
			sequence = conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle");
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			this.close(conn);
		}
	}


	public <T> T query( StringBuffer sql, ResultSetHandler<T> rsh, List<Object> params ) {
		return this.query(this.getConnection(), sql, rsh, params);
	}


	public <T> T query( String sql, ResultSetHandler<T> rsh, Object... params ) {
		return this.query(this.getConnection(), sql, rsh, params);
	}


	public <T> T query( Connection conn, StringBuffer sql, ResultSetHandler<T> rsh, List<Object> params ) {
		return this.query(conn, sql.toString(), rsh,
			params == null ? new Object[ ] {} : params.toArray(new Object[ ] {}));
	}


	public <T> T query( Connection conn, String sql, ResultSetHandler<T> rsh, Object... params ) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		T result = null;
		try {
			sql = psh.adjust(sequence, sql, params);
			stmt = conn.prepareStatement(sql);
			this.fillStatement(stmt, params);
			rs = stmt.executeQuery();
			result = rsh.handle(rs);
		} catch ( SQLException e ) {
			psh.print(sql, params);
			throw new RuntimeException(e);
		} finally {
			close(rs, stmt, conn);
		}
		return result;
	}


	public int update( StringBuffer sql, List<Object> params ) {
		return this.update(sql.toString(), params.toArray(new Object[ ] {}));
	}


	public int update( String sql, Object... params ) {
		return this.update(this.getConnection(), sql, params);
	}


	public int update( Connection conn, StringBuffer sql, List<Object> params ) {
		return this.update(conn, sql.toString(), params.toArray(new Object[ ] {}));
	}


	public int update( Connection conn, String sql, Object... params ) {
		PreparedStatement stmt = null;
		int rows = 0;
		try {
			sql = psh.adjust(sequence, sql, params);
			stmt = conn.prepareStatement(sql);
			this.fillStatement(stmt, params);
			rows = stmt.executeUpdate();
		} catch ( SQLException e ) {
			psh.print(sql, params);
			throw new RuntimeException(e);
		} finally {
			close(stmt, conn);
		}
		return rows;
	}


	public int[] batch( String sql, Object[][] params ) {
		return this.batch(this.getConnection(), sql, params);
	}


	public int[] batch( Connection conn, String sql, Object[][] params ) {
		PreparedStatement stmt = null;
		int[] rows = null;
		try {
			conn.setAutoCommit(false);
			sql = psh.adjustSQL(sequence, sql, params[0]);
			stmt = conn.prepareStatement(sql);
			for ( int i = 0 ; i < params.length ; i++ ) {
				psh.adjustParams(params[i]);
				this.fillStatement(stmt, params[i]);
				stmt.addBatch();
			}
			rows = stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			close(stmt, conn);
		}
		return rows;
	}


	public <T> int insert( Class<T> cls, T bean ) {
		return this.insert(this.getConnection(), cls, bean);
	}


	public <T> int insert( Class<T> cls, T bean, boolean customKey ) {
		return this.insert(this.getConnection(), cls, bean, customKey);
	}


	public <T> int insert( Connection conn, Class<T> cls, T bean ) {
		return this.insert(conn, cls, bean, false);
	}


	public <T> int insert( Connection conn, Class<T> cls, T bean, boolean customKey ) {
		int rows = 0;
		PreparedStatement stmt = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

			String table = camel2underscore(cls.getSimpleName());
			String columns = "", questionMarks = "";

			Object[] params = customKey ? new Object[pds.length] : new Object[pds.length - 1];

			int j = 0;
			for ( PropertyDescriptor pd : pds ) {
				Method getter = pd.getReadMethod();
				String name = pd.getName();
				Object value = getter.invoke(bean);
				/**
				 * 非自定义主键，则ID作为主键且使用序列或自增主键
				 */
				if ( !customKey && name.equals("id") ) {
					if ( sequence ) {
						columns += "id,";
						questionMarks += table + "_SEQ.NEXTVAL,";
					}
				} else {
					columns += camel2underscore(name) + ",";
					questionMarks += "?,";
					params[j] = value;
					j++;
				}
			}
			columns = columns.substring(0, columns.length() - 1);
			questionMarks = questionMarks.substring(0, questionMarks.length() - 1);
			String sql = String.format("insert into %s (%s) values (%s)", table, columns, questionMarks);

			sql = psh.adjust(sequence, sql, params);

			/**
			 * 如果使用非自定义主键，则返回主键ID的值
			 */
			if ( !customKey ) {
				if ( sequence ) {
					String generatedColumns[] = { "id" };
					stmt = conn.prepareStatement(sql, generatedColumns);
				} else {
					stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				}
			} else {
				stmt = conn.prepareStatement(sql);
			}

			this.fillStatement(stmt, params);
			try {
				rows = stmt.executeUpdate();
			} catch ( SQLException e ) {
				psh.print(sql, params);
				throw new RuntimeException(e);
			}
			/**
			 * 如果使用非自定义主键，则返回主键ID的值
			 */
			if ( !customKey ) {
				ResultSet rs = stmt.getGeneratedKeys();
				long id = 0;
				if ( rs.next() ) {
					id = rs.getLong(1);
				}

				for ( PropertyDescriptor pd : pds ) {
					String name = pd.getName();
					if ( name.equals("id") ) {
						Method setter = pd.getWriteMethod();
						setter.invoke(bean, id);
						break;
					}
				}
			}
		} catch ( IllegalArgumentException e ) {
			throw new RuntimeException(e);
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} catch ( IntrospectionException e ) {
			throw new RuntimeException(e);
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException(e);
		} finally {
			close(stmt, conn);
		}
		return rows;
	}


	public <T> int[] create( Class<T> cls, List<T> beans ) {
		return create(this.getConnection(), cls, beans, false);
	}


	public <T> int[] create( Class<T> cls, List<T> beans, boolean customKey ) {
		return create(this.getConnection(), cls, beans, customKey);
	}


	public <T> int[] create( Connection conn, Class<T> cls, List<T> beans ) {
		return create(conn, cls, beans, false);
	}


	public <T> int[] create( Connection conn, Class<T> cls, List<T> beans, boolean customKey ) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(cls, Object.class);
		} catch ( IntrospectionException e ) {
			throw new RuntimeException(e);
		}
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

		// build SQL
		String table = camel2underscore(cls.getSimpleName());
		String columns = "", questionMarks = "";

		for ( PropertyDescriptor pd : pds ) {
			String name = pd.getName();
			if ( !customKey && name.equals("id") ) {
				if ( sequence ) {
					columns += "id,";
					questionMarks += table + "_SEQ.NEXTVAL,";
				}
			} else {
				columns += camel2underscore(name) + ",";
				questionMarks += "?,";
			}
		}
		columns = columns.substring(0, columns.length() - 1);
		questionMarks = questionMarks.substring(0, questionMarks.length() - 1);
		String sql = String.format("insert into %s (%s) values (%s)", table, columns, questionMarks);

		// build parameters */
		int rows = beans.size();
		int cols = customKey ? pds.length : pds.length - 1;

		Object[][] params = new Object[rows][cols];
		try {
			for ( int i = 0 ; i < rows ; i++ ) {
				int j = 0;
				for ( PropertyDescriptor pd : pds ) {
					Method getter = pd.getReadMethod();
					String name = pd.getName();
					Object value = getter.invoke(beans.get(i));
					if ( !customKey && name.equals("id") ) {
						continue;
					}
					params[i][j] = value;
					j++;
				}
			}
		} catch ( IllegalArgumentException e ) {
			throw new RuntimeException(e);
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException(e);
		}
		// execute
		return batch(conn, sql, params);
	}


	public <T> T select( Class<T> cls, long id ) {
		return this.select(this.getConnection(), cls, id);
	}


	public <T> T select( Connection conn, Class<T> cls, long id ) {
		String table = camel2underscore(cls.getSimpleName());
		return (T) query(conn, "select * from " + table + " where id=?", new BeanHandler<T>(cls), id);
	}


	public <T> int update( Class<T> cls, T bean ) {
		return this.update(cls, bean, "id");
	}


	public <T> int update( Connection conn, Class<T> cls, T bean ) {
		return this.update(conn, cls, bean, "id");
	}


	public <T> int update( Class<T> cls, T bean, String primaryKey ) {
		return this.update(this.getConnection(), cls, bean, primaryKey);
	}


	public <T> int update( Connection conn, Class<T> cls, T bean, String primaryKey ) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

			Object[] params = new Object[pds.length];
			primaryKey = underscore2camel(primaryKey);
			Object id = 0;
			String columnAndQuestionMarks = "";
			int j = 0;
			for ( PropertyDescriptor pd : pds ) {
				Method getter = pd.getReadMethod();
				String name = pd.getName();
				Object value = getter.invoke(bean);
				if ( name.equals(primaryKey) ) {
					id = value;
				} else {
					columnAndQuestionMarks += camel2underscore(name) + "=?,";
					params[j] = value;
					j++;
				}
			}
			params[j] = id;
			String table = camel2underscore(cls.getSimpleName());
			columnAndQuestionMarks = columnAndQuestionMarks.substring(0, columnAndQuestionMarks.length() - 1);
			String sql = String.format("update %s set %s where %s = ?", table, columnAndQuestionMarks,
				camel2underscore(primaryKey));
			return update(conn, sql, params);
		} catch ( IllegalArgumentException e ) {
			throw new RuntimeException(e);
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException(e);
		} catch ( IntrospectionException e ) {
			throw new RuntimeException(e);
		}
	}


	public <T> int[] update( Class<T> cls, List<T> beans ) {
		return this.update(cls, beans, "id");
	}


	public <T> int[] update( Connection conn, Class<T> cls, List<T> beans ) {
		return this.update(conn, cls, beans, "id");
	}


	public <T> int[] update( Class<T> cls, List<T> beans, String primaryKey ) {
		return this.update(this.getConnection(), cls, beans, primaryKey);
	}


	public <T> int[] update( Connection conn, Class<T> cls, List<T> beans, String primaryKey ) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

			primaryKey = underscore2camel(primaryKey);
			String columnAndQuestionMarks = "";

			for ( PropertyDescriptor pd : pds ) {
				String name = pd.getName();
				if ( name.equals(primaryKey) ) {} else {
					columnAndQuestionMarks += camel2underscore(name) + "=?,";
				}
			}
			String table = camel2underscore(cls.getSimpleName());
			columnAndQuestionMarks = columnAndQuestionMarks.substring(0, columnAndQuestionMarks.length() - 1);
			String sql = String.format("update %s set %s where %s = ?", table, columnAndQuestionMarks,
				camel2underscore(primaryKey));

			// build parameters
			int rows = beans.size();
			int cols = pds.length;
			Object id = 0;
			Object[][] params = new Object[rows][cols];
			for ( int i = 0 ; i < rows ; i++ ) {
				int j = 0;
				for ( PropertyDescriptor pd : pds ) {
					Method getter = pd.getReadMethod();
					String name = pd.getName();
					Object value = getter.invoke(beans.get(i));
					if ( name.equals(primaryKey) ) {
						id = value;
					} else {
						params[i][j] = value;
						j++;
					}
				}
				params[i][j] = id;
			}
			return batch(conn, sql, params);
		} catch ( IllegalArgumentException e ) {
			throw new RuntimeException(e);
		} catch ( IllegalAccessException e ) {
			throw new RuntimeException(e);
		} catch ( InvocationTargetException e ) {
			throw new RuntimeException(e);
		} catch ( IntrospectionException e ) {
			throw new RuntimeException(e);
		}
	}


	public <T> int delete( Class<T> cls, long id ) {
		return this.delete(this.getConnection(), cls, id);
	}


	public <T> int delete( Connection conn, Class<T> cls, long id ) {
		String sql = String.format("delete from %s where id=?", camel2underscore(cls.getSimpleName()));
		return update(conn, sql, new Object[ ] { id });
	}


	public void pager( StringBuffer sql, List<Object> params, int pageSize, int pageNo ) {
		psh.pager(sequence, sql, params, pageSize, pageNo);
	}


	public <T> void in( StringBuffer sql, List<Object> params, String operator, String field, List<T> values ) {
		psh.in(sequence, sql, params, operator, field, values);
	}


	public Connection getConnection() {
		try {
			return this.ds.getConnection();
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}


	private void fillStatement( PreparedStatement stmt, Object... params ) {
		if ( params == null ) return;
		try {
			for ( int i = 0 ; i < params.length ; i++ ) {
				// hack oracle's bug (version <= 9)
				if ( sequence && params[i] == null ) {
					stmt.setNull(i + 1, Types.VARCHAR);

				} else {
					stmt.setObject(i + 1, params[i]);
				}
			}
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}


	private void close( ResultSet rs, Statement stmt, Connection conn ) {
		try {
			if ( rs != null ) {
				rs.close();
			}
			close(stmt, conn);
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}


	private void close( Statement stmt, Connection conn ) {
		try {
			if ( stmt != null ) {
				stmt.close();
			}
			close(conn);
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}


	private void close( Connection conn ) {
		try {
			if ( conn != null && conn.getAutoCommit() ) {
				conn.close();
			}
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		}
	}


	private String camel2underscore( String camel ) {
		return psh.camel2underscore(camel);
	}


	private String underscore2camel( String underscore ) {
		return psh.underscore2camel(underscore);
	}
}