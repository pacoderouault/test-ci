package ngsdiaglim.controllers.cells;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import ngsdiaglim.App;
import ngsdiaglim.modeles.users.Roles.Role;

import java.io.IOException;

public class RoleListCell extends ListCell<Role> {

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Role item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/fxml/RoleListCell.fxml"), App.getBundle());
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
