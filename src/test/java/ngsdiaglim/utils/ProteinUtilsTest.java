package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProteinUtilsTest {

    @Test
    void threeToOne() {
        assertEquals("S", ProteinUtils.ThreeToOne("Ser"));
        assertEquals("C", ProteinUtils.ThreeToOne("Cys"));
        assertEquals("=", ProteinUtils.ThreeToOne("="));
        assertNull(ProteinUtils.ThreeToOne(null));
        assertNull(ProteinUtils.ThreeToOne(""));
        assertNull(ProteinUtils.ThreeToOne("NoProt"));
    }

    @Test
    void oneToThree() {
        assertEquals("Ser", ProteinUtils.OneToThree("S"));
        assertEquals("His", ProteinUtils.OneToThree("H"));
        assertEquals("=", ProteinUtils.OneToThree("="));
        assertNull(ProteinUtils.OneToThree(null));
        assertNull(ProteinUtils.OneToThree(""));
        assertNull(ProteinUtils.OneToThree("NoProt"));
    }

    @Test
    void mutationThreeToOne() {
        assertEquals("S332P", ProteinUtils.mutationThreeToOne("Ser332Pro"));
        assertEquals("S3=", ProteinUtils.mutationThreeToOne("Ser3="));
        assertNull(ProteinUtils.mutationThreeToOne(null));
        assertEquals("badFormat", ProteinUtils.mutationThreeToOne("badFormat"));
    }

    @Test
    void mutationOneToThress() {
        assertEquals("Ser332Pro", ProteinUtils.mutationOneToThree("S332P"));
        assertEquals("Ser3=", ProteinUtils.mutationOneToThree("S3="));
        assertNull(ProteinUtils.mutationOneToThree(null));
        assertEquals("badformat", ProteinUtils.mutationOneToThree("badformat"));
    }

    @Test
    void formatAAChange() {
        assertEquals("p.Ser442Pro", ProteinUtils.formatAAChange("p.ser442Pro"));
        assertEquals("p.Ser442=", ProteinUtils.formatAAChange("p.ser442="));
        assertNull(ProteinUtils.formatAAChange(null));
        assertEquals("badformat", ProteinUtils.formatAAChange("badformat"));
        assertEquals("", ProteinUtils.formatAAChange(""));
    }
}