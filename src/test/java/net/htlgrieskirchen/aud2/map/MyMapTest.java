package net.htlgrieskirchen.aud2.map;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class MyMapTest {
	public static String[] someString = {"404", "Internet", "Not", "Found"};
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
}