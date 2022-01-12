package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberUtilsTest {

    @Test
    void isInt() {
        assertTrue(NumberUtils.isInt("0"));
        assertTrue(NumberUtils.isInt("-0"));
        assertTrue(NumberUtils.isInt("5"));
        assertTrue(NumberUtils.isInt("-5"));
        assertFalse(NumberUtils.isInt(null));
        assertFalse(NumberUtils.isInt("1.2"));
        assertFalse(NumberUtils.isInt("-0.2"));
        assertFalse(NumberUtils.isInt("f2"));
        assertFalse(NumberUtils.isInt("2f"));
        assertFalse(NumberUtils.isInt("2d"));
    }

    @Test
    void isDouble() {
        assertTrue(NumberUtils.isDouble("0.0"));
        assertTrue(NumberUtils.isDouble("-0.0"));
        assertTrue(NumberUtils.isDouble("5"));
        assertTrue(NumberUtils.isDouble("-5"));
        assertFalse(NumberUtils.isDouble(null));
        assertTrue(NumberUtils.isDouble("1.2"));
        assertFalse(NumberUtils.isDouble("f2"));
        assertTrue(NumberUtils.isDouble("2f"));
        assertTrue(NumberUtils.isDouble("2d"));
    }

    @Test
    void isFloat() {
        assertTrue(NumberUtils.isFloat("0.0"));
        assertTrue(NumberUtils.isFloat("-0.0"));
        assertTrue(NumberUtils.isFloat("5"));
        assertTrue(NumberUtils.isFloat("-5"));
        assertFalse(NumberUtils.isFloat(null));
        assertTrue(NumberUtils.isFloat("1.2"));
        assertFalse(NumberUtils.isFloat("f2"));
        assertTrue(NumberUtils.isFloat("2f"));
        assertTrue(NumberUtils.isFloat("2d"));
    }

    @Test
    void isNumeric() {
        assertTrue(NumberUtils.isFloat("0.0"));
        assertTrue(NumberUtils.isFloat("-0.0"));
        assertTrue(NumberUtils.isFloat("5"));
        assertTrue(NumberUtils.isFloat("-5"));
        assertFalse(NumberUtils.isFloat(null));
        assertTrue(NumberUtils.isFloat("1.2"));
        assertFalse(NumberUtils.isFloat("f2"));
        assertTrue(NumberUtils.isFloat("2f"));
        assertTrue(NumberUtils.isFloat("2d"));
    }

    @Test
    void round() {
        assertEquals(1.25, NumberUtils.round(1.25, 2));
        assertEquals(1.25, NumberUtils.round(1.248695, 2));
        assertEquals(-3, NumberUtils.round(-3.0125, 1));
        assertThrows(NullPointerException.class, () -> NumberUtils.round(null, 1));
    }
}