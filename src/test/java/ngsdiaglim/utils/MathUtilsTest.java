package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilsTest {

    @Test
    void meanOfInt() {
        List<Integer> intList = new ArrayList<>();
        assertEquals(0, MathUtils.meanOfInt(intList));
        intList.add(0);
        intList.add(-5);
        intList.add(null);
        intList.add(14);
        assertEquals(3, MathUtils.meanOfInt(intList));
    }

    @Test
    void median() {
        List<Double> doubleList = new ArrayList<>();
        assertNull(MathUtils.median(doubleList));
        doubleList.add(-5d);
        doubleList.add(0d);
        doubleList.add(null);
        doubleList.add(10d);
        doubleList.add(2d);
        assertEquals(1, MathUtils.median(doubleList));
        doubleList.add(10d);
        assertEquals(2, MathUtils.median(doubleList));
    }

    @Test
    void meanOfDouble() {
        List<Double> doubleList = new ArrayList<>();
        assertEquals(0, MathUtils.meanOfDouble(doubleList));
        doubleList.add(-5d);
        doubleList.add(0d);
        doubleList.add(null);
        doubleList.add(11d);
        doubleList.add(2d);
        assertEquals(2, MathUtils.meanOfDouble(doubleList));
    }

    @Test
    void findDeviation() {
        List<Double> doubleList = new ArrayList<>();
        assertNull(MathUtils.findDeviation(doubleList));
        doubleList.add(1.23);
        doubleList.add(1.45);
        doubleList.add(2.1);
        doubleList.add(null);
        doubleList.add(2.2);
        doubleList.add(1.9);
        assertEquals(0.41967844833872525, Objects.requireNonNull(MathUtils.findDeviation(doubleList)).getSecond());
    }
}