package ngsdiaglim.controllers.cells;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.util.Tool;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class ImportAnalysisAnalysisParametersTableCell extends TableCell<AnalysisInputData, AnalysisParameters> {

    private final Logger logger = LogManager.getLogger(AnalysisInputData.class);
    private static ObservableList<AnalysisParameters> analysisParametersList;
    private final static Tooltip expendTp = new Tooltip(App.getBundle().getString("importanalysesdialog.tp.expandPanels"));

    public ImportAnalysisAnalysisParametersTableCell() {
        try {
            analysisParametersList = DAOController.getAnalysisParametersDAO().getActiveAnalysisParameters();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        expendTp.setShowDelay(Duration.ZERO);
    }

    @Override
    protected void updateItem(AnalysisParameters item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);

        if (empty) {
            setGraphic(null);
        } else {
            HBox box = new HBox();
            box.getStyleClass().add("box-action-cell");
            box.setSpacing(5);
            box.setAlignment(Pos.CENTER_LEFT);

            ComboBox<AnalysisParameters> analysisParametersComboBox = new ComboBox<>();
            analysisParametersComboBox.setItems(analysisParametersList);
            if (item != null) {
                analysisParametersComboBox.getSelectionModel().select(item);
            }
            analysisParametersComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                getTableRow().getItem().setAnalysisParameters(newV);
            });

            Button expendButton = new Button("", new FontIcon("mdal-height"));
            expendButton.setOnAction(e -> {
                getTableView().getItems().forEach(i -> {
                    i.setAnalysisParameters(analysisParametersComboBox.getValue());
                });
            });
            Tooltip.install(expendButton, expendTp);
            box.getChildren().addAll(analysisParametersComboBox, expendButton);
            setGraphic(box);
        }
    }
}
