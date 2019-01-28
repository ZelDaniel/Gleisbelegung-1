package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsPlatformInterface;
import org.gleisbelegung.database.StsScheduleEntryInterface;
import org.gleisbelegung.database.StsScheduleFlagsInterface;
import org.gleisbelegung.database.StsScheduleInterface;
import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class ScheduleFlagsTest {


    @Test(expected = IllegalArgumentException.class)
    public void testParseWithWrongKey() {
        ScheduleFlags.parse(XML.generateEmptyXML("some.xml"));
    }

    @Test
    public void testParseEmpty() {
        ScheduleFlags.parse(XML.generateEmptyXML("gleis"));
    }

    @Test
    public void testParseA() {
        assertTrue(
                ScheduleFlags.parse(
                        XML.generateEmptyXML("gleis").set("flags", "A")
                )
                .hasA()
        );
    }

    @Test
    public void testParseD() {
        assertTrue(
                ScheduleFlags.parse(
                        XML.generateEmptyXML("gleis").set("flags", "D")
                )
                .hasD()
        );
    }

    @Test
    public void testParseE() {
        XML xml = XML.generateEmptyXML("gleis").set("flags", "E(2)");
        final Map<Integer, StsTrainInterface> trains = new HashMap<>();

        trains.put(2, new StsTrain(2));

        ScheduleFlags flags = ScheduleFlags.parse(xml);

        assertNull(flags.getE());
        assertTrue(flags.hasE());

        flags = ScheduleFlags.parse(xml);
        assertTrue(flags.hasE());

        ScheduleFlags.parse(xml);
    }

    @Test
    public void testParseF() {
        XML xml = XML.generateEmptyXML("gleis").set("flags", "F(2)");
        final Map<Integer, StsTrainInterface> trains = new HashMap<>();

        trains.put(2, new StsTrain(2));

        ScheduleFlags flags = ScheduleFlags.parse(xml);
        assertNull(flags.getF());
        assertTrue(flags.hasF());

        flags = ScheduleFlags.parse(xml);
        assertTrue(flags.hasF());
    }

    @Test
    public void testParseK() {
        XML xml = XML.generateEmptyXML("gleis").set("flags", "K(2)");
        final Map<Integer, StsTrainInterface> trains = new HashMap<>();

        trains.put(2, new StsTrain(2));

        ScheduleFlags flags = ScheduleFlags.parse(xml);
        assertNull(flags.getK());
        assertTrue(flags.hasK());

        flags = ScheduleFlags.parse(xml);
        assertTrue(flags.hasK());
    }

    @Test
    public void testParseL() {
        assertTrue(
                ScheduleFlags.parse(
                        XML.generateEmptyXML("gleis").set("flags", "L")
                )
                .hasL()
        );
    }

    @Test
    public void testParseR() {
            assertTrue(
                    ScheduleFlags.parse(
                            XML.generateEmptyXML("gleis").set("flags", "R2")
                    )
                    .hasR()
            );
    }

    @Test
    public void testParseW() {
        assertTrue(
                ScheduleFlags.parse(
                        XML.generateEmptyXML("gleis").set("flags", "W[1][1]")
                )
                .hasW()
        );
    }

    @Test
    public void testParseComplexFlag() {
        Map<Integer, StsTrainInterface> trains = new HashMap<>();
        for (int i = 1; i < 6; ++i) {
            trains.put(i, new StsTrain(i));
        }
        ScheduleFlags flags = ScheduleFlags.parse(
                XML.generateEmptyXML("gleis").set("flags", "W[1][4]B1AE4(2)F2(4)K5(3)R6P[l]")
        );
        assertTrue(flags.hasW() && flags.hasA() && flags.hasR() && flags.hasK() && flags.hasE());
        assertTrue(flags.toString().matches("^[AEFKRW]+$"));
    }
}

class StsTrain implements StsTrainInterface {

    Integer id;
    StsTrainInterface successor, predecessor;

    StsTrain(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public <T extends StsTrainInterface> void setPredecessor(final T predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public void setSuccessor(final StsTrainInterface successor) {
        this.successor = successor;
    }

    @Override
    public StsScheduleInterface getSchedule() {
        return new StsScheduleInterface() {
            @Override
            public <T extends StsScheduleEntryInterface> Iterator<T> iterator() {
                return new Iterator<T>() {

                    StsScheduleEntryInterface next = new StsScheduleEntryInterface() {
                        @Override
                        public int getArrival() {
                            return 0;
                        }

                        @Override
                        public int getDepature() {
                            return 0;
                        }

                        @Override
                        public StsScheduleFlagsInterface getFlags() {
                            return new ScheduleFlags();
                        }

                        @Override
                        public StsPlatformInterface getPlatformPlanned() {
                            return new StsPlatformInterface() {

                                @Override
                                public boolean equals(Object obj) {
                                    return true;
                                }
                            };
                        }
                    };

                    @Override
                    public boolean hasNext() {
                        return next != null;
                    }

                    @Override
                    public T next() {
                        StsScheduleEntryInterface next = this.next;
                        this.next = null;

                        return (T) next;
                    }
                };
            }
        };
    }

    @Override
    public StsTrainDetailsInterface getDetails() {
        return new StsTrainDetailsInterface() {
            @Override
            public StsPlatformInterface getPlatform() {
                return null;
            }
        };
    }
}
