package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsPlatformInterface;
import org.gleisbelegung.database.StsScheduleInterface;
import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;


public class ScheduleTest {

    private Schedule createSchedule() {
        return Schedule.parse(
                XML.generateEmptyXML("zugfahrplan")
                        .addInternXML(
                                XML.generateEmptyXML("gleis")
                                    .set("name", "1")
                                    .set("plan", "2")
                        )
                        .addInternXML(
                                XML.generateEmptyXML("gleis")
                                        .set("name", "5")
                                        .set("plan", "5")
                        )
                        .addInternXML(
                                XML.generateEmptyXML("gleis")
                                        .set("name", "7")
                                        .set("plan", "7")
                                        .set("flags", "L")
                        )
                        .addInternXML(
                                XML.generateEmptyXML("gleis")
                                        .set("name", "9")
                                        .set("plan", "9")
                                        .set("flags", "W[4][4]")
                        )
                        .addInternXML(
                                XML.generateEmptyXML("gleis")
                                        .set("name", "5")
                                        .set("plan", "5")
                        ),
                new StsTrainInterface() {
                    @Override
                    public Integer getId() {
                        return Integer.valueOf(10);
                    }

                    @Override
                    public <T extends StsTrainDetailsInterface> T getDetails() {
                        return (T) new StsTrainDetailsInterface() {
                            @Override
                            public StsPlatformInterface getPlatform() {
                                return Platform.get("1");
                            }
                        };
                    }

                    @Override
                    public <T extends StsScheduleInterface> T getSchedule() {
                        return null;
                    }

                    @Override
                    public <T extends StsTrainInterface> void setPredecessor(T predecessor) {

                    }

                    @Override
                    public <T extends StsTrainInterface> void setSuccessor(T successor) {

                    }
                },
                null
        );
    }

    @Test
    public void testGetFirstEntry() {
        Schedule s = createSchedule();
        ScheduleEntry first = s.getFirstEntry();
        assertEquals(Platform.get("1"), first.getPlatform());
        assertEquals(Platform.get("2"), first.getPlatformPlanned());
    }

    @Test
    public void testAdvance() {
        Schedule s = createSchedule();
        ScheduleEntry next = s.getNextEntry();
        s.advance();
        assertEquals(next, s.getCurrentEntry());
    }

     @Test
    public void testGetTrain() {
        Schedule s = createSchedule();
        StsTrainInterface train = s.getTrain();
        assertEquals(Integer.valueOf(10), train.getId());
    }

    @Test
    public void testGetNextL() {
        Schedule s = createSchedule();
        ScheduleEntry lEntry = s.getNextL();
        assertEquals(Platform.get("7"), lEntry.getPlatform());
        while (s.getCurrentEntry() != lEntry) {
            s.advance();
        }
        s.advance();
        assertNull(s.getNextL());
        while (s.getCurrentEntry() != s.getLastEntry()) {
            s.advance();
        }
        s.advance();
        assertNull(s.getNextL());
    }

    @Test
    public void testGetNextW() {
        Schedule s = createSchedule();
        ScheduleEntry wEntry = s.getNextW();
        assertEquals(Platform.get("9"), wEntry.getPlatform());
        while (s.getCurrentEntry() != wEntry) {
            s.advance();
        }
        s.advance();
        assertNull(s.getNextW());
        while (s.getCurrentEntry() != s.getLastEntry()) {
            s.advance();
        }
        s.advance();
        assertNull(s.getNextW());
    }

    @Test
    public void testGetNextEntry() {
        Schedule s = createSchedule();
        while (s.getLastEntry() != s.getCurrentEntry()) {
            s.advance();
        }
        assertEquals(ScheduleEntry.EMPTY, s.getNextEntry());
        s.advance();
        assertEquals(ScheduleEntry.EMPTY, s.getNextEntry());
    }

    @Test
    public void testUpdatePos() {
        Schedule s = createSchedule();

        assertTrue(s.updatePos(Platform.get("non existing")));
        assertTrue(s.updatePos(Platform.get("1")));
        assertFalse(s.updatePos(Platform.get("1")));
        assertTrue(s.updatePos(Platform.get("5")));
    }

    @Test
    public void testUpdateWithXml() {
        Schedule s = createSchedule();
        s.updateByXml(XML.generateEmptyXML("zugfahrplan"));
        assertEquals(Platform.get("1"), s.getCurrentEntry().getPlatform()); //Update by XML should only influence the flags of the entries
    }
}