package org.gleisbelegung.sts;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(Parameterized.class)
public class TrainTypeTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new LinkedList<>();
        try {
            for (Field f : TrainType.class.getDeclaredFields()) {
                if (f.getType() == TrainType.class && Modifier.isPublic(f.getModifiers())) {
                    f.setAccessible(true);
                    data.add(new Object[]{f.getName(), f.get(null),});
                }
            }
        } catch (IllegalAccessException e) {
            fail();
        }

        return data;
    }

    private static TrainType _previous;
    private final String name;
    private final TrainType expected;
    private final TrainType previous;

    public TrainTypeTest(String name, TrainType expected) {
        this.name = name;
        this.expected = expected;
        this.previous = _previous;
        _previous = this.expected;
    }

    @Test
    public void test() {
        assertEquals(this.expected, TrainType.create(this.name));
        assertEquals(this.expected, TrainType.valueOf(this.name));
        assertEquals(this.expected, this.expected.getBase());
        assertEquals(this.name, this.expected.toString());
        if (previous != null) {
            assertTrue(this.previous.compareTo(this.expected) < 0);
        }
    }
}