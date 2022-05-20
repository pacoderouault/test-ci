package ngsdiaglim.stats;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MADTest {

    @Test
    void getMAD() {
        List<Double> values1 = new ArrayList<>();
        values1.add(1d);
        values1.add(8d);
        values1.add(-5d);
        values1.add(-6d);
        values1.add(0d);
        assertEquals(5, MAD.getMAD(values1));
    }

    @Test
    void getZScore() {
        assertEquals(-0.5901875, MAD.getZScore(10, 8, 3));
    }

}