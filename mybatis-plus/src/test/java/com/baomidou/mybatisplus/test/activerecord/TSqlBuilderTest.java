package com.baomidou.mybatisplus.test.activerecord;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.baomidou.mybatisplus.activerecord.sql.SqlBuilder;
import com.baomidou.mybatisplus.activerecord.sql.TSqlBuilder;

public class TSqlBuilderTest {
	private static SqlBuilder sql = null;

	@BeforeClass
	public static void setUpClass() {
		sql = new TSqlBuilder();
	}

	@Test
	public void testSelectAllFrom() {
		assertEquals("select * from users", sql.select().from("users").toString());
		assertEquals("select * from users", sql.select("*").from("users").toString());
	}

	@Test
	public void testSelectSomeFrom() {
		assertEquals("select name, age from users", sql.select("name", "age").from("users").toString());
	}

	@Test
	public void testWhere() {
		sql.select().from("users").where("age > 17");
		assertEquals("select * from users where age > 17", sql.toString());
		sql.select().from("users").where("age >= 18 and age <= 25");
		assertEquals("select * from users where age >= 18 and age <= 25", sql.toString());
	}

	@Test
	public void testHaving() {
		sql.select("sum(age) as sum", "count(*) as count").from("users").groupBy("age").having("count(*) > 2");
		assertEquals("select sum(age) as sum, count(*) as count from users group by age having count(*) > 2",
				sql.toString());
		sql.select().from("users").having("count(*) > 2");
		assertEquals("select * from users", sql.toString());
		sql.select("age", "count(*)").from("users").groupBy("age");
		assertEquals("select age, count(*) from users group by age", sql.toString());
	}

	@Test
	public void testSort() {
		sql.select().from("users").orderBy("age");
		assertEquals("select * from users order by age", sql.toString());
		sql.select().from("users").orderBy("age asc");
		assertEquals("select * from users order by age asc", sql.toString());
		sql.select().from("users").orderBy("age desc");
		assertEquals("select * from users order by age desc", sql.toString());
		sql.select().from("users").orderBy("age", "name");
		assertEquals("select * from users order by age, name", sql.toString());
	}

	@Test
	public void testPaging() {
		sql.select().from("users").limit(10);
		assertEquals("select * from users limit 10", sql.toString());
		sql.select().from("users").offset(10);
		assertEquals("select * from users offset 10", sql.toString());
		sql.select().from("users").limit(10).offset(20);
		assertEquals("select * from users limit 10 offset 20", sql.toString());
		sql.select().from("users").offset(20).limit(10);
		assertEquals("select * from users limit 10 offset 20", sql.toString());
	}

	@Test
	public void testQuery() {
		assertEquals(
				"select age, count(*) from users where id > 1 and age >= 18 group by age having count(*) > 2 order by age desc limit 10 offset 100",
				sql.select("age", "count(*)").from("users").where("id > 1", "age >= 18").groupBy("age")
						.having("count(*) > 2").orderBy("age desc").limit(10).offset(100).toString());
	}

	@Test
	public void testUpdate() {
		assertEquals("update users set age = ?", sql.update("users").set("age").toString());
		assertEquals("update users set name = ?, age = ? where id = ?",
				sql.update("users").set("name", "age").where("id = ?").toString());
		assertEquals("update users set age = ? where id > ? and name = ?",
				sql.update("users").set("age").where("id > ?", "name = ?").toString());
	}

	@Test
	public void testDelete() {
		assertEquals("delete from users", sql.delete().from("users").toString());
		assertEquals("delete from users where id > 2 and name = ?",
				sql.delete().from("users").where("id > 2", "name = ?").toString());
	}

	@Test
	public void testInsert() {
		assertEquals("insert into users (name, age) values (?, ?)",
				sql.insert().into("users").values("name", "age").toString());
	}
}
