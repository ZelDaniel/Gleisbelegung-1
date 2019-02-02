package org.gleisbelegung.sts;

import org.gleisbelegung.xml.XML;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DetailsTest {

    private static final XML minimalXml = XML.generateEmptyXML("zugdetails")
        .set("verspaetung", "1")
        .set("von", "")
        .set("nach", "")
        .set("amgleis", "false");

    @Test
    public void testParseMinimalXml() {
        Details.parse(minimalXml);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidXml() {
        Details.parse(XML.generateEmptyXML("foo"));
    }

    @Test
    public void testDelay() {
        XML xml = minimalXml.set("verspaetung", "-1");
        assertEquals(-1, Details.parse(xml).getDelay());
        xml.set("verspaetung", "1");
        assertEquals(1, Details.parse(xml).getDelay());
    }

    @Test
    public void testAtPlatform() {
        XML xml = minimalXml.set("amgleis", "false");
        assertFalse(Details.parse(xml).isAtPlatform());
        xml.set("amgleis", "true");
        assertTrue(Details.parse(xml).isAtPlatform());
    }

    @Test
    public void testEquals() throws Exception {
        Details master = Details.parse(minimalXml.set("name", "").set("plangleis", ""));
        XML xml;
        xml = XML.parse(minimalXml.toString());
        assertTrue(Details.parse(xml).equals(master));

        xml.set("gleis", "A");
        assertFalse(Details.parse(xml).equals(master));

        xml = XML.parse(minimalXml.toString());
        xml.set("nach", "A");
        assertFalse(Details.parse(xml).equals(master));

        xml = XML.parse(minimalXml.toString());
        xml.set("von", "A");
        assertFalse(Details.parse(xml).equals(master));


        xml = XML.parse(minimalXml.toString());
        master.toggleAtPlatform();
        assertFalse(Details.parse(xml).equals(master));
        master.toggleAtPlatform();

        xml = XML.parse(minimalXml.toString());
        xml.set("name", "A");
        assertFalse(Details.parse(xml).equals(master));

        assertFalse(Details.parse(xml.set("von", "B")).equals(Details.parse(xml.set("von", "A"))));
    }

    @Test
    public void testVisible() {
        assertFalse(Details.parse(minimalXml).isVisible());
        Details d = Details.parse(minimalXml);
        d.setVisible();
        assertTrue(d.isVisible());
        d.setInvisible();
        assertFalse(d.isVisible());
        assertTrue(Details.parse(minimalXml.set("sichtbar", "true")).isVisible());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("null null -> null 1  X", Details.parse(minimalXml).toString());
        XML initXML = XML.parse(minimalXml.toString())
                .set("gleis", "1")
                .set("name", "RB 1")
                .set("plan", "2")
                .set("von", "A")
                .set("nach", "B")
                .set("amgleis", "true")
                .set("sichtbar", "true")
                .set("verspaetung", "-1");
        assertEquals("RB 1 A -> B -1 -", Details.parse(initXML).toString());
        assertEquals("RB 1 A -> B -1 ", Details.parse(initXML.set("amgleis", "false")).toString());
    }
}
