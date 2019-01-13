package data;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.gleisbelegung.xml.XML;

public class Plattform {

    private final String name;
    public final Set<Plattform> neighbours = new HashSet<>();

    public Plattform(final String name) {
        this.name = name;
    }

    public static XML toXML(List<Plattform> plattformList) {
        XML xmlList = XML.generateEmptyXML("bahnsteigliste");
        for (Plattform p : plattformList) {
            XML xmlPlattform = XML.generateEmptyXML("bahnsteig")
                    .set("name", p.name);
            for (Plattform pn : p.neighbours) {
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
