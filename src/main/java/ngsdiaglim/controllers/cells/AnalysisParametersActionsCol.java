package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AnalysisInfoDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class AnalysisParametersActionsCol extends TableCell<AnalysisParameters, Void> {

    private final Logger logger = LogManager.getLogger(ngsdiaglim.controllers.cells.RunActionTableCell.class);

    private final HBox box = new HBox();
    private final Button deleteAnalysisParametersBtn = new Button("", new FontIcon("mdal-delete_forever"));

    public AnalysisParametersActionsCol() {
        box.getStyleClass().add("box-action-cell");
        deleteAnalysisParametersBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(deleteAnalysisParametersBtn);
        deleteAnalysisParametersBtn.setOnAction(e -> deleteAnalysisParametersHandler());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        deleteAnalysisParametersBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
        if(empty) {
            setGraphic(null);
        }
        else {
            setGraphic(box);
        }
    }

    private void deleteAnalysisParametersHandler() {
        AnalysisParameters analysisParameters = getTableRow().getItem();

        if (analysisParameters != null) {
            try {
                if (DAOController.get().getAnalysisParametersDAO().isUsed(analysisParameters.getId())) {
                    Message.error(App.getBundle().getString("createAnalasisParameters.msg.err.parametersUsed"));
                }
                else {
                    Object[] arguments = {analysisParameters.getAnalysisName()};
                    DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("createAnalasisParameters.msg.confirm.deleteAnalysisParameters", arguments));
                    dialog.getButton(ButtonType.YES).setOnAction(event -> {
                        try {
                            DAOController.get().getAnalysisParametersDAO().deleteAnalysisParameters(analysisParameters.getId());
                            getTableView().getItems().remove(analysisParameters);
                            Message.hideDialog(dialog);
                        } catch (SQLException e) {
                            logger.error("Error when deleting analysis parameters", e);
                            Message.error(e.getMessage(), e);
                        }
                    });
                }
            } catch (SQLException e) {
                logger.error("Error when deleting analysis parameters", e);
                Message.error(e.getMessage(), e);
            }
        }
    }
}
