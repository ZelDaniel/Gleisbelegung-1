package org.gleisbelegung.sts;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

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
}