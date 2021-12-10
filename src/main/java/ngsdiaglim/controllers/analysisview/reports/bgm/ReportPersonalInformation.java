package ngsdiaglim.controllers.analysisview.reports.bgm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddAnalysisCommentaryDialog;
import ngsdiaglim.controllers.dialogs.ManagePrescribersDialogController;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.controllers.ui.AutoCompleteComboboxBuilder;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Gender;
import ngsdiaglim.enumerations.SamplingType;
import ngsdiaglim.modeles.Prescriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportPersonalInformation extends ReportPane {

    private static final Logger logger = LogManager.getLogger(ReportPersonalInformation.class);

    @FXML private ToggleGroup gengerToggleGroup;
    @FXML private ToggleGroup samplingTypeToggleGroup;
    @FXML private RadioButton genderMaleRb;
    @FXML private RadioButton genderFemaleRb;
    @FXML private RadioButton sampleADNRb;
    @FXML private RadioButton sampleBloodRb;
    @FXML private CheckBox childCb;
    @FXML private TextField firstNameTf;
    @FXML private TextField lastNameTf;
    @FXML private TextField maidenNameTf;
    @FXML private TextField patientBarcodeTf;
    @FXML private DatePicker birthdateDp;
    @FXML private DatePicker samplingDateDp;
    @FXML private DatePicker arrivedDateDp;
    @FXML private ComboBox<AutoCompleteComboboxBuilder.HideableItem<Prescriber>> prescribersCb;

    public ReportPersonalInformation(AnalysisViewReportBGMController analysisViewReportBGMController) {
        super(analysisViewReportBGMController);
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/ReportPersonalInformation.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        fillPrescribersCb();
        birthdateDp.setValue(LocalDate.now());
        samplingDateDp.setValue(LocalDate.now());
        arrivedDateDp.setValue(LocalDate.now());
        prescribersCb.getSelectionModel().select(0);
    }


    @FXML
    private void openManagePrescribersDialog() {
        ManagePrescribersDialogController ManagePrescribersDialog = new ManagePrescribersDialogController();
        Message.showDialog(ManagePrescribersDialog);
        Button b = ManagePrescribersDialog.getButton(ButtonType.OK);
        b.setOnAction(e -> {
            Message.hideDialog(ManagePrescribersDialog);
            fillPrescribersCb();
        });
    }

    private void fillPrescribersCb() {
        try {
            AutoCompleteComboboxBuilder.createAutoCompleteCombobox(prescribersCb, DAOController.get().getPrescriberDAO().getPrescribers());
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    @Override
    public String checkForm() {
        if (gengerToggleGroup.getSelectedToggle() == null) {
            return App.getBundle().getString("manageprescriberdialog.msg.err.genderEmpty");
        }
        else if (sampleBloodRb.isSelected() && samplingDateDp.getValue() == null) {
            return App.getBundle().getString("manageprescriberdialog.msg.err.samplingDateEmpty");
        }
        else if (prescribersCb.getValue() == null) {
            return App.getBundle().getString("manageprescriberdialog.msg.err.prescriberEmpty");
        }
        else return null;
    }


    public Gender getGender() {
        if (genderMaleRb.isSelected()) return Gender.MALE;
        else if (genderFemaleRb.isSelected()) return Gender.FEMALE;
        return null;
    }

    public String getFirstName() {
        return firstNameTf.getText();
    }

    public String getLastName() {
        return lastNameTf.getText();
    }

    public String getMaidenNameName() {
        return maidenNameTf.getText();
    }

    public boolean isChild() {
        return childCb.isSelected();
    }

    public String getBarcode() {
        return patientBarcodeTf.getText();
    }

    public LocalDate getBirthdate() {
        return birthdateDp.getValue();
    }

    public Prescriber getPrescriber() {
        if (prescribersCb.getValue() != null) return prescribersCb.getValue().getObject();
        return null;
    }

    public SamplingType getSamplingType() {
        if (sampleADNRb.isSelected()) return SamplingType.DNA;
        else if (sampleBloodRb.isSelected()) return SamplingType.BLOOD;
        else return null;
    }

    public LocalDate getSamplingDate() {
        return samplingDateDp.getValue();
    }

    public LocalDate getSamplingArrivedDate() {
        return arrivedDateDp.getValue();
    }
}
