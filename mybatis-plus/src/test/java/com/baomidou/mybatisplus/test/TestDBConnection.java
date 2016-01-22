package com.baomidou.mybatisplus.test;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class TestDBConnection {

	protected static final Logger logger = Logger.getLogger("DBConnection");

	private static final String DB_CONFIG = "dbconfig.properties";

	private DruidDataSource dds = null;


	public void initDruidDataSourceFactory() {
		try {
			dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(getInputStream(DB_CONFIG));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}


	public DruidDataSource getDruidDataSource() {
		return dds;
	}


	private static Properties getInputStream( String cfg ) {
		return getInputStream(TestDBConnection.class.getClassLoader().getResourceAsStream(cfg));
	}


	private static Properties getInputStream( InputStream in ) {
		Properties p = null;
		try {
			p = new Properties();
			p.load(in);
		} catch ( Exception e ) {
			logger.severe(" kisso read config file error. ");
			e.printStackTrace();
		}
		return p;
	}
}
