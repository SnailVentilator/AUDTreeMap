package net.htlgrieskirchen.aud2.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyMap<K,V> implements Map<K,V> {
	@Override
	public int size() {
		throw new IllegalStateException("Not yet implemented!");
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
		throw new IllegalStateException("Not yet implemented!");
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
}
