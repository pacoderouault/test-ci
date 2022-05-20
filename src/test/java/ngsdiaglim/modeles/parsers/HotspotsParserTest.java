package ngsdiaglim.modeles.parsers;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.HotspotDAO;
import ngsdiaglim.database.dao.HotspotsSetDAO;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.variants.GenomicVariant;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.modeles.variants.HotspotsSet;
import ngsdiaglim.modeles.variants.Variant;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotspotsParserTest extends BaseSetup {

    private static final HotspotsSetDAO hotspotsSetDAO = DAOController.getHotspotsSetDAO();
    private static final File resourcesDirectory = new File("src/test/resources");
    private static final HotspotDAO hotspotDAO = new HotspotDAO();
    @Test
    void parseHotspotFile() {
        File hotspotsFile = Paths.get(resourcesDirectory.getPath(), "data/hotspots.tsv").toFile();
        try {
            List<Hotspot> hotspots = HotspotsParser.parseHotspotFile(hotspotsFile);
            long id = hotspotsSetDAO.addHotspotsSet("hotspots");
            for (Hotspot hotspot : hotspots) {
                hotspotDAO.addHotspot(id, hotspot);
            }
            HotspotsSet hotspotSet = hotspotsSetDAO.getHotspotsSet(id);
            assertEquals(1742, hotspotSet.getHotspots().size());
            GenomicVariant gv1 = new GenomicVariant("chr7", 140453137, 140453137, "C", "G");
            GenomicVariant gv2 = new GenomicVariant("chr7", 140453145, 140453145, "A", "G");
            GenomicVariant gv3 = new GenomicVariant("chr22", 38509438, 38509438, "G", "T");
            Variant v1 = new Variant(gv1, null);
            Variant v2 = new Variant(gv2, null);
            Variant v3 = new Variant(gv3, null);
            assertNotNull(hotspotSet.getHotspot(Genome.GRCh37, v1));
            assertNull(hotspotSet.getHotspot(Genome.GRCh37, v2));
            assertNotNull(hotspotSet.getHotspot(Genome.GRCh37, v3));
        } catch (IOException | MalformedPanelFile | SQLException e) {
           fail(e.getMessage());
        }

    }
}