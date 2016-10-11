package com.baomidou.mybatisplus.test.activerecord;

import java.util.Scanner;

import com.baomidou.mybatisplus.activerecord.DB;
import com.baomidou.mybatisplus.activerecord.Table;
import com.baomidou.mybatisplus.activerecord.pool.JdbcDataSource;

public class CountActiveConnection {

	public static void main(String[] args) throws InterruptedException {
		DB dbo = DB.open(new JdbcDataSource("jdbc:postgresql:code", "postgres", "123"));
		final Table People = dbo.active("person");
		for (int i = 0; i < 10; i++) {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					People.first();
				}
			});
			th.start();
			th.join();
		}
		System.out.println("pause");
		Scanner cin = new Scanner(System.in);
		cin.next();
		cin.close();
	}

}
