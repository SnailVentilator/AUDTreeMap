package net.htlgrieskirchen.aud2.map;

public class Main {
	public static void main(String[] args) {
		MyMap<Integer, String> map = new MyMap<>();

		int[] array = {42,20,74,7,23,53,76,4,17,21,62};

		for(int i = 0; i < array.length; i++) {
			map.put(array[i], null);
		}

		map.createViewer();
	}
}
