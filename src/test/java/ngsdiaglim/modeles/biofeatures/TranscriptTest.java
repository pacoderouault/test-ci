package ngsdiaglim.modeles.biofeatures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranscriptTest {

    @Test
    void testGetNameWithoutVersion() {
        assertEquals("NM_0001254", Transcript.getNameWithoutVersion("NM_0001254.3"));
    }

    @Test
    void getVersion() {
        Transcript t = new Transcript("NM_0001254.3");
        assertEquals("3", t.getVersion());
    }
}