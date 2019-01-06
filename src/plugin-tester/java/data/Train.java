package data;

import org.gleisbelegung.xml.XML;

import java.lang.reflect.Field;
import java.util.Map;

public class Train {
    public final String name;
    public String verspaetung = "0";
    public String plangleis = "4A";
    public String gleis = "4A";
    public String von = "A-Hausen";
    public String nach = "B-Stadt";
    public Boolean sichtbar = Boolean.FALSE;
    public Boolean amgleis = Boolean.FALSE;
    public Schedule schedule = new Schedule();

    public Train(String name) {
        this.name = name;
    }

    public XML toXml(Integer id) {
        XML xml = XML.generateEmptyXML("zugdetails");
        xml.set("zid", id.toString());
        for (Field field : Train.class.getDeclaredFields()) {
            if (field.getName().equals("schedule")) {
                continue;
            }
            try {
                xml.set(field.getName(), field.get(this).toString());
            } catch (IllegalAccessException e) {
            }
        }

        return xml;
    }

    public XML scheduleToXml(Integer id) {
        return schedule.toXML(id);
    }

    public static XML toXML(Iterable<Map.Entry<Integer, Train>> trains) {
        XML xmlList = XML.generateEmptyXML("zugliste");
        for (Map.Entry<Integer, Train> train : trains) {
            xmlList = xmlList.addInternXML(XML.generateEmptyXML("zug")
                    .set("zid", train.getKey().toString())
                    .set("name", train.getValue().name)
            );
        }

        return xmlList;
    }
}
