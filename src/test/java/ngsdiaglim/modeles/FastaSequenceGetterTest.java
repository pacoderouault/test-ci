package ngsdiaglim.modeles;

import ngsdiaglim.AppSettings;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FastaSequenceGetterTest {

    @Test
    void getSequence() {
        try {
            FastaSequenceGetter fastaSequenceGetter = new FastaSequenceGetter(new File(AppSettings.DefaultAppSettings.REFERENCE_GRCH37.getValue()));
            assertEquals("AGACAAAGGA", fastaSequenceGetter.getSequence("chr17", 53456780, 53456789));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
}