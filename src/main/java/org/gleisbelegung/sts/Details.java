package org.gleisbelegung.sts;

import org.gleisbelegung.xml.XML;

/**
 * Represents the information of zugdetails
 */
public class Details {

	public static Details parse(final XML xml) {
		if (!xml.getKey().equals("zugdetails")) {
			throw new IllegalArgumentException();
		}
		final String name = xml.get("name");
		final String plan = xml.get("plangleis");
		final String track = xml.get("gleis");
		final Plattform plattformPlanned = Plattform.get(plan);
		final Plattform plattform = Plattform.get(track);
		final String target = xml.get("nach");
		final String source = xml.get("von");
		final int delay = Integer.parseInt(xml.get("verspaetung"));
		final boolean atPlattform = Boolean.parseBoolean(xml.get("amgleis"));
		final boolean visible = Boolean.parseBoolean(xml.get("sichtbar"));
		return new Details(name, plattformPlanned, plattform, target, source, delay, atPlattform,
				visible);
	}

	final String name;
	final Plattform plattform;
	final Plattform plattformPlanned;
	String target;
	String source;
	final int delay;
	boolean atPlattform;
	
	private long updated_at = System.currentTimeMillis();

	private boolean visible;

	private Details(final String name, final Plattform plattformPlanned, final Plattform plattform,
			final String target, final String source, final int delay,
			final boolean atPlattform, final boolean visible) {
		this.name = name;
		this.plattform = plattform;
		this.plattformPlanned = plattformPlanned;
		this.target = target.isEmpty() ? null : target;
		this.source = source.isEmpty() ? null : source;
		this.atPlattform = atPlattform;
		this.delay = delay;
		this.visible = visible;
	}

	public boolean isAtPlattform() {
		return this.atPlattform;
	}

	public String from() {
		return this.source;
	}

	public int getDelay() {
		return this.delay;
	}

	public Plattform getPlattform() {
		return this.plattform;
	}

	public void setVisible() {
		this.visible = true;
		this.updated_at = System.currentTimeMillis();
	}
	
	public boolean equals(final Details o) {
		if (this.atPlattform ^ o.atPlattform || this.visible ^ o.visible)
			return false;
		if (source == null ^ o.source == null) { 
			return false;
		}
		if (target == null ^ o.target == null) { 
			return false;
		}
		if (source != null && !source.equals(o.source) || target != null && !target.equals(o.target)) {
			return false;
		}
		if (plattform != o.plattform || delay != o.delay) {
			return false;
		}
		if (!name.equals(o.name)) {
			return false;
		}
		return true;
		
	}

	public String to() {
		return this.target;
	}

	@Override
	public String toString() {
		return this.name + " " + this.source + " -> " + this.target + " "
				+ this.delay + " "
				+ (!this.visible ? " X" : (this.atPlattform ? " -" : ""));
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setSource(final String source) {
		this.source = source;
	}
}
