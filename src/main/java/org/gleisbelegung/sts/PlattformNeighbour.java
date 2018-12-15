package org.gleisbelegung.sts;

import java.util.Iterator;
import java.util.Set;

public class PlattformNeighbour {

	private final Plattform plattform;
	private final Set<Plattform> neighbours;

	public PlattformNeighbour(final Plattform plattform,
			final Set<Plattform> neighbours) {
		this.plattform = plattform;
		this.neighbours = neighbours;
	}

	public Plattform get() {
		return this.plattform;
	}

	public Iterator<Plattform> iterator() {
		return neighbours.iterator();
	}

}
