package org.gleisbelegung.sts;

import java.util.HashSet;
import java.util.Set;

import org.gleisbelegung.xml.XML;
import org.gleisbelegung.collection.DoubleKeyMap;

public class Plattform {

	public static final Plattform EMPTY = new Plattform();

	private static final DoubleKeyMap<String, Plattform> generatorMap = new DoubleKeyMap<>();

	private static final String NULL = "N/A";

	public static Plattform get(final String planned, final String plattform) {
		if ((planned != null) && planned.isEmpty()) {
			return Plattform.get(null, plattform);
		}
		if (plattform == null) {
		}
		if ((plattform == null) || plattform.isEmpty()) {
			if (planned != null) {
				return Plattform.get(planned, Plattform.NULL);
			}
			return Plattform.EMPTY;
		}
		final Plattform pf = Plattform.generatorMap.get(planned, plattform);
		if (pf != null) {
			return pf;
		}
		return new Plattform(planned, plattform);
	}

	private final String plan;

	private Plattform() {
		this.plan = null;
	}

	public Plattform(final String planned, final String plattform) {
		Plattform.generatorMap.put(planned, plattform, this);
		this.plan = planned;
	}

	public boolean equals(final Plattform o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		return this.plan.equals(o.plan);
	}
	
	@Override
	public int hashCode() {
		return this.plan.hashCode();
		
	}

	public String getPlan() {
		if (this == EMPTY) {
			return "";
		}
		return this.plan;
	}

	@Override
	public String toString() {
		return this.plan;
	}

	public static PlattformNeighbour getAllNeighbours(final XML xml) {
		if (!xml.getKey().equals("bahnsteig"))
			return null;
		final Set<Plattform> plattforms = new HashSet<>();
		final String name = xml.get("name");
		
		for (final XML pf : xml.getInternXML()) {
			plattforms.add(get(pf.get("name")));
		}
		return new PlattformNeighbour(get(name), plattforms);
	}

	private static Plattform get(final String name) {
		return get(name, name);
	}
}
