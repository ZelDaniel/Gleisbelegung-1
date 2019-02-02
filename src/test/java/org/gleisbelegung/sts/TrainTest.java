package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsTrainInterface;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TrainTest {

    @Test
    public void testCompareTo() {
        Train t0 = new Train(1, "S1");
        Train t1 = new Train(2, "S1");
        Train t2 = new Train(3, "S2");
        assertTrue(t0.compareTo(t1) < 0);
        assertTrue(t2.compareTo(t1) > 0);
        assertTrue(t1.compareTo(t2) < 0);
    }

    @Test
    public void testSuccessor() {
        Train t0 = new Train(1, "S1");
        Train t1 = new Train(2, "T1");
        t0.setSuccessor(t1);
        assertEquals(t1, t0.getSuccessor());
    }

    @Test
    public void testPredecessor() {
        Train t0 = new Train(1, "S1");
        Train t1 = new Train(2, "T1");
        t0.setPredecessor(t1);
        assertEquals(t1, t0.getPredecessor());
    }

    @Test
    public void testGetName() {
        Train t = new Train(1, "DB");
        assertEquals("DB", t.getName());
    }
    @Test

    public void testGetNameId() {
        Train t = new Train(1, "DB42R");
        assertEquals(42, t.getNameId());
    }

    @Test
    public void testGetId() {
        Train t = new Train(1, "DB");
        assertEquals(Integer.valueOf(1), t.getId());
    }

    @Test
    public void testGetSchedule() {
        Train t = new Train(1, "DB");
        Schedule s = new Schedule(new ArrayList<>(), t);
        t.setSchedule(s);
        assertEquals(s, t.getSchedule());
    }

    @Test
    public void testGetDetails() {
        Train t = new Train(1, "DB");
        Details d = new Details(
                "foo",
                null,
                null,
                "",
                "",
                0,
                false,
                true
        );
        t.setPosition(d);
        assertEquals(d, t.getDetails());
    }

    @Test
    public void testUpdateByEvent() {
        Train t = new Train(1, "DB");
        Details d = new Details(
                "foo",
                Platform.EMPTY,
                Platform.EMPTY,
                "",
                "",
                0,
                false,
                true
        );
        t.setPosition(d);
        t.setSchedule(new Schedule(new ArrayList<>(), t));
        t.updateByEvent(new Event(Event.EventType.EXIT, t, d));
        assertEquals(d, t.getDetails());
        t.updateByEvent(new Event(Event.EventType.ARRIVAL, t, d));
        d.toggleAtPlatform();
        t.updateByEvent(new Event(Event.EventType.DEPARTURE, t, d));
        assertTrue(t.getDetails().isAtPlatform());
        d.toggleAtPlatform();
        t.updateByEvent(new Event(Event.EventType.DEPARTURE, t, d));
        assertFalse(t.getDetails().isAtPlatform());
        d.setInvisible();
        t.updateByEvent(new Event(Event.EventType.ENTER, t, d));
        assertFalse(t.getDetails().isVisible());
}

    @Test
    public void testGetType() {
        Train t = new Train(1, "ICE");
        assertEquals(TrainType.ICE, t.getType());
    }

    @Test
    public void testInvalidateGFlagByEvent() {
        Train tWithKFlag = new Train(1, "RB K");
        Train tWithGFlag = new Train(2, "RB G");
        Map<Integer, Train> trains = new HashMap<>();
        trains.put(1, tWithKFlag);
        trains.put(2, tWithGFlag);
        List<ScheduleEntry> sEntrieswithKFlag = new ArrayList<>();
        List<ScheduleEntry> sEntriesWithGFlag = new ArrayList<>();
        Schedule sWithKFlag = new Schedule(sEntrieswithKFlag, tWithKFlag);
        Schedule sWithGFlag = new Schedule(sEntriesWithGFlag, tWithGFlag);
        sEntriesWithGFlag.add(
                new ScheduleEntry(
                        Platform.get("1"), Platform.get("1"), 4, 0, new ScheduleFlags()
                )
        );
        tWithGFlag.setSchedule(sWithGFlag);
        ScheduleEntry sEntryWithKFlag = new ScheduleEntry(
                Platform.get("1"), Platform.get("1"), 1, 1, new ScheduleFlags("K(2)")
        );
        sEntrieswithKFlag.add(sEntryWithKFlag);
        tWithKFlag.setSchedule(sWithKFlag);
        tWithKFlag.setPosition(new Details(tWithKFlag.getName(), Platform.get("1"), Platform.get("1"), "", "", 0, false, false));
        tWithGFlag.setPosition(new Details(tWithGFlag.getName(), Platform.get("1"), Platform.get("1"), "", "", 0, false, false));
        sWithKFlag.getFlags().linkWithEntry(sEntryWithKFlag, tWithKFlag, trains);

        // reverse K is set
        assertEquals("G", sEntriesWithGFlag.iterator().next().getFlags().toString());

        // the target of K is leaving the platform
        tWithGFlag.updateByEvent(
                new Event(
                        Event.EventType.DEPARTURE,
                        tWithKFlag,
                        new Details(
                                tWithGFlag.getName(),
                                Platform.EMPTY,
                                Platform.EMPTY,
                                "",
                                "",
                                0,
                                false,
                                true
                        )
                )
        );
        assertEquals("", sEntriesWithGFlag.iterator().next().getFlags().toString());
    }

    @Test
    public void testInvalidateGFlagByRemoval() {
        Train tWithKFlag = new Train(1, "RB K");
        Train tWithGFlag = new Train(2, "RB G");
        Map<Integer, Train> trains = new HashMap<>();
        trains.put(1, tWithKFlag);
        trains.put(2, tWithGFlag);
        List<ScheduleEntry> sEntrieswithKFlag = new ArrayList<>();
        List<ScheduleEntry> sEntriesWithGFlag = new ArrayList<>();
        Schedule sWithKFlag = new Schedule(sEntrieswithKFlag, tWithKFlag);
        Schedule sWithGFlag = new Schedule(sEntriesWithGFlag, tWithGFlag);
        sEntriesWithGFlag.add(
                new ScheduleEntry(
                        Platform.get("1"), Platform.get("1"), 4, 0, new ScheduleFlags()
                )
        );
        tWithGFlag.setSchedule(sWithGFlag);
        ScheduleEntry sEntryWithKFlag = new ScheduleEntry(
                Platform.get("1"), Platform.get("1"), 1, 1, new ScheduleFlags("K(2)")
        );
        sEntrieswithKFlag.add(sEntryWithKFlag);
        tWithKFlag.setSchedule(sWithKFlag);
        tWithKFlag.setPosition(new Details(tWithKFlag.getName(), Platform.get("1"), Platform.get("1"), "", "", 0, false, false));
        tWithGFlag.setPosition(new Details(tWithGFlag.getName(), Platform.get("1"), Platform.get("1"), "", "", 0, false, false));
        sWithKFlag.getFlags().linkWithEntry(sEntryWithKFlag, tWithKFlag, trains);

        // reverse K is set
        assertEquals("G", sEntriesWithGFlag.iterator().next().getFlags().toString());

        tWithKFlag.removeCallback();
        assertEquals("", sEntriesWithGFlag.iterator().next().getFlags().toString());
    }

    @Test
    public void testSuccessorAndPredecessor() {
        Train t1 = new Train(1, "RB 1");
        Train t2 = new Train(2, "RB 2");
        t1.setPosition(new Details("RB 1", null, null, "A", "B", 0, false, false));
        t2.setPosition(new Details("RB 2", null, null, "C", "D", 0, false, false));
        ((StsTrainInterface) t2).setPredecessor(t1);
        assertEquals(null, t2.getDetails().from());
        assertNotEquals(null, t1.getDetails().to());
    }
}