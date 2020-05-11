package net.htlgrieskirchen.aud2.map;

import java.util.*;

public class MyMap<K extends Comparable<K>, V> implements Map<K, V> {
	private final KeySet keySet = new KeySet();
	private final ValuesCollection values = new ValuesCollection();
	private final EntrySet entrySet = new EntrySet();

	private MyEntry<K, V> root = null;

	@Override
	public int size() {
		if(root == null) return 0;
		return root.size();
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public boolean containsKey(Object key) {
		return root != null && root.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return root != null && root.containsValue(value);
	}

	private boolean containsEntry(Entry<?, ?> entry) {
		if(entry == null) return false;
		return Objects.equals(get(entry.getKey()), entry.getValue());
	}

	@Override
	public V get(Object key) {
		if(root == null) return null;
		return root.get(key);
	}

	@Override
	public V put(K key, V value) {
		if(root == null) {
			root = new MyEntry<>(key, value);
			return null;
		}
		return root.put(key, value);
	}

	@Override
	public V remove(Object key) {
		if(root == null)
			return null;
		return root.removeByKey(key);
	}

	/**
	 * Removes all entries that have the given value.
	 *
	 * @param value The value to test for
	 * @return true if the Map has been changed as a result of this method
	 */
	private boolean removeByValue(Object value) {
		if(root == null)
			return false;
		return root.removeByValue(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> e : m.entrySet()) {
			this.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public void clear() {
		root = null;
	}

	@Override
	public Set<K> keySet() {
		return keySet;
	}

	@Override
	public Collection<V> values() {
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return entrySet;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Map)) return false;
		Map<?, ?> map = (Map<?, ?>) o;
		return Objects.equals(entrySet(), map.entrySet());
	}

	@Override
	public int hashCode() {
		return entrySet().parallelStream().mapToInt(Entry::hashCode).sum();
	}

	private class MyEntry<K extends Comparable<K>, V> implements Comparable<MyEntry<K, V>>, Entry<K, V> {
		private final K key;
		private V value;

		private MyEntry<K, V> parent;
		private MyEntry<K, V> left;
		private MyEntry<K, V> right;

		public MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int compareTo(MyEntry<K, V> entry) {
			return key.compareTo(entry.key);
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
			//Copied from TreeMap$Entry#hashCode so that equals works
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
					this.left.parent = this;
					return null;
				}
				return this.left.put(key, value);
			} else {
				if(this.right == null) {
					this.right = entry;
					this.right.parent = this;
					return null;
				}
				return this.right.put(key, value);
			}
		}

		public int size() {
			return 1 + (left == null ? 0 : left.size()) + (right == null ? 0 : right.size());
		}

		public Set<K> keySet() {
			Set<K> keySet = new HashSet<>();

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

		public V get(Object key) {
			if(this.key.equals(key)) return value;
			MyEntry<K, V> sideToSearch = this.key.compareTo((K) key) < 0 ? left : right;
			if(sideToSearch != null) {
				return sideToSearch.get(key);
			}
			return null;
		}

		public boolean containsKey(Object key) {
			if(Objects.equals(this.key, key)) return true;
			MyEntry<K, V> sideToSearch = this.key.compareTo((K) key) < 0 ? left : right;
			if(sideToSearch != null) {
				return sideToSearch.containsKey(key);
			}
			return false;
		}

		public boolean containsValue(Object value) {
			if(Objects.equals(this.value, value)) return true;
			return (left != null && left.containsValue(value)) || (right != null && right.containsValue(value));
		}

		private int height() {
			return 1 + Math.max(
					this.left == null ? 0 : this.left.height(),
					this.right == null ? 0 : this.right.height()
			);
		}

		private V removeByKey(Object key) {
			if(this.key.equals(key)) {
				return this.remove();
			}
			V returnValue = null;
			if(left != null)
				returnValue = this.left.removeByKey(key);
			if(returnValue != null)
				return returnValue;
			if(right != null) {
				return this.right.removeByKey(key);
			}
			return null;
		}

		private boolean removeByValue(Object value) {
			boolean changed = false;
			if(this.value.equals(value)) {
				//TODO: this only works if value != null
				changed = this.remove() != null;
			}
			if(left != null)
				changed |= left.removeByValue(value);
			if(right != null)
				changed |= right.removeByValue(value);
			return changed;
		}

		private V remove() {
			//No children
			if(this != root) {
				if (left == null && right == null) {
					if (this.parent.left == this) {
						this.parent.left = null;
						return this.value;
					} else if (this.parent.right == this) {
						this.parent.right = null;
						return this.value;
					}
				}
			}
			else {
				if (left == null && right == null) {
						root = null;
						return this.value;
					}
				}
			//One child
			if(this != root) {
				if (left != null && right == null) {
					if (this.parent.left == this) {
						this.parent.left = this.left;
						return this.value;
					} else if (this.parent.right == this) {
						this.parent.left = this.left;
						return this.value;
					}
				} else if (right != null && left == null) {
					if (this.parent.left == this) {
						this.parent.right = this.right;
						return this.value;
					} else if (this.parent.right == this) {
						this.parent.right = this.right;
						return this.value;
					}
				}
			}
			else {
				if (left != null && right == null) {
					root = root.left;
				} else if (right != null && left == null) {
					root = root.right;
				}
			}
			//Two children
			if(this != root) {
				if (left != null && right != null) {
					if (left.height() > right.height()) {
						this.left.droStickln(this.right);
						if (this.parent.left == this) {
							this.parent.left = this.left;
							return this.value;
						} else if (this.parent.right == this) {
							this.parent.right = this.left;
							return this.value;
						}
					} else {
						this.right.droStickln(this.left);
						if (this.parent.left == this) {
							this.parent.left = this.right;
							return this.value;
						} else if (this.parent.right == this) {
							this.parent.right = this.right;
							return this.value;
						}
					}
				}
			}
			else {
				if (left != null && right != null) {
					if (left.height() > right.height()) {
						this.left.droStickln(this.right);
						root = root.left;
						return this.value;
					} else {
						this.right.droStickln(this.left);
						root = root.right;
						return this.value;
					}
				}
			}
			assert false;
			return null;
		}

		@SuppressWarnings("SpellCheckingInspection")
		private void droStickln(MyEntry<K,V> entry) {
			if(left == null) {
				left = entry;
				return;
			}
			if(right == null) {
				right = entry;
				return;
			}
			if(left.height() > right.height())
				right.droStickln(entry);
			left.droStickln(entry);
		}
	}

	private class KeySet implements Set<K> {
		@Override
		public int size() {
			return MyMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return MyMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			if(root == null) return new Iterator<K>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public K next() {
					throw new NoSuchElementException();
				}

				@Override
				public void remove() {
					throw new IllegalStateException();
				}
			};
			return new Iterator<K>() {
				private final Iterator<K> iterator = root.keySet().iterator();
				private K lastReturn = null;

				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public K next() {
					return lastReturn = iterator.next();
				}

				@Override
				public void remove() {
					if(lastReturn == null) throw new IllegalStateException();
					KeySet.this.remove(lastReturn);
				}
			};
		}

		@Override
		public Object[] toArray() {
			if(root == null) return new Object[0];
			return root.keySet().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if(root == null) return a;
			//noinspection SuspiciousToArrayCall
			return root.keySet().toArray(a);
		}

		@Override
		public boolean add(K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			return MyMap.this.remove(o) != null;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.stream().allMatch(MyMap.this::containsKey);
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			if(root == null) return false;
			//TODO: Test if the code below can be simplified
			return root.keySet().stream().filter(k -> !c.contains(k)).map(MyMap.this::remove).map(Objects::nonNull).filter(Boolean::booleanValue).count() > 0;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			//TODO: Test if the code below can be simplified
			return c.stream().map(MyMap.this::remove).map(Objects::nonNull).filter(Boolean::booleanValue).count() > 0;
		}

		@Override
		public void clear() {
			MyMap.this.clear();
		}

		@Override
		public int hashCode() {
			return stream().mapToInt(Objects::hashCode).sum();
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == this) return true;
			if(obj == null) return false;
			if(!(obj instanceof Set<?>)) return false;
			Set<?> set = (Set<?>) obj;
			if(size() != set.size()) return false;
			return containsAll(set);
		}
	}

	private class ValuesCollection implements Collection<V> {
		@Override
		public int size() {
			return MyMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return MyMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public Iterator<V> iterator() {
			if(root == null) return new Iterator<V>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public V next() {
					throw new NoSuchElementException();
				}

				@Override
				public void remove() {
					throw new IllegalStateException();
				}
			};
			return new Iterator<V>() {
				private final Object UNINITIALIZED = new Object();
				private final Iterator<V> iterator = root.values().iterator();
				private Object lastValue = UNINITIALIZED;

				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public V next() {
					V next = iterator.next();
					lastValue = next;
					return next;
				}

				@Override
				public void remove() {
					if(lastValue == UNINITIALIZED) throw new IllegalStateException();
					ValuesCollection.this.remove(lastValue);
				}
			};
		}

		@Override
		public Object[] toArray() {
			if(root == null) return new Object[0];
			return root.values().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if(root == null) return a;
			//noinspection SuspiciousToArrayCall
			return root.values().toArray(a);
		}

		@Override
		public boolean add(V v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			return MyMap.this.removeByValue(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			if(root == null) return c.isEmpty();
			return c.stream().allMatch(MyMap.this::containsValue);
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if(root == null) return false;
			//FIXME: test if below can be simplified
			return c.stream().map(MyMap.this::removeByValue).filter(Boolean::booleanValue).count() > 0;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			if(root == null) return false;
			//FIXME: test if below can be simplified
			return root.values().stream().filter(v -> !c.contains(v)).map(MyMap.this::removeByValue).filter(Boolean::booleanValue).count() > 0;
		}

		@Override
		public void clear() {
			MyMap.this.clear();
		}

		@Override
		public int hashCode() {
			return stream().mapToInt(Objects::hashCode).sum();
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == this) return true;
			if(obj == null) return false;
			if(!(obj instanceof Collection<?>)) return false;
			Collection<?> set = (Collection<?>) obj;
			if(size() != set.size()) return false;
			return containsAll(set);
		}
	}

	private class EntrySet implements Set<Entry<K, V>> {
		@Override
		public int size() {
			return MyMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return MyMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			if(root == null) return false;
			return root.entrySet().contains(o);
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			if(root == null) return new Iterator<Entry<K, V>>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Entry<K, V> next() {
					throw new NoSuchElementException();
				}

				@Override
				public void remove() {
					throw new IllegalStateException();
				}
			};
			return new Iterator<Entry<K, V>>() {
				private final Iterator<Entry<K, V>> iterator = root.entrySet().iterator();
				private Entry<K, V> lastEntry = null;

				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public Entry<K, V> next() {
					return lastEntry = iterator.next();
				}

				@Override
				public void remove() {
					if(lastEntry == null) throw new IllegalStateException();
					EntrySet.this.remove(lastEntry);
				}
			};
		}

		@Override
		public Object[] toArray() {
			if(root == null) return new Object[0];
			return root.entrySet().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if(root == null) return a;
			//noinspection SuspiciousToArrayCall
			return root.entrySet().toArray(a);
		}

		@Override
		public boolean add(Entry<K, V> kvEntry) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			if(!(o instanceof Entry))
				return false;
			Entry<?, ?> entry = (Entry<?, ?>) o;
			return MyMap.this.remove(entry.getKey(), entry.getValue());
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.stream().map(o -> (Entry<?, ?>) o).allMatch(MyMap.this::containsEntry);
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			if(root == null) return false;
			//FIXME: find out if the values have to match for removal
			return root.entrySet().stream().filter(entry -> !c.contains(entry)).map(Entry::getKey).map(MyMap.this::remove).map(Objects::nonNull).count() > 0;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			//FIXME: find out if the values have to match for removal
			return c.stream().map(o -> (Entry<?, ?>) o).map(Entry::getKey).map(MyMap.this::remove).map(Objects::nonNull).count() > 0;
		}

		@Override
		public void clear() {
			MyMap.this.clear();
		}

		@Override
		public int hashCode() {
			return stream().mapToInt(Objects::hashCode).sum();
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == this) return true;
			if(obj == null) return false;
			if(!(obj instanceof Set<?>)) return false;
			Set<?> set = (Set<?>) obj;
			if(size() != set.size()) return false;
			return containsAll(set);
		}
	}
}
