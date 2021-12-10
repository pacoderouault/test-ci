package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void stringContainsItemFromList() {
        String[] items = new String[]{"test", "test1", "test2"};
        assertTrue(StringUtils.stringContainsItemFromList("test", items));
        assertFalse(StringUtils.stringContainsItemFromList("tEsT", items));
        assertFalse(StringUtils.stringContainsItemFromList("", items));
        assertFalse(StringUtils.stringContainsItemFromList(null, items));
        assertFalse(StringUtils.stringContainsItemFromList("no contain", items));
    }

    @Test
    void capitalizeFirstLetter() {
        assertEquals("Test", StringUtils.capitalizeFirstLetter("test"));
        assertEquals("TEST", StringUtils.capitalizeFirstLetter("TEST"));
        assertNull(StringUtils.capitalizeFirstLetter(null));
        assertEquals("", StringUtils.capitalizeFirstLetter(""));
    }

    @Test
    void containsIgnoreCase() {
        assertTrue(StringUtils.containsIgnoreCase("banana", "BaN"));
        assertTrue(StringUtils.containsIgnoreCase("banana", "A"));
        assertTrue(StringUtils.containsIgnoreCase("banana", "A"));
        assertFalse(StringUtils.containsIgnoreCase("banana", null));
        assertFalse(StringUtils.containsIgnoreCase(null, "test"));
        assertFalse(StringUtils.containsIgnoreCase("", "test"));
        assertTrue(StringUtils.containsIgnoreCase("", ""));
        assertTrue(StringUtils.containsIgnoreCase("test", ""));
        assertFalse(StringUtils.containsIgnoreCase("banana", "test"));
    }
}