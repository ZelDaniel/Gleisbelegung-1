package org.gleisbelegung.sts;

import org.gleisbelegung.xml.XML;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class FacilityTest {

    @Test
    public void test() {
        Facility f = Facility.parse(XML.generateEmptyXML("anlageninfo")
                .set("aid", "4")
                .set("simbuild", "3")
                .set("name", "test")
        );
        assertEquals(4, f.getAid());
        assertEquals(3, f.getSimbuild());
        assertEquals("test", f.getName());
    }
}