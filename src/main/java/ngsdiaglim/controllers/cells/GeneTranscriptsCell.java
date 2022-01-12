package ngsdiaglim.controllers.cells;

import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;

public class GeneTranscriptsCell extends TableCell<Gene, HashMap<String, Transcript>> {

    private final Logger logger = LogManager.getLogger(GeneTranscriptsCell.class);
    private final HBox fp = new HBox();

    public GeneTranscriptsCell() {
        fp.setSpacing(5);
    }

    @Override
    protected void updateItem(HashMap<String, Transcript> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null || item.isEmpty()) {
            setText(null);
            setGraphic(null);
        }
        else {
            fp.getChildren().clear();
            Gene gene = getTableRow().getItem();

            if (gene != null) {
                ToggleGroup group = new ToggleGroup();
                group.setUserData(gene);
                item.forEach((k, transcript) -> {
                    ToggleButton tb = new ToggleButton(transcript.getNameWithoutVersion());
                    tb.setToggleGroup(group);
                    tb.setUserData(transcript);
                    tb.setMinWidth(USE_COMPUTED_SIZE);
                    if (gene.getTranscriptPreferred() != null && gene.getTranscriptPreferred().equals(transcript)) {
                        tb.setSelected(true);
                    }
                    fp.getChildren().add(tb);
                });
                group.selectedToggleProperty().addListener((obs, oldV, newV) -> {

                    Gene g = (Gene) group.getUserData();
                    if (newV == null) {
                        try {
                            DAOController.getGeneDAO().setPreferredTranscript(g.getId(), -1);
                        } catch (SQLException e) {
                            logger.error(e);
                            Message.error(e.getMessage(), e);
                        }
                    }
                    else {
                        Transcript t = (Transcript) newV.getUserData();
                        try {
                            DAOController.getGeneDAO().setPreferredTranscript(g.getId(), t.getId());
                        } catch (SQLException e) {
                            logger.error(e);
                            Message.error(e.getMessage(), e);
                        }
                    }


                });
                setGraphic(fp);
            }
            else {
                setText(null);
                setGraphic(null);
            }
        }
    }

}
