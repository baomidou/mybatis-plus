package com.baomidou.mybatisplus.test.generator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;

public class DB2PageSelectTest {
	public static void main(String[] args) {
		String dbUrl = "jdbc:db2://192.168.0.227:50000/TRADECTR";
		 DataSourceConfig dataSourceConfig = new DataSourceConfig();
	        dataSourceConfig.setDbType(DbType.DB2)
	                .setUrl(dbUrl)
	                .setUsername("tc")
	                .setPassword("business")
	                .setDriverName("com.ibm.db2.jcc.DB2Driver");
	        Connection conn = dataSourceConfig.getConn();
	        //我们项目中得2.1.9版本自动生成的分页查询sql，此处直接复制sql过来执行
	        String page1 = "select * from ( select rownumber() over() as rownumber_, id AS id,name,age,address,birthday,state FROM user WHERE state='U' ) as temp_ where rownumber_ <= 10";
	        String page2 = "select * from ( select rownumber() over() as rownumber_, id AS id,name,age,address,birthday,state FROM user WHERE state='U' ) as temp_ where rownumber_ between 10+1 and 10+10";
	        Statement stmt;
			try {
				stmt = conn.createStatement();
//				DatabaseMetaData dbmd=(DatabaseMetaData) conn.getMetaData();
				stmt.execute(page1);
				ResultSet rs = stmt.getResultSet();
				while(rs.next()){
			        int id = rs.getInt(1);
			        String name = rs.getString(2);
			        String gender = rs.getString(3);
			        System.out.println("id:"+id+" 年龄："+name+" 姓名："+gender);
			    }
				System.out.println("----------第二页数据-----------");
				stmt.execute(page2);
				rs = stmt.getResultSet();
				while(rs.next()){
			        int id = rs.getInt(1);
			        String name = rs.getString(2);
			        String gender = rs.getString(3);
			        System.out.println("id:"+id+" 年龄："+name+" 姓名："+gender);
			    }
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
