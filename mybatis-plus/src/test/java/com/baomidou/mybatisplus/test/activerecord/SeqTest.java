package com.baomidou.mybatisplus.test.activerecord;

import com.baomidou.mybatisplus.activerecord.Seq;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class SeqTest {
	@Test
	public void testConcat() {
		Assert.assertArrayEquals(Seq.array("1", "2", "3", "4"), Seq.concat(Seq.array("1", "2"), "3", "4"));
	}

	@Test
	public void testRemove() {
		Assert.assertArrayEquals(Seq.array("1", "2", "4"), Seq.remove(Seq.array("1", "3", "2", "3", "3", "4"), "3"));
	}

	@Test
	public void testJoinListNull() {
		Assert.assertEquals("", Seq.join(null, ""));
	}

	@Test
	public void testJoinListEmpty() {
		Assert.assertEquals("", Seq.join(Collections.EMPTY_LIST, ""));
	}

	@Test
	public void testJoinDelimiterNull() {
		Assert.assertEquals("123", Seq.join(Arrays.asList("1", "2", "3"), null));
	}

	@Test
	public void testJoinDelimiterEmpty() {
		Assert.assertEquals("123", Seq.join(Arrays.asList("1", "2", "3"), ""));
	}

	@Test
	public void testCommaList() {
		Assert.assertEquals("1, 2, 3", Seq.join(Arrays.asList("1", "2", "3"), ", "));
	}

	@Test
	public void testConditionList() {
		Assert.assertEquals("username = ? and rowid = ? and password = ?",
				Seq.join(Arrays.asList("username = ?", "rowid = ?", "password = ?"), " and "));
	}

	@Test
	public void testConstantMap() {
		String[] actuals = Seq.map(Arrays.asList("1", "2", "3"), "?").toArray(new String[0]);
		Assert.assertArrayEquals(new String[] { "?", "?", "?" }, actuals);
	}

	@Test
	public void testFormatMap() {
		String[] actuals = Seq.map(Arrays.asList("1", "2", "3"), "id = %s").toArray(new String[0]);
		Assert.assertArrayEquals(new String[] { "id = 1", "id = 2", "id = 3" }, actuals);
	}

	@Test
	public void testPartition() {
		String[] actuals = null;

		actuals = Seq.partition(Arrays.asList("1", "2", "3", "4"), 2, " ").toArray(new String[0]);
		Assert.assertArrayEquals(new String[] { "1 2", "3 4" }, actuals);

		actuals = Seq.partition(Arrays.asList("1", "2", "3", "4", "5"), 2, " ").toArray(new String[0]);
		Assert.assertArrayEquals(new String[] { "1 2", "3 4" }, actuals);

		actuals = Seq.partition(Arrays.asList("1", "2", "3", "4", "5", "6"), 3, " ").toArray(new String[0]);
		Assert.assertArrayEquals(new String[] { "1 2 3", "4 5 6" }, actuals);
	}

	@Test
	public void testAssignAt() {
		int[] a = new int[5];
		Seq.assignAt(a, Seq.array(0, 2, 3), 1, 3, 4);
		Seq.assignAt(a, Seq.array(-1, -4), 5, 2);
		Assert.assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, a);

		String[] s = new String[3];
		Seq.assignAt(s, Seq.array(0, 2), "a", "dialect");
		Seq.assignAt(s, Seq.array(-2, -1), "b", "c");
		Assert.assertArrayEquals(Seq.array("a", "b", "c"), s);
	}
}