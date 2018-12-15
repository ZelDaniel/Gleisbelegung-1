package org.gleisbelegung.collection;

class DoubleKeyEntry<K, V> {
	final K k1, k2;
	final V v;

	DoubleKeyEntry(final K k1, final K k2, final V v) {
		this.k1 = k1;
		this.k2 = k2;
		this.v = v;
	}

	@Override
	public int hashCode() {
		return (this.k1.hashCode() << 8) ^ this.k2.hashCode();
	}

	@Override
	public String toString() {
		return "[" + this.k1.toString() + "," + this.k2.toString() + "]=" + this.v.toString();
	}
}