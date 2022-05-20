package ngsdiaglim.controllers.cells;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class AnalysisParametersGeneSetDeleteCell extends TableCell<GeneSet, Void> {

    private final Logger logger = LogManager.getLogger(AnalysisParametersGeneSetDeleteCell.class);

    private final HBox box = new HBox();
    private final Button deleteBtn = new Button("", new FontIcon("mdal-delete_forever"));

    public AnalysisParametersGeneSetDeleteCell() {
        box.getStyleClass().add("box-action-cell");
        deleteBtn.getStyleClass().add("button-action-cell");
        box.getChildren().addAll(deleteBtn);
        deleteBtn.setOnAction(e -> deleteHandler());
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        deleteBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
        if(empty) {
            setGraphic(null);
        }
        else {
            setGraphic(box);
        }
    }

    private void deleteHandler() {
        GeneSet geneSet = getTableRow().getItem();

        if (geneSet != null) {
            try {
                if (DAOController.getGeneSetDAO().isUsed(geneSet.getId())) {
                    Message.error(App.getBundle().getString("createAnalasisParameters.module.genes.msg.err.parametersUsed"));
                }
                else {
                    Object[] arguments = {geneSet.getName()};
                    DialogPane.Dialog<ButtonType> dialog = Message.confirm(BundleFormatter.format("createAnalasisParameters.msg.confirm.deleteGeneSet", arguments));
                    dialog.getButton(ButtonType.YES).setOnAction(event -> {
                        try {
                            DAOController.getGeneSetDAO().deleteGeneSet(geneSet.getId());
                            getTableView().getItems().remove(geneSet);
                            Message.hideDialog(dialog);
                        } catch (SQLException e) {
                            logger.error("Error when deleting geneset", e);
                            Message.error(e.getMessage(), e);
                        }
                    });
                }
            } catch (SQLException e) {
                logger.error("Error when deleting geneset", e);
                Message.error(e.getMessage(), e);
            }
        }
    }
}