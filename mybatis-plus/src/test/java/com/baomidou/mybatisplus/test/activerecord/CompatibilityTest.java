package com.baomidou.mybatisplus.test.activerecord;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.baomidou.mybatisplus.activerecord.DB;
import com.baomidou.mybatisplus.activerecord.Record;
import com.baomidou.mybatisplus.activerecord.Table;

public class CompatibilityTest {

	private Table Zombie, Tweet, Comment, City, Relation;
	private DB dbo;

	public void setUpTables() {
		Zombie = dbo.createTable("zombies", "name varchar(64)");
		City = dbo.createTable("cities", "name varchar(64)");
		Tweet = dbo.createTable("tweets", "zombie_id int", "city_id int", "content varchar(64)");
		Comment = dbo.createTable("comments", "zombie_id int", "tweet_id int", "content varchar(64)");
		Relation = dbo.createTable("relations", "following int", "follower int");
	}

	public void setUpAssociations() {
		Zombie.hasMany("tweets").by("zombie_id");
		Zombie.hasAndBelongsToMany("travelled_cities").by("city_id").in("cities").through("tweets");
		Zombie.hasMany("received_comments").by("tweet_id").in("comments").through("tweets");
		Zombie.hasMany("send_comments").by("zombie_id").in("comments");
		Zombie.hasMany("follower_relations").by("following").in("relations");
		Zombie.hasAndBelongsToMany("followers").by("follower").in("zombies").through("follower_relations");
		Zombie.hasMany("following_relations").by("follower").in("relations");
		Zombie.hasAndBelongsToMany("followings").by("following").in("zombies").through("following_relations");

		City.hasMany("tweets").by("city_id");
		City.hasAndBelongsToMany("zombies").by("zombie_id").through("tweets");

		Tweet.belongsTo("zombie").in("zombies");
		Tweet.belongsTo("city").in("cities");
		Tweet.hasMany("comments").by("tweet_id");

		Comment.belongsTo("zombie").by("zombie_id").in("zombies");
		Comment.belongsTo("tweet").by("tweet_id").in("tweets");
	}

	public void setUpMetaData() {
		Record boston = City.create("name:", "Boston");
		Record newyord = City.create("name:", "NewYork");

		Record ash = Zombie.create("name:", "Ash");
		Table ashTweets = ash.get("tweets");
		Table ashTweetOnBoston = ashTweets.create("city_id:", boston.getInt("id"), "content:", "Hello Boston from Ash!")
				.get("comments");
		Table ashTweetOnNewYork = ashTweets
				.create("city_id:", newyord.getInt("id"), "content:", "Hello NewYord from Ash!").get("comments");

		Record bob = Zombie.create("name:", "Bob");
		Table bobTweets = bob.get("tweets");
		Table bobTweetOnBoston = bobTweets.create("city_id:", boston.getInt("id"), "content:", "Hello Boston from Bob!")
				.get("comments");
		Table bobTweetOnNewYork = bobTweets
				.create("city_id:", newyord.getInt("id"), "content:", "Hello NewYord from Bob!").get("comments");

		Record jim = Zombie.create("name:", "Jim");
		Table jimTweets = jim.get("tweets");
		Table jimTweetOnBoston = jimTweets.create("city_id:", boston.getInt("id"), "content:", "Hello Boston from Jim!")
				.get("comments");
		Table jimTweetOnNewYork = jimTweets
				.create("city_id:", newyord.getInt("id"), "content:", "Hello NewYord from Jim!").get("comments");

		ashTweetOnBoston.create("zombie_id:", bob.getInt("id"), "content:", "Cool from Bob @ Boston");
		ashTweetOnBoston.create("zombie_id:", jim.getInt("id"), "content:", "Cool from Jim @ Boston");
		ashTweetOnNewYork.create("zombie_id:", bob.getInt("id"), "content:", "Cool from Bob @ NewYork");
		ashTweetOnNewYork.create("zombie_id:", jim.getInt("id"), "content:", "Cool from Jim @ NewYork");
		bobTweetOnBoston.create("zombie_id:", ash.getInt("id"), "content:", "Cool from Ash @ Boston");
		bobTweetOnBoston.create("zombie_id:", jim.getInt("id"), "content:", "Cool from Jim @ Boston");
		bobTweetOnNewYork.create("zombie_id:", ash.getInt("id"), "content:", "Cool from Ash @ NewYork");
		bobTweetOnNewYork.create("zombie_id:", jim.getInt("id"), "content:", "Cool from Jim @ NewYork");
		jimTweetOnBoston.create("zombie_id:", ash.getInt("id"), "content:", "Cool from Ash @ Boston");
		jimTweetOnBoston.create("zombie_id:", bob.getInt("id"), "content:", "Cool from Bob @ Boston");
		jimTweetOnNewYork.create("zombie_id:", ash.getInt("id"), "content:", "Cool from Ash @ NewYork");
		jimTweetOnNewYork.create("zombie_id:", bob.getInt("id"), "content:", "Cool from Bob @ NewYork");

		Relation.create("following:", ash.getInt("id"), "follower:", bob.getInt("id"));
		Relation.create("following:", ash.getInt("id"), "follower:", jim.getInt("id"));
		Relation.create("following:", bob.getInt("id"), "follower:", ash.getInt("id"));
		Relation.create("following:", bob.getInt("id"), "follower:", bob.getInt("id"));
		Relation.create("following:", jim.getInt("id"), "follower:", ash.getInt("id"));
		Relation.create("following:", jim.getInt("id"), "follower:", jim.getInt("id"));
	}

	public void setUp() {
		setUpTables();
		setUpAssociations();
		setUpMetaData();
	}

	public void validateCreate() {
		Assert.assertEquals(5, dbo.getTableNames().size());

		String[] cityNames = new String[] { "Boston", "NewYork" };
		List<Record> cities = City.all();
		Assert.assertEquals(cityNames.length, cities.size());
		for (int i = 0; i < cityNames.length; i++)
			Assert.assertEquals(cityNames[i], cities.get(i).getStr("name"));

		String[] zombieNames = new String[] { "Ash", "Bob", "Jim" };
		List<Record> zombies = Zombie.all();
		Assert.assertEquals(zombieNames.length, zombies.size());
		for (int i = 0; i < zombieNames.length; i++)
			Assert.assertEquals(zombieNames[i], zombies.get(i).getStr("name"));

		List<Record> tweets = Tweet.all();
		Assert.assertEquals(cityNames.length * zombieNames.length, tweets.size());
		Iterator<Record> tweet = tweets.iterator();
		for (String zombie : zombieNames) {
			for (String city : cityNames) {
				Assert.assertEquals(String.format("Hello %s from %s!", city, zombie), tweet.next().getStr("content"));
			}
		}

		List<Record> comments = Comment.all();
		Assert.assertEquals((zombieNames.length - 1) * cityNames.length * zombieNames.length, comments.size());
		Iterator<Record> comment = comments.iterator();
		for (String zombie : zombieNames) {
			for (String city : cityNames) {
				for (String friend : zombieNames) {
					if (!zombie.equals(friend)) {
						Assert.assertEquals(String.format("Cool from %s @ %s", friend, city),
								comment.next().getStr("content"));
					}
				}
			}
		}

		List<Record> relations = Relation.all();
		Iterator<Record> relation = relations.iterator();
		for (int zombie = 0; zombie < zombieNames.length; zombie++) {
			for (int friend = 0; friend < zombieNames.length; friend++) {
				if (zombie != friend) {
					Record e = relation.next();
					Assert.assertEquals(zombie, e.getInt("following"));
					Assert.assertEquals(friend, e.getInt("follower"));
				}
			}
		}
	}

	public void validateRelation() {
		for (Record zombie : Zombie.all()) {
			Assert.assertEquals(2, zombie.get("tweets", Table.class).all().size());
			Assert.assertEquals(2, zombie.get("travelled_cities", Table.class).all().size());
			Assert.assertEquals(4, zombie.get("received_comments", Table.class).all().size());
			Assert.assertEquals(4, zombie.get("send_comments", Table.class).all().size());
			Assert.assertEquals(2, zombie.get("follower_relations", Table.class).all().size());
			Assert.assertEquals(2, zombie.get("followers", Table.class).all().size());
			Assert.assertEquals(2, zombie.get("following_relations", Table.class).all().size());
			Assert.assertEquals(2, zombie.get("followings", Table.class).all().size());
		}

		for (Record city : City.all()) {
			Assert.assertEquals(3, city.get("tweets", Table.class).all().size());
			Assert.assertEquals(3, city.get("zombies", Table.class).all().size());
		}

		for (Record tweet : Tweet.all()) {
			try {
				Assert.assertNotNull(tweet.get("zombie"));
				Assert.assertNotNull(tweet.get("city"));
			} catch (Throwable e) {
				Assert.fail(e.getMessage());
			}
			Assert.assertEquals(2, tweet.get("comments", Table.class).all().size());
		}

		for (Record comment : Comment.all()) {
			try {
				Assert.assertNotNull(comment.get("zombie"));
				Assert.assertNotNull(comment.get("tweet"));
			} catch (Throwable e) {
				Assert.fail(e.getMessage());
			}
		}
	}

	public void valiateQuery() {
		Assert.assertEquals(1, Tweet.first().getInt("id"));
		Assert.assertEquals(12, Tweet.last().getInt("id"));
		List<Record> list = Tweet.paging(2, 4);
		Assert.assertEquals(4, list.size());
		Assert.assertEquals(9, list.get(0).getInt("id"));
		Assert.assertEquals(10, list.get(1).getInt("id"));
		Assert.assertEquals(11, list.get(2).getInt("id"));
		Assert.assertEquals(12, list.get(3).getInt("id"));
	}

	public void validate() {
		validateCreate();
		validateRelation();
		valiateQuery();
	}

	public void test(DB dbo) {
		this.dbo = dbo;

		try {
			setUp();
		} catch (Throwable e) {
			Assert.fail(e.getMessage());
		} finally {
			tearDown();
		}
	}

	@Test
	public void sqlite3() {
		test(DB.open("jdbc:sqlite::memory:"));
	}

	@Test
	public void mysql() {
		// test(DB.open("jdbc:mysql://localhost/weibo", "redraiment", ""));
	}

	@Test
	public void postgresql() {
		// test(DB.open("jdbc:postgresql://localhost/weibo", "postgres",
		// "123456"));
	}

	@Test
	public void hypersql() {
		test(DB.open("jdbc:hsqldb:mem:weibo", "sa", ""));
	}

	public void tearDown() {
		if (dbo != null) {
			dbo = null;
		}
	}
}
