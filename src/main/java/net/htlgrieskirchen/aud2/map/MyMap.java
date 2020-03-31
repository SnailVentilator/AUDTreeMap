package net.htlgrieskirchen.aud2.map;

import java.util.*;

public class MyMap<K extends Comparable<K>, V> implements Map<K, V> {
	private MyEntry<K, V> root = null;

	@Override
	public int size() {
		return root.size();
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public boolean containsKey(Object key) {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public boolean containsValue(Object value) {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public V get(Object key) {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public V put(K key, V value) {
		MyEntry<K, V> entry = new MyEntry<>(key, value);
		if(root == null) {
			root = entry;
			return null;
		} else {
			return root.put(key, value);
		}
	}

	@Override
	public V remove(Object key) {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public void clear() {
		Set<Entry<K, V>> set = new HashSet<>();

	}

	@Override
	public Set<K> keySet() {
		return root.keySet();
	}

	@Override
	public Collection<V> values() {
		return root.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return root.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Map)) return false;
		Map<?, ?> map = (Map<?, ?>) o;
		return Objects.equals(entrySet(), map.entrySet());
	}

	private static class MyEntry<K extends Comparable<K>, V> implements Comparable<MyEntry<K, V>>, Entry<K, V> {
		private final K key;
		private V value;

		private MyEntry<K, V> parent;
		private MyEntry<K, V> left;
		private MyEntry<K, V> right;

		@Override
		public int compareTo(MyEntry<K, V> entry) {
			if(entry.key == null) {
				return 1;
			}
			return key.compareTo(entry.key);
		}

		public MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(!(o instanceof Entry)) return false;
			Entry<?, ?> entry = (Entry<?, ?>) o;
			return Objects.equals(key, entry.getKey()) && Objects.equals(value, entry.getValue());
		}

		@Override
		public int hashCode() {
			//Copied from TreeMap$Entry#hashCode so that equals work
			int keyHash = (key == null ? 0 : key.hashCode());
			int valueHash = (value == null ? 0 : value.hashCode());
			return keyHash ^ valueHash;
		}

		public V put(K key, V value) {
			MyEntry<K, V> entry = new MyEntry<>(key, value);
			if(this.key.equals(entry.key)) {
				V oldValue = this.value;
				this.value = value;
				return oldValue;
			}
			if(this.compareTo(entry) <= 0) {
				if(this.left == null) {
					this.left = entry;
					return null;
				}
				return this.left.put(key, value);
			} else {
				if(this.right == null) {
					this.right = entry;
					return null;
				}
				return this.right.put(key, value);
			}
		}

		public int size() {
			return 1 + (left == null ? 0 : left.size()) + (right == null ? 0 : right.size());
		}

		public Set<K> keySet() {
			Set<K> keySet = new LinkedHashSet<>(); //Order is needed for entrySet()

			keySet.add(key);
			if(left != null) keySet.addAll(left.keySet());
			if(right != null) keySet.addAll(right.keySet());

			return keySet;
		}

		public Collection<V> values() {
			Collection<V> values = new ArrayList<>();

			values.add(value);
			if(left != null) values.addAll(left.values());
			if(right != null) values.addAll(right.values());

			return values;
		}

		public Set<Entry<K, V>> entrySet() {
			Set<Entry<K, V>> entrySet = new HashSet<>();

			entrySet.add(this);
			if(left != null) entrySet.addAll(left.entrySet());
			if(right != null) entrySet.addAll(right.entrySet());

			return entrySet;
		}
	}
}
