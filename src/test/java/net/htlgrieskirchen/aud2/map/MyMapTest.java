package net.htlgrieskirchen.aud2.map;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class MyMapTest {
	public static String[] someString = {"404", "Internet", "Not", "Found", "Hallo", "", "Holland", "null", "Sweden", "Kalk", "perfect"};
	private Map<String, String> myMap;
	private Map<String, String> treeMap;

	@Before
	public void prepareMaps() {
		myMap = new MyMap<>();
		treeMap = new TreeMap<>();
	}

	/**
	 * This helper function executes a given function for each of the maps
	 * being tested and then asserts that they are equal.
	 * This is useful for the majority of tests that modify the state of
	 * the map.
	 *
	 * @param consumer A consumer that is executed for each map before comparing
	 */
	public void executeAndCompare(Consumer<Map<String, String>> consumer) {
		execute(consumer);
		assertEquals(myMap, treeMap);
		assertEquals(treeMap, myMap);
	}

	/**
	 * This helper function executes a given function for each of the maps
	 * being tested.
	 *
	 * @param consumer A consumer that is executed for each map
	 */
	public void execute(Consumer<Map<String, String>> consumer) {
		consumer.accept(myMap);
		consumer.accept(treeMap);
	}

	/**
	 * This helper function asserts that a given method reference results in the
	 * same result for both maps.
	 * <p>
	 * Example:
	 * <pre>assertEqualResult(Map::size);</pre>
	 *
	 * @param function A method reference or other function that returns something comparable
	 */
	public void assertEqualResult(Function<Map<String, String>, ? extends Comparable<?>> function) {
		Comparable<?> myMapResult = function.apply(myMap);
		Comparable<?> treeMapResult = function.apply(treeMap);
		assertEquals(treeMapResult, myMapResult);
		assertEquals(myMapResult, treeMapResult);
	}

	@Test
	public void empty() {
		assertEquals(treeMap, myMap);
	}

	@Test
	public void putSimple() {
		executeAndCompare(map -> map.put(someString[0], someString[1]));
	}

	@Test
	public void putReplace() {
		execute(map -> map.put(someString[0], someString[1]));
		executeAndCompare(map -> map.put(someString[0], someString[2]));
	}

	@Test
	public void putNull() {
		executeAndCompare(map -> map.put(someString[0], null));
	}

	@Test
	public void isEmptySimple() {
		execute(map -> map.put(someString[0], someString[1]));
		assertEqualResult(Map::isEmpty);
	}

	@Test
	public void isEmptyNull() {
		execute(map -> map.put(someString[0], null));
		assertEqualResult(Map::isEmpty);
	}

	@Test
	public void sizeEmpty() {
		assertEqualResult(Map::size);
	}

	@Test
	public void sizeSimple() {
		execute(map -> map.put(someString[0], someString[1]));
		assertEqualResult(Map::size);
	}

	@Test
	public void sizeNull() {
		execute(map -> map.put(someString[0], null));
		assertEqualResult(Map::size);
	}

	@Test
	public void getEmpty() {
		assertEqualResult(map -> map.get(someString[0]));
	}

	@Test
	public void getSimple() {
		execute(map -> map.put(someString[0], someString[1]));
		assertEqualResult(map -> map.get(someString[0]));
	}

	@Test
	public void getNull() {
		execute(map -> map.put(someString[0], null));
		assertEqualResult(map -> map.get(someString[0]));
	}

	@Test
	public void getDeep() {
		for(final AtomicInteger i = new AtomicInteger(0); i.get() < 10; i.incrementAndGet())
			assertEqualResult(map -> map.put(someString[i.get()], someString[i.get()+1]));
		for(final AtomicInteger i = new AtomicInteger(0); i.get() < 10; i.incrementAndGet())
			assertEqualResult(map -> map.get(someString[i.get()]));
	}

	@Test
	public void equalsEmpty() {
		assertEquals(treeMap, myMap);
		assertEquals(myMap, treeMap);
	}

	@Test
	public void equalsSimple() {
		execute(map -> map.put(someString[0], someString[1]));
		assertEquals(treeMap, myMap);
		assertEquals(myMap, treeMap);
	}

	@Test
	public void equalsNull() {
		execute(map -> map.put(someString[0], null));
		assertEquals(treeMap, myMap);
		assertEquals(myMap, treeMap);
	}

	@Test
	public void containsKeySimple() {
		execute(map -> map.put(someString[0], someString[1]));
		execute(map -> map.put(someString[5], someString[6]));
		assertEqualResult(map -> map.containsKey(someString[5]));
	}

	@Test
	public void containsKeyEmpty() {
		assertEqualResult(map -> map.containsKey(someString[0]));
	}

	@Test
	public void putAllSingle() {
		executeAndCompare(map -> map.putAll(Collections.singletonMap(someString[0], someString[1])));
	}

	@Test
	public void putAllMultiple() {
		Map<String, String> myMap2 = new MyMap<>();
		Map<String, String> treeMap2 = new TreeMap<>();

		myMap2.put(someString[0], someString[1]);
		myMap2.put(someString[3], someString[4]);
		myMap2.put(someString[6], someString[7]);
		treeMap2.put(someString[0], someString[1]);
		treeMap2.put(someString[3], someString[4]);
		treeMap2.put(someString[6], someString[7]);

		executeAndCompare(map -> map.putAll(myMap2));
		executeAndCompare(map -> map.putAll(treeMap2));
	}

	@Test
	public void clearEmpty() {
		executeAndCompare(Map::clear);
	}

	@Test
	public void clearSimple() {
		execute(map -> map.put(someString[someString.length - 1], someString[someString.length - 2]));
		executeAndCompare(Map::clear);
	}
}