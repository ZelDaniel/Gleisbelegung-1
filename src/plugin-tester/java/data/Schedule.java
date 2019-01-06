package data;

import org.gleisbelegung.xml.XML;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class Schedule {

    class ScheduleEntry {

        public String plan;
		public String name;
		public String ab;
		public String an;

		ScheduleEntry(String planned, String actual, String arrival, String depature) {
		    this.plan = planned;
		    this.name = actual;
		    this.ab = depature;
		    this.an = arrival;
        }

		XML toXML() {
            XML xml = XML.generateEmptyXML("gleis");
            for (Field f : this.getClass().getFields()) {
                try {
                    xml.set(f.getName(), f.get(this).toString());
                } catch (IllegalAccessException e) {
                }
            }

            return xml;
        }

    }

    public final List<ScheduleEntry> entries;

    public Schedule() {
        entries = new LinkedList<>();
    }

    public XML toXML(Integer id) {
        return addEntries(XML.generateEmptyXML("zugfahrplan")
                .set("zid", id.toString()));
    }

    public Schedule createNewEntry(String planned, String actual, String arrival, String depature) {
        this.entries.add(new ScheduleEntry(planned, actual, arrival, depature));

        return this;
    }

    private XML addEntries(XML xml) {
        for (ScheduleEntry se : entries) {
            xml = xml.addInternXML(se.toXML());
        }

        return xml;
    }
}
