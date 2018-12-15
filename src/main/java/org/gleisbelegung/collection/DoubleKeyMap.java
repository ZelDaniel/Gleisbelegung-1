package org.gleisbelegung.collection;

import org.gleisbelegung.utils.MathX;

import java.lang.reflect.Array;

/**
 * A Map where two values form the key.
 *
 * @param <K>
 *            Class for keys
 * @param <V>
 *            Class for values
 */
public class DoubleKeyMap<K, V> {

	/**
	 * If at last threshold * array.length entries are present, the internal array will be enlarged.
	 */
	private final double threshold;
	private DoubleKeyEntry<K, V>[] array;
	private int size = 0;

	/**
	 * Creates a new instance of {@link DoubleKeyMap}
	 */
	public DoubleKeyMap() {
		this(23);
	}

	/**
	 * Creates a new instance of {@link DoubleKeyMap} with certain initial
	 * capacity.
	 *
	 * @param initSize
	 *            initial capacity
	 */
	private DoubleKeyMap(final int initSize) {
		this(initSize, 0.80);
	}

	/**
	 * Creates a new instance of {@link DoubleKeyMap} with certain initial
	 * capacity.
	 *
	 * @param initSize
	 *            initial capacity
	 * @param threshold
	 *            threshold for capacity until a capacity change will take place
	 */
	private DoubleKeyMap(final int initSize, final double threshold) {
		if (initSize < 0)
			throw new IllegalArgumentException();
		if (threshold < 0)
			throw new IllegalArgumentException();
		this.threshold = threshold;
		this.array = (DoubleKeyEntry<K, V>[]) Array.newInstance(DoubleKeyEntry.class, initSize);
	}

	private boolean put(final DoubleKeyEntry<K, V> entry, final DoubleKeyEntry<K, V>[] array) {
		final int hash = entry.hashCode();
		final int index = (hash < 0 ? ~hash : hash) % array.length;
		if (array[index] == null) {
			array[index] = entry;
			return true;
		}
		int offset = 1;
		while (true) {
			int collIndexLeft = index - offset;
			while (collIndexLeft < 0) {
				collIndexLeft += array.length;
			}
			if (array[collIndexLeft] == null) {
				array[collIndexLeft] = entry;
				return true;
			}
			int collIndexRight = index + offset;
			while (collIndexRight >= array.length) {
				collIndexRight -= array.length;
			}
			if (array[collIndexRight] == null) {
				array[collIndexRight] = entry;
				return true;
			}
			offset *= 2;
		}
	}

	private int getNewSize()
	{
		int newSize = (this.array.length * 5) / 2;
		while (!MathX.isPrime(newSize)) {
			newSize++;
		}

		return newSize;
	}

	private void enlargeArray() {
		final int newSize = this.getNewSize();
		final DoubleKeyEntry<K, V>[] array = (DoubleKeyEntry<K, V>[]) Array.newInstance(DoubleKeyMap.class, newSize);
		for (int i = 0; i < this.array.length; ++i) {
			if (this.array[i] == null) {
				continue;
			}
			put(this.array[i], array);
		}
		this.array = array;
	}

	/**
	 * @see java.util.Map#get(Object)
	 *
	 * @param key1
	 *            value for first key
	 * @param key2
	 *            value for second key
	 * @return value
	 *
	 */
	@SuppressWarnings("unchecked")
	public V get(final K key1, final K key2) {
		final int hash = new DoubleKeyEntry<K, V>(key1, key2, null).hashCode();
		final int index = (hash < 0 ? ~hash : hash) % this.array.length;
		if (this.array[index] == null) {
			return null;
		}
		DoubleKeyEntry<K, V> entry = this.array[index];
		if (entry.k1.equals(key1) && entry.k2.equals(key2)) {
			return entry.v;
		}
		int offset = 1;
		while (true) {
			int indexL = (index - offset) % this.array.length;
			if (indexL < 0) {
				indexL += this.array.length;
			}
			entry = this.array[indexL];
			if (entry == null) {
				return null;
			}
			if (entry.k1.equals(key1) && entry.k2.equals(key2)) {
				return entry.v;
			}
			int indexR = (index + offset) % this.array.length;
			entry = this.array[indexR];
			if (entry == null) {
				return null;
			}
			if (entry.k1.equals(key1) && entry.k2.equals(key2)) {
				return entry.v;
			}
			offset = (offset * 2) % (Integer.MAX_VALUE / 2);
		}
	}

	/**
	 * @see java.util.Map#put(Object, Object)
	 *
	 * @param key1
	 *            value for first key
	 * @param key2
	 *            value for second key
	 * @param value
	 *            value for actual value to be stored
	 * @return true if storing value was successful
	 */
	public boolean put(final K key1, final K key2, final V value) {
		if ((key1 == null) || (key2 == null)) {
			throw new NullPointerException();
		}

		final boolean success = put(new DoubleKeyEntry<>(key1, key2, value), this.array);
		if (success && (++this.size >= (int) (this.array.length * this.threshold))) {
			enlargeArray();
		}
		return success;
	}

	/**
	 * @return number of key-key-value pairs stored
	 */
	public int size() {
		return this.size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("javadoc")
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < this.array.length; ++i) {
			if (this.array[i] == null) {
				continue;
			}
			if (sb.length() != 1) {
				sb.append(", ");
			}
			sb.append(this.array[i].toString());
		}
		return sb.append(")").toString();
	}
}
