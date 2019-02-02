package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsScheduleInterface;
import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

public class EventTest {

    final StsTrainInterface testTrain = new StsTrainInterface() {
        @Override
        public Integer getId() {
            return Integer.valueOf(1);
        }

        @Override
        public StsTrainDetailsInterface getDetails() {
            return null;
        }

        @Override
        public StsScheduleInterface getSchedule() {
            return null;
        }

        @Override
        public <T extends StsTrainInterface> void setPredecessor(T predecessor) {

        }

        @Override
        public <T extends StsTrainInterface> void setSuccessor(T successor) {

        }
    };

    @Test
    public void parseXmlEnter() {
        Event event = Event.parse(XML.generateEmptyXML("ereignis")
                .set("art", "einfahrt")
                .set("zid", "1")
                .set("von", "")
                .set("nach", "")
                .set("amgleis", "false")
                .set("sichtbar", "true")
                .set("verspaetung", "-9"),
                testTrain
        );
        assertEquals(Event.EventType.ENTER, event.getType());
        assertEquals(Platform.EMPTY, event.getDetails().getPlatform());
    }

    @Test
    public void parseXmlArrival() {
        assertEquals(Event.EventType.ARRIVAL, Event.parse(XML.generateEmptyXML("ereignis")
                .set("art", "ankunft")
                .set("zid", "1")
                .set("von", "")
                .set("nach", "")
                .set("verspaetung", "0"),
                testTrain
            ).getType()
        );
    }

    @Test
    public void parseXmlDepature() {
        assertEquals(Event.EventType.DEPARTURE, Event.parse(XML.generateEmptyXML("ereignis")
                        .set("art", "abfahrt")
                        .set("zid", "1")
                        .set("von", "")
                        .set("nach", "")
                        .set("verspaetung", "0"),
                testTrain
                ).getType()
        );
    }

    @Test
    public void parseXmlExit() {
        assertEquals(Event.EventType.EXIT, Event.parse(XML.generateEmptyXML("ereignis")
                        .set("art", "ausfahrt")
                        .set("zid", "1")
                        .set("von", "")
                        .set("nach", "")
                        .set("verspaetung", "0"),
                testTrain
                ).getType()
        );
    }

    @Test
    public void testGetters() {
        long time = System.currentTimeMillis();
        Event e = new Event(Event.EventType.ENTER, testTrain, time);
        assertEquals(time, e.getTime());
    }

    @Test
    public void testToString() {
        assertEquals("einfahrt", Event.EventType.ENTER.getKey());
        assertEquals("ENTER", new Event(Event.EventType.ENTER, testTrain, 0).toString());
        assertEquals("ankunft", Event.EventType.ARRIVAL.getKey());
        assertEquals("abfahrt", Event.EventType.DEPARTURE.getKey());
        assertEquals("ausfahrt", Event.EventType.EXIT.getKey());
    }

    @Test
    public void parseUnknown() {
        assertNull(Event.parse(XML.generateEmptyXML("ereignis").set("art", "something weird"), testTrain));
    }
}