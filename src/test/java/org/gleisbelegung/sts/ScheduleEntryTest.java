package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsScheduleEntryInterface;
import org.gleisbelegung.database.StsScheduleInterface;
import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;
import org.junit.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ScheduleEntryTest {
    @Test
    public void testTimeToMinute() {
        assertEquals(638, ScheduleEntry.timeToMinutes("10:38"));
    }

    @Test
    public void testGetTimeDiff() {
        assertEquals(240000, ScheduleEntry.getTimeDiff(0, 0, 4));
        assertEquals(239999, ScheduleEntry.getTimeDiff(1, 0, 4));
        assertEquals(0, ScheduleEntry.getTimeDiff(0, 4, -4));
    }

    @Test
    public void testTimeDiffToString() {
        assertEquals("", ScheduleEntry.timeDiffToString(0, false));
        assertEquals("", ScheduleEntry.timeDiffToString(0, true));

        assertEquals("- 99:00", ScheduleEntry.timeDiffToString(TimeUnit.MINUTES.toMillis(99), true));
        assertEquals("   1.66", ScheduleEntry.timeDiffToString(TimeUnit.MINUTES.toMillis(100), false));
        assertEquals(" - -- -", ScheduleEntry.timeDiffToString(TimeUnit.HOURS.toMillis(-1000), false));

        assertEquals("  10:00", ScheduleEntry.timeDiffToString(TimeUnit.MINUTES.toMillis(10), false));
        assertEquals("  10:00", ScheduleEntry.timeDiffToString(TimeUnit.MINUTES.toMillis(-10), true));

        assertEquals("- 10:00", ScheduleEntry.timeDiffToString(TimeUnit.MINUTES.toMillis(10), true));
        assertEquals("+ 10:00", ScheduleEntry.timeDiffToString(TimeUnit.MINUTES.toMillis(-10), false));
    }

    @Test
    public void testGetTimeDiffDepatureAndArrival() {
        ScheduleEntry se = new ScheduleEntry(null, null, 638, 637, null);

        assertEquals("- 38:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, false, true));
        assertEquals("- 39:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 2, false, true));

        assertEquals("  38:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, false, false));
        assertEquals("  39:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 2, false, false));

        assertEquals("  38:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, true, false));
        assertEquals("  39:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 2, true, false));

        assertEquals("  38:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, true, true));
        assertEquals("  38:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), -1, true, true));
        assertEquals("  38:00", se.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 1, true, true));

        assertEquals("  37:00", se.getTimeDiffArrival(TimeUnit.HOURS.toMillis(10), 0));
        assertEquals("  36:00", se.getTimeDiffArrival(TimeUnit.HOURS.toMillis(10), -1));
        assertEquals("  38:00", se.getTimeDiffArrival(TimeUnit.HOURS.toMillis(10), 1));


        assertEquals("", ScheduleEntry.EMPTY.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, false, true));

        assertEquals("", ScheduleEntry.EMPTY.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, false, false));

        assertEquals("", ScheduleEntry.EMPTY.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, true, false));

        assertEquals("", ScheduleEntry.EMPTY.getTimeDiffDepature(TimeUnit.HOURS.toMillis(10), 0, true, true));

        assertEquals(" - -- -", ScheduleEntry.EMPTY.getTimeDiffArrival(TimeUnit.HOURS.toMillis(10), 0));
    }

    @Test
    public void testToString() {
        ScheduleEntry se = new ScheduleEntry(Platform.get("a"), Platform.get("b"), 638, 637, new ScheduleFlags("D"));
        assertEquals("b(D)", se.toString());
    }

    @Test
    public void testUpdate() {
        XML initXML = XML.generateEmptyXML("zugfahrplan");
        XML seXML = XML.generateEmptyXML("gleis")
                .set("name", "1")
                .set("plan", "1")
                .set("an", "10:37")
                .set("ab", "10:38")
                .set("flags", "");
        initXML = initXML.addInternXML(seXML).addInternXML(
                XML.generateEmptyXML("gleis")
                        .set("name", "bar")
                        .set("plan", "bar")
        );
        List<ScheduleEntry> entries = new LinkedList<>();
        StsTrainInterface train = new StsTrainInterface() {
            @Override
            public Integer getId() {
                return null;
            }

            @Override
            public <T extends StsTrainDetailsInterface> T getDetails() {
                return null;
            }

            @Override
            public <T extends StsScheduleInterface> T getSchedule() {
                return (T) new StsScheduleInterface() {

                    @Override
                    public <T extends StsScheduleEntryInterface> Iterator<T> iterator() {
                        return (Iterator<T>) entries.iterator();
                    }
                };
            }

            @Override
            public <T extends StsTrainInterface> void setPredecessor(T predecessor) {

            }

            @Override
            public <T extends StsTrainInterface> void setSuccessor(T successor) {

            }
        };
        ScheduleEntry se = new ScheduleEntry(Platform.get("1"), Platform.get("1"), 638, 637, new ScheduleFlags());
        entries.add(se);
        entries.add(new ScheduleEntry(Platform.get("foo"), Platform.get("foo"), 700, 700, new ScheduleFlags()));

        // actual test stop at platform 1 is moved to 2
        seXML.set("name", "2");
        ScheduleEntry.updateWithExisting(initXML.getInternXML(), train, entries);
        assertEquals(Platform.get("2"), se.getPlatform());

        initXML.getInternXML().remove(0);
        entries.remove(0);
        ScheduleEntry.updateWithExisting(initXML.getInternXML(), train, entries);
    }
}