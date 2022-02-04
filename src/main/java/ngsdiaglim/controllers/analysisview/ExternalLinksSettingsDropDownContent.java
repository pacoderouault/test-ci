package ngsdiaglim.controllers.analysisview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.enumerations.ExternalLinksEnum;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.HashSet;
import java.util.StringJoiner;

public class ExternalLinksSettingsDropDownContent extends PopOver {

    private static final Logger logger = LogManager.getLogger(ExternalLinksSettingsDropDownContent.class);

    @FXML private VBox box;
    @FXML private ListView<ExternalLinksEnum> lv;
    @FXML private HashSet<ExternalLinksEnum> selectedExternalLinks = new HashSet<>();

    public ExternalLinksSettingsDropDownContent() {
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ExternalLinksSettingsDropDownContent.fxml"), App.getBundle());
            box = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setContentNode(box);
    }

    @FXML
    public void initialize() {
        lv.setCellFactory(data -> new ExternalLinksSettingsDropDownContent.ExternalLinksCell());
        lv.getItems().setAll(ExternalLinksEnum.values());

        for (String s : App.get().getLoggedUser().getPreferences().getPreference(DefaultPreferencesEnum.VISIBLE_EXTERNAL_LINKS).split(",")) {
            try {
                selectedExternalLinks.add(ExternalLinksEnum.valueOf(s));
            } catch (Exception ignored) {}
        }
        getStyleClass().add("dropdown-menu");
    }


    @FXML
    private void valid() {
        StringJoiner sj = new StringJoiner(",");
        for (ExternalLinksEnum e : selectedExternalLinks) {
            sj.add(e.name());
        }
        App.get().getLoggedUser().setPreference(DefaultPreferencesEnum.VISIBLE_EXTERNAL_LINKS, sj.toString());
        App.get().getLoggedUser().savePreferences();
        ModuleManager.getAnalysisViewController().getVariantsViewController().getVariantDetailController().setExternalLinksButtonsVisibility();
        hide();
    }


    private class ExternalLinksCell extends ListCell<ExternalLinksEnum> {

        private final CheckBox checkBox = new CheckBox();

        public ExternalLinksCell() {
            checkBox.setOnAction(e -> editExternalLinksVisibility());
        }

        @Override
        protected void updateItem(ExternalLinksEnum item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
            }
            else {
                checkBox.setText(item.getLinkName());
                checkBox.setUserData(item);
                checkBox.setSelected(selectedExternalLinks.contains(item));
                setGraphic(checkBox);
            }
        }



        private void editExternalLinksVisibility() {
            Object item = checkBox.getUserData();
            if (item instanceof ExternalLinksEnum) {
                ExternalLinksEnum externalLink = (ExternalLinksEnum) item;
                if (checkBox.isSelected()) {
                    selectedExternalLinks.add(externalLink);
                } else {
                    selectedExternalLinks.remove(externalLink);
                }
            }
        }
    }

}
