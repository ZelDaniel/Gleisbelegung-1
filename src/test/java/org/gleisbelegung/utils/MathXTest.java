package org.gleisbelegung.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathXTest
{
    @Test
    public void testIsPrime()
    {
        assertTrue(MathX.isPrime(2));
        assertTrue(MathX.isPrime(3));
        assertTrue(MathX.isPrime(5));
        assertFalse(MathX.isPrime(4));
    }
}
