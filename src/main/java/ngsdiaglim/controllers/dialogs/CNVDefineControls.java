package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.cnv.CNVControlGroup;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.CNVControlType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ListSelectionView;

import java.io.IOException;
import java.sql.SQLException;

public class CNVDefineControls extends DialogPane.Dialog<CNVDefineControls.CNVControlData> {

    private final static Logger logger = LogManager.getLogger(CNVDefineControls.class);

    @FXML private VBox dialogContainer;
    @FXML private VBox controlsPatientContainer;
    @FXML private VBox controlsExternalContainer;
    @FXML private ComboBox<CNVControlType> controlTypeCb;
    @FXML private ListSelectionView<CNVSample> samplesListSelection;
    @FXML private ListView<CNVControlGroup> cnvControlGroupListView;

    private final CovCopCNVData cnvData;

    public CNVDefineControls(DialogPane pane, CovCopCNVData cnvData) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/CNVDefineControls.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        setTitle(App.getBundle().getString("changepassworddialog.title"));
        setContent(dialogContainer);
        this.cnvData = cnvData;
        initView();
        setValue(new CNVControlData());
        getValue().controlType.bind(controlTypeCb.valueProperty());
        getValue().setSamplesControls(samplesListSelection.getTargetItems());
        getValue().groupControl.bind(cnvControlGroupListView.getSelectionModel().selectedItemProperty());
    }

    private void initView() {

        controlTypeCb.getItems().setAll(CNVControlType.values());
        controlTypeCb.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV.equals(CNVControlType.SAMPLES)) {
                controlsPatientContainer.setVisible(true);
                controlsPatientContainer.setManaged(true);
                controlsExternalContainer.setVisible(false);
                controlsExternalContainer.setManaged(false);
            } else if (newV.equals(CNVControlType.EXTERNAL)) {
                controlsPatientContainer.setVisible(false);
                controlsPatientContainer.setManaged(false);
                controlsExternalContainer.setVisible(true);
                controlsExternalContainer.setManaged(true);
            } else {
                controlsPatientContainer.setVisible(false);
                controlsPatientContainer.setManaged(false);
                controlsExternalContainer.setVisible(false);
                controlsExternalContainer.setManaged(false);
            }
        });

        controlTypeCb.getSelectionModel().select(CNVControlType.NONE);

        initSamplesSelectionListView();
        initCNVControlGroupListView();
    }

    private void initSamplesSelectionListView() {
        samplesListSelection.getSourceItems().setAll(cnvData.getSamples().values());
    }

    private void initCNVControlGroupListView() {
        try {
            cnvControlGroupListView.setItems(DAOController.getCnvControlGroupsDAO().getCNVControlGroups(cnvData.getPanel()));
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }



    public static class CNVControlData {
        private final SimpleObjectProperty<CNVControlType> controlType = new SimpleObjectProperty<>();
        private ObservableList<CNVSample> samplesControls;
        private final SimpleObjectProperty<CNVControlGroup> groupControl = new SimpleObjectProperty<>();

        public CNVControlType getControlType() {
            return controlType.get();
        }

        public SimpleObjectProperty<CNVControlType> controlTypeProperty() {
            return controlType;
        }

        public void setControlType(CNVControlType controlType) {
            this.controlType.set(controlType);
        }

        public ObservableList<CNVSample> getSamplesControls() {return samplesControls;}

        public void setSamplesControls(ObservableList<CNVSample> samplesControls) {
            this.samplesControls = samplesControls;
        }

        public CNVControlGroup getGroupControl() {
            return groupControl.get();
        }

        public SimpleObjectProperty<CNVControlGroup> groupControlProperty() {
            return groupControl;
        }

        public void setGroupControl(CNVControlGroup groupControl) {
            this.groupControl.set(groupControl);
        }
    }
}
