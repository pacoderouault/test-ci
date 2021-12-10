package ngsdiaglim.utils;

import ngsdiaglim.modeles.variants.Variant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariantUtilsTest {

    @Test
    void getHashVariant() {
        Variant v1 = new Variant("chr1", 100, 101, "A", "T");
        Variant v2 = new Variant("chr1", 100, 103, "A", "TCT");
        Variant v3 = new Variant("chr1", 100, 103, "ACG", "A");
        Variant v4 = new Variant("chr1", 100, 103, "ACG", "TGA");
        assertEquals("chr1:100A>T", VariantUtils.getHashVariant(v1));
        assertEquals("chr1:100A>TCT", VariantUtils.getHashVariant(v2));
        assertEquals("chr1:100ACG>A", VariantUtils.getHashVariant(v3));
        assertEquals("chr1:100ACG>TGA", VariantUtils.getHashVariant(v4));
        assertThrows(NullPointerException.class, () -> VariantUtils.getHashVariant(null));
    }
}