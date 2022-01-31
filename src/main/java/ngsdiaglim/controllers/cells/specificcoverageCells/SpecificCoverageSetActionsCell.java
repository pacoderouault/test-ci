package ngsdiaglim.controllers.cells.specificcoverageCells;

import com.dlsc.gemsfx.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.SpecificCoverageSet;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BundleFormatter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class SpecificCoverageSetActionsCell extends TableCell<SpecificCoverageSet, Void> {

    private final HBox box = new HBox();
    private final Button deleteButton = new Button("", new FontIcon(("mdal-delete_forever")));
    public SpecificCoverageSetActionsCell() {
        box.getStyleClass().add("box-action-cell");

        deleteButton.setOnAction(e -> deleteCoverageSet());
        box.getChildren().add(deleteButton);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            User user = App.get().getLoggedUser();
            deleteButton.setDisable(!user.isPermitted(PermissionsEnum.MANAGE_ANALYSISPARAMETERS));
            setGraphic(box);
        }
    }

    private void deleteCoverageSet() {
        SpecificCoverageSet specificCoverageSet = getTableRow().getItem();
        if (specificCoverageSet != null) {

            Object[] messageArguments = {specificCoverageSet.getName()};
            String message = BundleFormatter.format("createAnalasisParameters.module.specificcov.msg.confirm.inactiveCoverageSet", messageArguments);
            DialogPane.Dialog<ButtonType> d =  Message.confirm(message);
            d.getButton(ButtonType.YES).setOnAction(e -> {
                try {
                    boolean isUsed = DAOController.getSpecificCoverageSetDAO().isUsed(specificCoverageSet.getId());
                    if (isUsed) {
                        Message.error(App.getBundle().getString("createAnalasisParameters.module.specificcov.msg.error.deleteCoverageSet"));
                    } else {
                        DAOController.getSpecificCoverageSetDAO().deleteSpecificCoverageSet(specificCoverageSet.getId());
                        getTableView().getItems().remove(specificCoverageSet);
                        getTableView().refresh();
                    }
                    Message.hideDialog(d);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
