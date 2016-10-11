package com.baomidou.mybatisplus.activerecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 数组和List相关的工具方法。
 * 
 * @since 1.0
 * @author redraiment
 */
public final class Seq {
	/**
	 * 将一系列对象组装成数组。
	 * 
	 * @param <E>
	 *            元素类型。
	 * @param args
	 *            多个同类型的元素。
	 * @return 返回由这些元素组成的数组。
	 */
	public static <E> E[] array(E... args) {
		return args;
	}

	/**
	 * 将一系列对象组装成List。
	 * 
	 * @param <E>
	 *            元素类型。
	 * @param args
	 *            多个同类型的元素。
	 * @return 返回由这些元素组成的List。
	 */
	public static <E> List<E> list(E... args) {
		List<E> list = new ArrayList<E>();
		list.addAll(Arrays.asList(args));
		return list;
	}

	/**
	 * 追加任意多个新int到int数组末尾。
	 * 
	 * @param a
	 *            现有int数组。
	 * @param b
	 *            任意多个新int。
	 * @return 返回组装好的新int数组。
	 */
	public static int[] concat(int[] a, int... b) {
		int[] c = new int[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	/**
	 * 追加任意多个新元素到数组末尾。
	 * 
	 * @param <E>
	 *            元素的类型。
	 * @param a
	 *            现有数组。
	 * @param b
	 *            任意多个新元素。
	 * @return 返回组装好的新数组。
	 */
	public static <E> E[] concat(E[] a, E... b) {
		return merge(a, b);
	}

	/**
	 * 将数组连接成字符串。
	 * 
	 * @param delimiter
	 *            分隔符。
	 * @param args
	 *            任意多个元素。
	 * @return 连接后的字符串。
	 */
	public static String join(String delimiter, Object... args) {
		return join(Arrays.asList(args), delimiter);
	}

	/**
	 * 将容器里的元素连接成字符串。
	 * 
	 * @param list
	 *            包含任意多个元素的容器。
	 * @param delimiter
	 *            分隔符。
	 * @return 连接后的字符串。
	 */
	public static String join(Collection<?> list, String delimiter) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		if (delimiter == null) {
			delimiter = "";
		}

		StringBuilder s = new StringBuilder();
		boolean first = true;
		for (Object e : list) {
			if (first) {
				first = false;
			} else {
				s.append(delimiter);
			}
			s.append(e);
		}
		return s.toString();
	}

	/**
	 * 合并两个数组。
	 * 
	 * @param <E>
	 *            数组元素的类型。
	 * @param a
	 *            前半部分数组。
	 * @param b
	 *            后半部分数组。
	 * @return 合并后的数组。
	 */
	public static <E> E[] merge(E[] a, E[] b) {
		List<E> list = merge(Arrays.asList(a), Arrays.asList(b));
		return list.toArray(a);
	}

	/**
	 * 合并两个List。
	 * 
	 * @param <E>
	 *            List元素的类型。
	 * @param a
	 *            前半部分List。
	 * @param b
	 *            后半部分List。
	 * @return 合并后的List。
	 */
	public static <E> List<E> merge(List<E> a, List<E> b) {
		List<E> list = new ArrayList<E>();
		list.addAll(a);
		list.addAll(b);
		return list;
	}

	/**
	 * 从数组中删除所有与给定对象equals的元素。
	 * 
	 * @param <E>
	 *            元素类型。
	 * @param a
	 *            给定数组。
	 * @param e
	 *            给定对象
	 * @return 去除e之后的数组。
	 */
	public static <E> E[] remove(E[] a, E e) {
		List<E> list = remove(Arrays.asList(a), e);
		return (E[]) list.toArray();
	}

	/**
	 * 从List中删除所有与给定对象equals的元素。
	 * 
	 * @param <E>
	 *            元素类型。
	 * @param a
	 *            给定List。
	 * @param e
	 *            给定对象
	 * @return 去除e之后的List。
	 */
	public static <E> List<E> remove(List<E> a, E e) {
		List<E> list = new ArrayList<E>();
		for (E o : a) {
			if (!o.equals(e)) {
				list.add(o);
			}
		}
		return list;
	}

	/**
	 * 根据给定的下标，选出多个元素，并组成新的数组。
	 * 
	 * @param <E>
	 *            元素类型。
	 * @param a
	 *            给定数组。
	 * @param indexes
	 *            任意多个要获取的元素下标。
	 * @return 根据下标获得的元素组成的新数组。
	 */
	public static <E> E[] valuesAt(E[] a, int... indexes) {
		List<E> list = valuesAt(Arrays.asList(a), indexes);
		return (E[]) list.toArray();
	}

	/**
	 * 根据给定的下标，选出多个元素，并组成新的List。
	 * 
	 * @param <E>
	 *            元素类型。
	 * @param from
	 *            给定List。
	 * @param indexes
	 *            任意多个要获取的元素下标。
	 * @return 根据下标获得的元素组成的新List。
	 */
	public static <E> List<E> valuesAt(List<E> from, int... indexes) {
		List<E> list = new ArrayList<E>();
		for (int i : indexes) {
			if (0 <= i && i < from.size()) {
				list.add(from.get(i));
			} else if (-from.size() <= i && i < 0) {
				list.add(from.get(from.size() + i));
			} else {
				list.add(null);
			}
		}
		return list;
	}

	/**
	 * 同时给int数组中多个位置同时赋值。
	 * 
	 * @param a
	 *            给定int数组。
	 * @param indexes
	 *            待赋值的位置下标。
	 * @param values
	 *            与下标一一对应的int值。
	 * @return 返回赋值后的新int数组。
	 */
	public static int[] assignAt(int[] a, Integer[] indexes, int... values) {
		if (indexes.length != values.length) {
			throw new IllegalArgumentException(
					String.format("index.length(%d) != values.length(%d)", indexes.length, values.length));
		}
		for (int i = 0; i < indexes.length; i++) {
			int index = indexes[i];
			if (0 <= index && index < a.length) {
				a[index] = values[i];
			} else if (-a.length <= index && index < 0) {
				a[a.length + index] = values[i];
			} else {
				throw new ArrayIndexOutOfBoundsException(index);
			}
		}
		return a;
	}

	/**
	 * 同时给数组中多个位置同时赋值。
	 * 
	 * @param <E>
	 *            数组元素类型。
	 * @param a
	 *            给定数组。
	 * @param indexes
	 *            待赋值的位置下标。
	 * @param values
	 *            与下标一一对应的值。
	 * @return 返回赋值后的新数组。
	 */
	public static <E> E[] assignAt(E[] a, Integer[] indexes, E... values) {
		if (indexes.length != values.length) {
			throw new IllegalArgumentException(
					String.format("index.length(%d) != values.length(%d)", indexes.length, values.length));
		}
		for (int i = 0; i < indexes.length; i++) {
			int index = indexes[i];
			if (0 <= index && index < a.length) {
				a[index] = values[i];
			} else if (-a.length <= index && index < 0) {
				a[a.length + index] = values[i];
			} else {
				throw new ArrayIndexOutOfBoundsException(index);
			}
		}
		return a;
	}

	/**
	 * 根据规定格式，对容器中的每个元素进行格式化，并返回格式化后的结果。
	 * 
	 * @param from
	 *            包含任意多个元素的容器。
	 * @param format
	 *            格式化模板，与printf兼容。
	 * @return 格式化后的新列表。
	 */
	public static List<String> map(Collection<?> from, String format) {
		List<String> to = new ArrayList<String>(from.size());
		for (Object e : from) {
			to.add(String.format(format, e));
		}
		return to;
	}

	/**
	 * 拆分容器，每份至多包含n个元素，将每堆元素连接成一个独立字符串。
	 * 
	 * @param from
	 *            包含任意多个元素的容器。
	 * @param n
	 *            子元素个数。
	 * @param delimiter
	 *            分隔符。
	 * @return 拆分后的字符串List。
	 */
	public static List<String> partition(Collection<String> from, int n, String delimiter) {
		List<String> to = new ArrayList<String>();
		List<String> buffer = new ArrayList<String>(n);
		for (String e : from) {
			buffer.add(e);
			if (buffer.size() >= n) {
				to.add(join(buffer, delimiter));
				buffer.clear();
			}
		}
		return to;
	}
}
