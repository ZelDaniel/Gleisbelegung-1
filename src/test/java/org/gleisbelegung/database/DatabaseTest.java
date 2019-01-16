package org.gleisbelegung.database;

import org.gleisbelegung.sts.Schedule;
import org.gleisbelegung.xml.XML;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DatabaseTest {

    @Test
    public void testRegisterScheduleAndIterate() throws Exception {
        Schedule testSchedule = Schedule.parse(XML.generateEmptyXML("zugfahrplan"), null, null);
        Database.getInstance().registerSchedule(testSchedule);
        assertEquals(testSchedule, Database.getInstance().getScheduleIterator().next());

        // set schedule to null to remove it by the garbage collection
        testSchedule = null;
        System.gc();
        assertFalse(Database.getInstance().getScheduleIterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterateScheduleWithNoneExisting() {
        Database.getInstance().getScheduleIterator().next();
    }
}
