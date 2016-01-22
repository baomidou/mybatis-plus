package com.baomidou.mybatisplus.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.handler.BeanHandler;
import com.baomidou.mybatisplus.mapper.SimpleMapper;

public class TestMybatisPlus {

	private static DruidDataSource getDataSource() {
		TestDBConnection dbd = new TestDBConnection();
		dbd.initDruidDataSourceFactory();
		return dbd.getDruidDataSource();
	}


	/**
	 * 测试
	 */
	public static void main( String[] args ) {
		SimpleMapper memory = new SimpleMapper(getDataSource());
		
		/**
		 * 创建一条记录
		 */
		TestUser user = new TestUser();
		user.setName("tom");
		user.setAge(6);
		System.out.print("入库之前Product没有ID：");
		memory.insert(TestUser.class, user);

		/**
		* 读取这条记录
		*/
		user = memory.select(TestUser.class, user.getId());
		System.out.print("使用CRUD的read方法读取：");
		System.out.println(user);

		/**
		* 换一种方式读取
		*/
		String mySql = "select * from test_user where id = ?";
		user = memory.query(mySql, new BeanHandler<TestUser>(TestUser.class), user.getId());
		System.out.print("使用CQRS的query方法读取：");
		System.out.println(user);

		/**
		* 更新这条记录
		*/
		user.setAge(7);
		memory.update(TestUser.class, user);

		// 查看结果
		user = memory.select(TestUser.class, user.getId());
		System.out.print("查看更新结果：");
		System.out.println(user);

		/**
		* 删除一条记录
		*/
		memory.delete(TestUser.class, user.getId());
		// 查看结果
		user = memory.select(TestUser.class, user.getId());
		System.out.print("查看删除结果：");
		System.out.println(user);
	}
	
}
