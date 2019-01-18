package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

public class TrainListTest {

    @Test
    public void parseEmptyList() {
        assertTrue(new Trainlist().update(
                XML.generateEmptyXML("zugliste")
        ).isEmpty());
    }

    @Test
    public void workWithList() {
        Trainlist list = new Trainlist().update(
                XML.generateEmptyXML("zugliste")
                        .addInternXML(XML.generateEmptyXML("zug")
                                .set("zid", "1")
                                .set("name", "Train")
                        )
                        .addInternXML(XML.generateEmptyXML("zug")
                                .set("zid", "2")
                                .set("name", "Train")
                        )
        );
        assertFalse(list.isEmpty());
        assertNotNull(list.get("1"));
        assertNotNull(list.get(Integer.valueOf(1)));
        Train t = list.get(1);
        assertEquals(Integer.valueOf(1), list.get(1).getId());
        list.remove(2);
        assertFalse(list.isEmpty());
        assertNotNull(list.get("1"));
        list.remove(t);
        assertTrue(list.isEmpty());
        assertNull(list.get(1));
    }

    @Test
    public void updateList() {
        Trainlist list = new Trainlist();
        list.update(XML.generateEmptyXML("zugliste")
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "1")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "2")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "4")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "-1")
                        .set("name", "Train")
                )
        );
        list.remove(1);
        list.update(XML.generateEmptyXML("zugliste")
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "4")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "1")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "3")
                        .set("name", "Train")
                )
        );
        assertNull(list.get(2));
        assertNull(list.get(1));
        assertNotNull(list.get(4));
    }

    @Test
    public void toList() {
        Trainlist list = new Trainlist();
        list.update(XML.generateEmptyXML("zugliste")
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "1")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "2")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "4")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "-1")
                        .set("name", "Train")
                )
        );
        assertEquals(4, list.toList().size());
    }

    @Test
    public void toMap() {
        Trainlist list = new Trainlist();
        list.update(XML.generateEmptyXML("zugliste")
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "1")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "2")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "4")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "-1")
                        .set("name", "Train")
                )
        );
        assertEquals(4, list.toMap().size());
    }

    @Test(expected=java.util.ConcurrentModificationException.class)
    public void iterator() {
        Trainlist list = new Trainlist();
        list.update(XML.generateEmptyXML("zugliste")
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "1")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "2")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "4")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "-1")
                        .set("name", "Train")
                )
        );

        // provoke concurrent modification with iterator
        Iterator iter = list.iterator();
        list.remove(1);
        iter.next();
    }

    public void iteratorByList() {
        Trainlist list = new Trainlist();
        list.update(XML.generateEmptyXML("zugliste")
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "1")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "2")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "4")
                        .set("name", "Train")
                )
                .addInternXML(XML.generateEmptyXML("zug")
                        .set("zid", "-1")
                        .set("name", "Train")
                )
        );

        // provoke concurrent modification with iterator
        Iterator<? extends StsTrainInterface> iter = list.toList().iterator();
        list.remove(1);
        assertEquals(Integer.valueOf(1), iter.next().getId());
    }
}