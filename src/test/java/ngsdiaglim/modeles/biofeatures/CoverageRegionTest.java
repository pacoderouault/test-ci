package ngsdiaglim.modeles.biofeatures;

import ngsdiaglim.enumerations.CoverageQuality;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoverageRegionTest {

    @Test
    void extendsRegion() {
        CoverageRegion cr1 = new CoverageRegion("chr1", 10, 11, "cr1", CoverageQuality.LOW_COVERAGE);
        cr1.extendsRegion(5);
        assertEquals(12, cr1.getEnd());
    }

    @Test
    void isTouching() {
        CoverageRegion cr1 = new CoverageRegion("chr1", 10, 11, "cr1", CoverageQuality.LOW_COVERAGE);
        assertFalse(cr1.isTouching("chr1", 11));
        assertTrue(cr1.isTouching("chr1", 12));
        assertFalse(cr1.isTouching("chr1", 13));
        assertFalse(cr1.isTouching("chr2", 12));
    }
}