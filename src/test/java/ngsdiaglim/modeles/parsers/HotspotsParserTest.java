package ngsdiaglim.modeles.parsers;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.HotspotDAO;
import ngsdiaglim.database.dao.HotspotsSetDAO;
import ngsdiaglim.exceptions.MalformedPanelFile;
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

    private static final HotspotsSetDAO hotspotsSetDAO = DAOController.get().getHotspotsSetDAO();
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
            Variant v1 = new Variant("chr7", 140453137, 140453137, "C", "G");
            Variant v2 = new Variant("chr7", 140453145, 140453145, "A", "G");
            Variant v3 = new Variant("chr22", 38509438, 38509438, "G", "T");
            assertNotNull(hotspotSet.getHotspot(v1));
            assertNull(hotspotSet.getHotspot(v2));
            assertNotNull(hotspotSet.getHotspot(v3));
        } catch (IOException | MalformedPanelFile | SQLException e) {
           fail(e.getMessage());
        }

    }
}