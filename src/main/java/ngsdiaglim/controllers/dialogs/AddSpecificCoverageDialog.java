package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import ngsdiaglim.controllers.cells.specificcoverageCells.SpecificCoverageActionsCell;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AddSpecificCoverageDialog extends DialogPane.Dialog<AddSpecificCoverageDialog.SpecificCoverageCreationData> {

    private static final Logger logger = LogManager.getLogger(AddSpecificCoverageDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TextField coverageSetNameTf;
    @FXML private TextField regionNameTf;
    @FXML private TextField contigTf;
    @FXML private TextField startTf;
    @FXML private TextField endTf;
    @FXML private TextField minCovTf;
    @FXML private TableView<SpecificCoverage> regionTable;
    @FXML private TableColumn<SpecificCoverage, String> nameCol;
    @FXML private TableColumn<SpecificCoverage, String> contigCol;
    @FXML private TableColumn<SpecificCoverage, Integer> startCol;
    @FXML private TableColumn<SpecificCoverage, Integer> endCol;
    @FXML private TableColumn<SpecificCoverage, Integer> minCovCol;
    @FXML private TableColumn<SpecificCoverage, Void> actionsCol;
    @FXML private Label errorLb;

    public AddSpecificCoverageDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AddSpecificCoverageDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("addspecificdialog.title"));
        setContent(dialogContainer);
        setValue(new SpecificCoverageCreationData());

        getValue().nameProperty().bind(coverageSetNameTf.textProperty());
        regionTable.setItems(getValue().getSpecificCoverages());

        getValue().nameProperty().addListener((obs, oldV, newV) -> checkValid());
        getValue().specificCoverages.addListener((ListChangeListener<SpecificCoverage>) c -> checkValid());

        setValid(false);
        initView();
    }

    private void initView() {
        initRegionTable();
    }

    private void initRegionTable() {
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        contigCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContig()));
        startCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStart()).asObject());
        endCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getEnd()).asObject());
        minCovCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMinCov()).asObject());
        actionsCol.setCellFactory(data -> new SpecificCoverageActionsCell());
    }


    @FXML
    private void addRegion() {
        String error = checkAddRegionError();
        if (error != null) {
            Message.error(error);
        } else {
            regionTable.getItems().add(
              new SpecificCoverage(
                regionNameTf.getText(),
                      contigTf.getText(),
                      Integer.parseInt(startTf.getText()),
                      Integer.parseInt(endTf.getText()),
                      Integer.parseInt(minCovTf.getText())
              )
            );
        }
    }


    private String checkAddRegionError() {
        if (StringUtils.isBlank(regionNameTf.getText())) {
            return App.getBundle().getString("addspecificdialog.msg.error.emptyregionname");
        }
        if (StringUtils.isBlank(contigTf.getText())) {
            return App.getBundle().getString("addspecificdialog.msg.error.emptycontig");
        }
        if (StringUtils.isBlank(startTf.getText()) || !NumberUtils.isInt(startTf.getText())) {
            return App.getBundle().getString("addspecificdialog.msg.error.invalidstart");
        }
        if (StringUtils.isBlank(endTf.getText()) || !NumberUtils.isInt(endTf.getText())) {
            return App.getBundle().getString("addspecificdialog.msg.error.invalidend");
        }
        if (StringUtils.isBlank(minCovTf.getText()) || !NumberUtils.isInt(minCovTf.getText())) {
            return App.getBundle().getString("addspecificdialog.msg.error.invalidmincov");
        }
        return null;
    }


    private void checkValid() {
        errorLb.setText(null);
        if (StringUtils.isBlank(getValue().getName())) {
            errorLb.setText(App.getBundle().getString("addspecificdialog.msg.error.emptysetname"));
            setValid(false);
        } else if (getValue().getSpecificCoverages().isEmpty()) {
            errorLb.setText(App.getBundle().getString("addspecificdialog.msg.error.emptyregions"));
            setValid(false);
        } else {
            setValid(true);
        }
    }


    public static class SpecificCoverageCreationData {
        private final SimpleStringProperty name = new SimpleStringProperty();
        private final ObservableList<SpecificCoverage> specificCoverages = FXCollections.observableArrayList();

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public ObservableList<SpecificCoverage> getSpecificCoverages() {return specificCoverages;}
    }
}
