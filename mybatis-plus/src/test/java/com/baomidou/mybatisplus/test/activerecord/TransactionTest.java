package com.baomidou.mybatisplus.test.activerecord;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baomidou.mybatisplus.activerecord.DB;
import com.baomidou.mybatisplus.activerecord.Record;
import com.baomidou.mybatisplus.activerecord.Table;

public class TransactionTest {

	private DB dbo;

	@Before
	public void setUp() {
		dbo = DB.open("jdbc:sqlite::memory:");
		dbo.createTable("tweets", "zombie_id int", "content varchar(64) not null unique");
		Table Zombie = dbo.createTable("zombies", "name varchar(64)");
		Zombie.hasMany("tweets").by("zombie_id");
	}

	@After
	public void tearDown() {
		if (dbo != null) {
			dbo = null;
		}
	}

	@Test
	public void testTx() {
		final Table Zombie = dbo.active("zombies");
		Table Tweet = dbo.active("tweets");

		dbo.tx(new Runnable() {
			@Override
			public void run() {
				Record ash = Zombie.create("name:", "Ash");
				Table tweets = ash.get("tweets");
				tweets.create("content:", "Hello world");
			}
		});
		Assert.assertEquals(1, Zombie.all().size());
		Assert.assertEquals(1, Tweet.all().size());

		Assert.assertFalse(dbo.tx(new Runnable() {
			@Override
			public void run() {
				Record bob = Zombie.create("name:", "Bob");
				Table tweets = bob.get("tweets");
				tweets.create("content:", "Hello world");
			}
		}));
		Assert.assertEquals(1, Zombie.all().size());
		Assert.assertEquals(1, Tweet.all().size());

		Record ash = Zombie.find(1);
		Table tweets = ash.get("tweets");
		tweets.create("content:", "Hello ash");
		Assert.assertEquals(1, Zombie.all().size());
		Assert.assertEquals(2, Tweet.all().size());
	}
}
