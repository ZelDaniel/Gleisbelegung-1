package data;

import frontend.Console;
import org.gleisbelegung.xml.XML;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class Train {
    enum Event {
        NONE, ARRIVAL, DEPATURE, ENTER, LEAVE;

        public static Event getByArt(String art) {
            switch (art) {
                case "einfahrt":
                    return ENTER;
                case "ankunft":
                    return ARRIVAL;
                case "abfahrt":
                    return DEPATURE;
                case "ausfahrt":
                    return LEAVE;
            }

            return null;
        }
    }
    public final String name;
    public String verspaetung = "0";
    public String plangleis = "4A";
    public String gleis = "4A";
    public String von = "A-Hausen";
    public String nach = "B-Stadt";
    public Boolean sichtbar = Boolean.FALSE;
    public Boolean amgleis = Boolean.FALSE;
    public Schedule schedule = new Schedule();

    private Event registeredEvent = Event.NONE;

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
            if (Modifier.isPublic(field.getModifiers())) {
                try {
                    xml.set(field.getName(), field.get(this).toString());
                } catch (IllegalAccessException e) {
                }
            }
        }

        return xml;
    }

    public XML scheduleToXml(Integer id) {
        return schedule.toXML(id);
    }

    public static XML toXmlList(Iterable<Map.Entry<Integer, Train>> trains) {
        XML xmlList = XML.generateEmptyXML("zugliste");
        for (Map.Entry<Integer, Train> train : trains) {
            xmlList = xmlList.addInternXML(XML.generateEmptyXML("zug")
                    .set("zid", train.getKey().toString())
                    .set("name", train.getValue().name)
            );
        }

        return xmlList;
    }

    public void triggerArrival(Integer id, Console console, boolean withDFlag) {
        amgleis = withDFlag;
        if (registeredEvent == Event.ARRIVAL) {
            console.queueXml(toXml(id));
        }
    }

    public void triggerDepature(Integer id, Console console) {
        amgleis = false;
        if (registeredEvent == Event.DEPATURE) {
            console.queueXml(toXml(id));
        }
    }

    public void setRegisteredEvent(String event) {
        registeredEvent = Event.getByArt(event);
    }
}
