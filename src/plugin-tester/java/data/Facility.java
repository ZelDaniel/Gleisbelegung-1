package data;

import org.gleisbelegung.xml.XML;

public class Facility {

    private final String name;
    private final int simbuild;
    private final int aid;

    public Facility(final String name, int simbuild, int aid) {
        this.name = name;
        this.simbuild = simbuild;
        this.aid = aid;
    }

    public String getName() {
        return this.name;
    }

    public XML toXML()
    {
        return XML.generateEmptyXML("anlageninfo")
                .set("name", name)
                .set("simbuild", Integer.toString(simbuild))
                .set("aid", Integer.toString(aid));
    }
}
