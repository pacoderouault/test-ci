package ngsdiaglim.modeles;

import ngsdiaglim.modeles.biofeatures.Region;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CytobandsTest {

    @Test
    void getCytobands() {

        Region r = new Region("chr14", 70200000, 73800000, "testRegion1");
        try {
            Region r1 = Cytobands.getCytoBand(new Region("chr14", 70500000, 72000000, "t1"));;
            assertEquals(r, r1);
        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

}