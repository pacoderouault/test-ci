package ngsdiaglim.controllers.cells.variantsTableCells;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.controllers.cells.TranscriptConsequenceListViewCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.users.UserPreferences;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.TranscriptConsequence;
import ngsdiaglim.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.util.Comparator;

public class TranscriptTableCell extends TableCell<Annotation, Transcript> {

    private final Logger logger = LogManager.getLogger(TranscriptTableCell.class);
    private final Button btn = new Button();
    private final FontIcon icon = new FontIcon("mdmz-menu");
    private final TranscriptPopOver popover = new TranscriptPopOver();
    private final static String warningTranscriptClass = "transcript-table-cell-warning";

    public TranscriptTableCell() {
        btn.getStyleClass().add("hidden-button");
        btn.setGraphic(icon);
        btn.setOnMouseClicked(e -> {
            popover.show(icon, -10);
        });
        popover.selectedTranscriptProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                if (popover.setForAllVariants()) {
                    ModuleManager.getAnalysisViewController().getVariantsViewController().setVisibleTranscript(newV.getTranscript());
                }
                else {
                    Annotation annotation = getTableRow().getItem();
                    if (annotation != null) {
                        annotation.setTranscriptConsequence(newV);
                        getTableView().refresh();
                    }
                }
                User loggedUser = App.get().getLoggedUser();
                loggedUser.setPreference(DefaultPreferencesEnum.SELECT_TRANSCRIPT_FOR_ALL_VARIANTS, popover.setForAllVariants());
                try {
                    DAOController.get().getUsersDAO().updatePreferences(loggedUser);
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        });
    }

    @Override
    protected void updateItem(Transcript item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            getStyleClass().remove(warningTranscriptClass);
            if (getTableRow() != null) {
                Annotation annotation = getTableRow().getItem();
                if (annotation != null) {
                    initPopOver(annotation);
                    setGraphic(btn);
                    setText(item.getName());
                    if (!item.isPreferred()) {
                        getStyleClass().add(warningTranscriptClass);
                    }
                    else {
                        setText(item.getName());
                    }
                } else {
                    setText(null);
                    setGraphic(null);
                }
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    private void initPopOver(Annotation annotation) {
        popover.clear();
        for (String transcript : annotation.getTranscriptConsequences().keySet()) {
            popover.addTranscript(annotation.getTranscriptConsequences().get(transcript));
        }
    }

    public static class TranscriptPopOver extends PopOver {

        private final CheckBox setForAllVariants = new CheckBox(App.getBundle().getString("analysisview.variant.transcriptcell.forAllGenes"));
        private final ListView<TranscriptConsequence> lv = new ListView<>();

        public TranscriptPopOver() {
            setForAllVariants.setSelected(
                    Boolean.parseBoolean(
                        App.get().getLoggedUser().getPreferences().getProperty(
                                DefaultPreferencesEnum.SELECT_TRANSCRIPT_FOR_ALL_VARIANTS.name(), "False")
                    )
            );
            lv.setCellFactory(f -> new TranscriptConsequenceListViewCell());
            VBox box = new VBox();
            box.getChildren().addAll(setForAllVariants, new Separator(Orientation.HORIZONTAL), lv);
            box.setSpacing(5);
            box.setPadding(new Insets(10));
            this.setContentNode(box);
        }

        public void addTranscript(TranscriptConsequence t) {
            lv.getItems().add(t);
            lv.getItems().sort(Comparator.comparing(e -> {
                StringBuilder toCompare = new StringBuilder();
                if (e.getTranscript().getGene() != null) {
                    toCompare.append(e.getTranscript().getGene().getGeneName());
                } else if (e.getGeneName() != null) {
                    toCompare.append(e.getGeneName());
                }
                toCompare.append(":").append(e.getTranscript().getName());
                return toCompare.toString();
            }));
        }

        public void clear() {lv.getItems().clear();}

        public ReadOnlyObjectProperty<TranscriptConsequence> selectedTranscriptProperty() { return lv.getSelectionModel().selectedItemProperty();}

        public boolean setForAllVariants() {
            return setForAllVariants.isSelected();
        }
    }
}
