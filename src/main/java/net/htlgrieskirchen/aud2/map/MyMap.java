package net.htlgrieskirchen.aud2.map;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MyMap<K extends Comparable<K>, V> implements Map<K, V> {
	private MyEntry<K, V> root = null;

	@Override
	public int size() {
		return root.size();
	}

	@Override
	public boolean isEmpty() {
		//FIXME: Implementation only works if put is not yet implemented
		return true;
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
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public Set<K> keySet() {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public Collection<V> values() {
		throw new IllegalStateException("Not yet implemented!");
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new IllegalStateException("Not yet implemented!");
	}

	private static class MyEntry<K extends Comparable<K>, V> implements Comparable<MyEntry<K, V>> {
		private K key;
		private V value;

		private MyEntry<K, V> parent;
		private MyEntry<K, V> left;
		private MyEntry<K, V> right;

		@Override
		public int compareTo(MyEntry<K, V> entry) {
			return key.compareTo(entry.key);
		}

		public MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			MyEntry<?, ?> myEntry = (MyEntry<?, ?>) o;
			return Objects.equals(key, myEntry.key);
		}

		@Override
		public int hashCode() {
			return Objects.hash(key);
		}

		public V put(K key, V value) {
			MyEntry<K, V> entry = new MyEntry<>(key, value);
			if(this.equals(entry)) {
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

	}
}
