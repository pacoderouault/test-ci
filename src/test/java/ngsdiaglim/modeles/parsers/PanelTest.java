package ngsdiaglim.modeles.parsers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.PanelDAO;
import ngsdiaglim.database.dao.PanelRegionDAO;
import ngsdiaglim.database.dao.UsersDAO;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.users.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PanelTest  extends BaseSetup {

    private static final PanelDAO panelDAO = DAOController.getPanelDAO();
    private static final PanelRegionDAO panelRegionDAO = DAOController.getPanelRegionDAO();

    @TempDir
    File tempDir;
    private static final File resourcesDirectory = new File("src/test/resources");

    @Test
    void parsePanel() {

        File panelFile = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel.bed").toFile();
        try {
            List<PanelRegion> regions = PanelParser.parsePanel(panelFile);
            assertEquals(2772, regions.size());

            long panelId = panelDAO.addPanel("panelTest2", panelFile.getPath());
            for (PanelRegion region : regions) {
                panelRegionDAO.addRegion(region, panelId);
            }
            ObservableList<PanelRegion> regions3 = panelRegionDAO.getPanelRegions(panelId);
            for (int i = 0; i < regions.size(); i++) {
                assertEquals(regions.get(i), regions3.get(i));
            }

        } catch (IOException | MalformedPanelFile | SQLException e) {
            e.printStackTrace();
            fail();
        }
    }


    @Test
    void writePanel() {
        File panelFile = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel.bed").toFile();
        try {
            List<PanelRegion> regions = PanelParser.parsePanel(panelFile);

            File panelCopy = new File(tempDir, "panelcopy.bed.gz");
            PanelParser.writePanel(regions, panelCopy);
            List<PanelRegion> regions2 = PanelParser.parsePanel(panelCopy);
            assertEquals(2772, regions2.size());
            for (int i = 0; i < regions.size(); i++) {
                assertEquals(regions.get(i), regions2.get(i));
            }

        } catch (IOException | MalformedPanelFile e) {
           fail();
        }
    }


    @Test
    void panelExists() {
        try {
            panelDAO.addPanel("truePanel", "ghost/path/panel.bed.gz");
            assertFalse(panelDAO.panelExists("johndoe"));
            assertTrue(panelDAO.panelExists("truePanel"));
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    void getPanel() {

    }
}