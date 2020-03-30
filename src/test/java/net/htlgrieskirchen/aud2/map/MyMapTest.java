package net.htlgrieskirchen.aud2.map;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class MyMapTest {
	private Map<String, String> myMap;
	private Map<String, String> treeMap;

	@Before
	public void prepareMaps() {
		myMap = new MyMap<>();
		treeMap = new TreeMap<>();
	}

	@Test
	public void basicPutTest() {
		executeAndCompare(map -> {
			map.put(someString[0], someString[1]);
		});
	}

	public static String[] someString = {"404", "Internet", "Not", "Found"};

	public void executeAndCompare(Consumer<Map<String, String>> runnable) {
		runnable.accept(myMap);
		runnable.accept(treeMap);
		assertEquals(myMap, treeMap);
	}
}
