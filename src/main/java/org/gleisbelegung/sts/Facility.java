package org.gleisbelegung.sts;

import org.gleisbelegung.xml.XML;

public class Facility {

    private final String name;
    private final int simbuild;
    private final int aid;

    private Facility(final String name, final int simbuild, final int aid) {
        this.name = name;
        this.simbuild = simbuild;
        this.aid = aid;
    }

    public static Facility parse(final XML xml) {
        return new Facility(xml.get("name"), Integer.parseInt(xml.get("simbuild")), Integer.parseInt(xml.get("aid")));
    }

    public String getName() {
        return this.name;
    }

    public int getSimbuild() {
        return this.simbuild;
    }

    public int getAid() {
        return this.aid;
    }
}
