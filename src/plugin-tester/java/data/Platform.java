package data;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.gleisbelegung.xml.XML;

public class Platform {

    public final Set<Platform> neighbours = new HashSet<>();
    public final String name;

    public Platform(final String name) {
        this.name = name;
    }

    public static XML toXML(List<Platform> platformList) {
        XML xmlList = XML.generateEmptyXML("bahnsteigliste");
        for (Platform p : platformList) {
            XML xmlPlattform = XML.generateEmptyXML("bahnsteig")
                    .set("name", p.name);
            for (Platform pn : p.neighbours) {
                xmlPlattform = xmlPlattform.addInternXML(
                        XML.generateEmptyXML("n")
                                .set("name", pn.name)
                );
            }
            xmlList = xmlList.addInternXML(xmlPlattform);
        }

        return xmlList;
    }
}
