package org.gleisbelegung.sts;

import org.gleisbelegung.xml.XML;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class PlatformTest {

    @Test
    public void test() {
        Platform p4 = Platform.parse(XML.generateEmptyXML("gleis")
                .set("name", "4")
                .addInternXML(
                        XML.generateEmptyXML("n")
                            .set("name", "5")
                ));
        assertTrue(p4.getNeighbours().isEmpty());
        Platform p5 = Platform.parse(XML.generateEmptyXML("gleis")
                .set("name", "5")
                .addInternXML(
                        XML.generateEmptyXML("n")
                                .set("name", "4")
                ));
        assertFalse(p4.getNeighbours().isEmpty());
        assertEquals(1, p4.getNeighbours().size());
        assertEquals(p5, p4.getNeighbours().iterator().next());
    }
}