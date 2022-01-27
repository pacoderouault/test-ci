package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.ciq.AddCIQHotspotActionsCell;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class AddCIQDialog extends DialogPane.Dialog<AddCIQDialog.CIQModelData> {

    private static final Logger logger = LogManager.getLogger(AddCIQDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TextField ciqNameTf;
    @FXML private TextField ciqBarcodeTf;
    @FXML private TextField hotspotNameTf;
    @FXML private TextField contigTf;
    @FXML private TextField positionTf;
    @FXML private TextField refTf;
    @FXML private TextField altTf;
    @FXML private TextField vafTargetTf;
    @FXML private Label errorLb;
    @FXML private Label errorLbCIQ;
    @FXML private TableView<CIQHotspot> hotspotTable;
    @FXML private TableColumn<CIQHotspot, String> nameCol;
    @FXML private TableColumn<CIQHotspot, String> contigCol;
    @FXML private TableColumn<CIQHotspot, Integer> positionCol;
    @FXML private TableColumn<CIQHotspot, String> refCol;
    @FXML private TableColumn<CIQHotspot, String> altCol;
    @FXML private TableColumn<CIQHotspot, Float> vafTargetCol;
    @FXML private TableColumn<CIQHotspot, Void> actionsCol;

    public AddCIQDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);

        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AddCIQDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setContent(dialogContainer);
        setValid(false);
        setValue(new CIQModelData());
        getValue().nameProperty().bind(ciqNameTf.textProperty());
        getValue().barcodeProperty().bind(ciqBarcodeTf.textProperty());
        getValue().setHotspots(hotspotTable.getItems());
        initHotspotTable();


        // event nom ciq et table items -> valid ou non
        getValue().nameProperty().addListener((obs, oldV, newV) -> CIQModelValidation());
        getValue().barcodeProperty().addListener((obs, oldV, newV) -> CIQModelValidation());
        getValue().getHotspots().addListener((ListChangeListener<CIQHotspot>) c -> CIQModelValidation());
    }

    private void initHotspotTable() {
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        contigCol.setCellValueFactory(data -> data.getValue().contigProperty());
        positionCol.setCellValueFactory(data -> data.getValue().positionProperty().asObject());
        refCol.setCellValueFactory(data -> data.getValue().refProperty());
        altCol.setCellValueFactory(data -> data.getValue().altProperty());
        vafTargetCol.setCellValueFactory(data -> data.getValue().vafTargetProperty().asObject());
        actionsCol.setCellFactory(data -> new AddCIQHotspotActionsCell());
    }

    @FXML
    private void addHotspotHandler() {
        // check hotspots
        errorLb.setText(null);
        String error = HotspotValidation();
        if (error != null) {
            errorLb.setText(error);
        } else {
            CIQHotspot h = new CIQHotspot(
                    hotspotNameTf.getText(),
                    contigTf.getText(),
                    Integer.parseInt(positionTf.getText()),
                    refTf.getText(),
                    altTf.getText(),
                    Float.parseFloat(vafTargetTf.getText())
            );
            hotspotTable.getItems().add(h);
            clearAddHotspotForm();
        }
    }


    private void clearAddHotspotForm() {
        hotspotNameTf.setText(null);
        contigTf.setText(null);
        positionTf.setText(null);
        refTf.setText(null);
        altTf.setText(null);
        vafTargetTf.setText(null);
    }

    private String HotspotValidation() {
        if (StringUtils.isBlank(hotspotNameTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyName");
        }
        if (StringUtils.isBlank(contigTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyContig");
        }
        if (StringUtils.isBlank(positionTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyPosition");
        }
        if (!NumberUtils.isInt(positionTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.positionNotValid");
        }
        if (StringUtils.isBlank(vafTargetTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyVafTarget");
        }
        if (!NumberUtils.isFloat(vafTargetTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.VafTargetNotValid");
        }
        if (StringUtils.isBlank(refTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyRef");
        }
        if (StringUtils.isBlank(altTf.getText())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyAlt");
        }
        return null;
    }


    private String getCIQError() throws SQLException {
        if (StringUtils.isBlank(getValue().getName())) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyCIQName");
        }
        if (getValue().getHotspots().isEmpty()) {
            return App.getBundle().getString("addCIQDialog.msg.err.emptyHotspot");
        }
        if (DAOController.getCiqModelDAO().CIQModelExists(getValue().getName())) {
            return App.getBundle().getString("addCIQDialog.msg.err.CIQNameExists");
        }
        if (!StringUtils.isBlank(getValue().getBarcode()) && DAOController.getCiqModelDAO().CIQModelExists(getValue().getName())) {
            return App.getBundle().getString("addCIQDialog.msg.err.CIQBarcodeExists");
        }
        return null;
    }


    private void CIQModelValidation() {
        errorLbCIQ.setText(null);
        try {
            String error = getCIQError();
            if (error != null) {
                errorLbCIQ.setText(error);
                setValid(false);
            } else {
                setValid(true);
            }
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
            setValid(false);
        }
    }


    public class CIQModelData {
        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleStringProperty barcode = new SimpleStringProperty();
        private ObservableList<CIQHotspot> hotspots;

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getBarcode() {
            return barcode.get();
        }

        public SimpleStringProperty barcodeProperty() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode.set(barcode);
        }

        public ObservableList<CIQHotspot> getHotspots() {return hotspots;}

        public void setHotspots(ObservableList<CIQHotspot> hotspots) {
            this.hotspots = hotspots;
        }


    }
}
