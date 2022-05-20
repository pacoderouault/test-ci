package ngsdiaglim.utils;

import ngsdiaglim.modeles.variants.GenomicVariant;
import ngsdiaglim.modeles.variants.Variant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariantUtilsTest {

    @Test
    void getHashVariant() {
        GenomicVariant gv1 = new GenomicVariant("chr1", 100, 101, "A", "T");
        GenomicVariant gv2 = new GenomicVariant("chr1", 100, 103, "A", "TCT");
        GenomicVariant gv3 = new GenomicVariant("chr1", 100, 103, "ACG", "A");
        GenomicVariant gv4 = new GenomicVariant("chr1", 100, 103, "ACG", "TGA");

        assertEquals("chr1:100A>T", VariantUtils.getHashVariant(gv1));
        assertEquals("chr1:100A>TCT", VariantUtils.getHashVariant(gv2));
        assertEquals("chr1:100ACG>A", VariantUtils.getHashVariant(gv3));
        assertEquals("chr1:100ACG>TGA", VariantUtils.getHashVariant(gv4));
        assertThrows(NullPointerException.class, () -> VariantUtils.getHashVariant(null));
    }
}