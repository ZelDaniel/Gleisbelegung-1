package org.gleisbelegung.sts;

import java.util.*;

import org.gleisbelegung.xml.XML;

public class Plattform {

	public static final Plattform EMPTY = new Plattform();

	private static final Map<String, Plattform> generatorMap = new HashMap<>();

	private final Set<Plattform> neighbours = new HashSet<>();

	public static Plattform get(final String name) {
		final Plattform pf = Plattform.generatorMap.get(name);
		if (pf != null) {
			return pf;
		}
		return new Plattform(name);
	}

	public static Plattform parse(XML xml) {
		Plattform generated = get(xml.get("name"));
		boolean complete = true;
		for (XML neighbour : xml.getInternXML()) {
			if (neighbour.getKey().equals("n")) {
				Plattform neighbourParsed = generatorMap.get(neighbour.get("name"));
				if (neighbourParsed == null) {
					complete = false;
					break;
				}
				generated.neighbours.add(neighbourParsed);
			}
		}
		if (complete) {
			for (Plattform p : generated.neighbours) {
				p.synchronizeNeighbours(generated);
			}
		}

		return generated;
	}

	private final String name;

	private Plattform() {
		this.name = null;
	}

	private Plattform(final String name) {
		Plattform.generatorMap.put(name, this);
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public Set<Plattform> getNeighbours()
	{
		return new HashSet<>(this.neighbours);
	}

	private void synchronizeNeighbours(Plattform plattform) {
		this.neighbours.clear();
		this.neighbours.addAll(plattform.getNeighbours());
		this.neighbours.remove(this);
		this.neighbours.add(plattform);
	}
}
