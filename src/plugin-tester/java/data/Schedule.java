package data;

import org.gleisbelegung.xml.XML;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class Schedule {

    public class ScheduleEntry {

        public String plan;
		public String name;
		public String ab;
		public String an;
		public String flags;

		private boolean visited = false;

		ScheduleEntry(String planned, String actual, String arrival, String depature, String flags) {
		    this.plan = planned;
		    this.name = actual;
		    this.ab = depature;
		    this.an = arrival;
		    this.flags = flags;
        }

		XML toXML() {
            XML xml = XML.generateEmptyXML("gleis");
            for (Field f : this.getClass().getFields()) {
                if (Modifier.isPublic(f.getModifiers())) {
                    try {
                        xml.set(f.getName(), f.get(this).toString());
                    } catch (IllegalAccessException e) {
                    }
                }
            }

            return xml;
        }

        public void setVisited() {
		    visited = true;
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
        return createNewEntry(planned, actual, arrival, depature, "");
    }

    public Schedule createNewEntry(String planned, String actual, String arrival, String depature, String flags) {
        this.entries.add(new ScheduleEntry(planned, actual, arrival, depature, flags));

        return this;
    }

    private XML addEntries(XML xml) {
        for (ScheduleEntry se : entries) {
            xml = xml.addInternXML(se.toXML());
        }

        return xml;
    }

    public ScheduleEntry getNext() {
        for (ScheduleEntry se : entries) {
            if (!se.visited) {
                return se;
            }
        }

        return null;
    }
}
