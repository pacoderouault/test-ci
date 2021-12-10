package ngsdiaglim.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DNAUtilsTest {

    @Test
    void reverseComplement() {
        String dna = "AaCgGNCT";
        assertEquals("AGNCCGTT", DNAUtils.reverseComplement(dna));
    }

    @Test
    void complement() {
        String dna = "AACgGcNT";
        assertEquals("TTGCCGNA", DNAUtils.complement(dna));
    }

    @Test
    void testComplement() {
        char a = 'A';
        char c = 'c';
        char g = 'g';
        char t = 'T';
        assertEquals('T', DNAUtils.complement(a));
        assertEquals('G', DNAUtils.complement(c));
        assertEquals('C', DNAUtils.complement(g));
        assertEquals('A', DNAUtils.complement(t));
        assertEquals('N', DNAUtils.complement('J'));
    }

    @Test
    void getGCContent() {
        String dna = "AaTTCgGcGaTGaGCCTTtAGC";
        assertEquals(0.5, DNAUtils.getGCContent(dna));
    }
}