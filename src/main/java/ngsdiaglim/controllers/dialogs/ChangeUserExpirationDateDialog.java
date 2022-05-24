package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.BundleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ChangeUserExpirationDateDialog extends DialogPane.Dialog<ChangeUserExpirationDateDialog.ChangeExpirationDateData> {

    private final static Logger logger = LogManager.getLogger(ChangePasswordDialog.class);

    private final GridPane gridPane = new GridPane();
    private final CheckBox setExpirationDateCb = new CheckBox(App.getBundle().getString("adduserdialog.lb.setExpirattionDate"));
    private final DatePicker expirationDatePicker = new DatePicker(LocalDate.now());
    private final Label errorLabel = new Label();
    ChangeListener<LocalDate> changeDateListener = (obs, oldV, newV) -> validDialog();

    public ChangeUserExpirationDateDialog(DialogPane pane, User user) {

        super(pane, DialogPane.Type.INPUT);
        Object[] arguments = {user.getUsername()};
        setTitle(BundleFormatter.format("changeExpirationDatedialog.title", arguments));
        setContent(gridPane);
        setValue(new ChangeExpirationDateData(user));

        initView();

        setExpirationDateCb.setSelected(user.getExpirationDate() != null);
        if (user.getExpirationDate() == null) {
            expirationDatePicker.setValue(LocalDate.now());
        } else {
            expirationDatePicker.setValue(user.getExpirationDate());
        }

        getValue().isInfiniteProperty().bind(setExpirationDateCb.selectedProperty().not());
        getValue().newDateProperty().bind(expirationDatePicker.valueProperty());

        expirationDatePicker.valueProperty().addListener(changeDateListener);
        expirationDatePicker.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            errorLabel.setText(null);
            if (isValidDate()) {
                // force the change of the datepicker value and the form validation
                expirationDatePicker.valueProperty().removeListener(changeDateListener);
                expirationDatePicker.setValue(expirationDatePicker.getConverter().fromString(expirationDatePicker.getEditor().getText()));
                validDialog();
                expirationDatePicker.valueProperty().addListener(changeDateListener);
            } else {
                errorLabel.setText(App.getBundle().getString("adduserdialog.msg.err.expirationDateInvalidFormat"));
                setValid(false);
            }
        });
        setValid(true);
    }

    private void initView() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        errorLabel.getStyleClass().add("error-label");

        int rowIdx = 0;
        gridPane.add(setExpirationDateCb, 0, ++rowIdx);
        gridPane.add(expirationDatePicker, 0, ++rowIdx);
        gridPane.add(errorLabel, 0, ++rowIdx);
        GridPane.setColumnSpan(errorLabel, 2);

        for (int i = 0; i < rowIdx; i++) {
            gridPane.getRowConstraints().add(i, new RowConstraints(30));
        }
    }

    private void validDialog() {
        String error = null;
        try {
            error = checkError();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            Message.error(e.getMessage());
            setValid(false);
        }
        errorLabel.setText(error);
        setValid(error == null);
    }

    private String checkError() throws SQLException {
        if (setExpirationDateCb.isSelected() && expirationDatePicker.getValue() == null) {
            return App.getBundle().getString("adduserdialog.msg.err.setExpirationDate");
        } else if (!setExpirationDateCb.isSelected() && DAOController.getUsersDAO().isLastAdminWithoutExpirationDate(getValue().getUser().getUsername())) {
            return App.getBundle().getString("adduserdialog.msg.err.lastAdminExpiration");
        } else if (setExpirationDateCb.isSelected() && expirationDatePicker.getValue().isBefore(LocalDate.now())) {
            return App.getBundle().getString("adduserdialog.msg.err.expirationDateNotValid");
        } else {
            return null;
        }
    }

    private boolean isValidDate() {
        try {
            expirationDatePicker.getConverter().fromString(expirationDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static class ChangeExpirationDateData {

        private final User user;
        private final SimpleBooleanProperty isInfinite = new SimpleBooleanProperty(false);
        private final SimpleObjectProperty<LocalDate> newDate = new SimpleObjectProperty<>();

        public ChangeExpirationDateData(User user) {
            this.user = user;
        }

        public User getUser() {return user;}

        public boolean isIsInfinite() {
            return isInfinite.get();
        }

        public SimpleBooleanProperty isInfiniteProperty() {
            return isInfinite;
        }

        public void setIsInfinite(boolean isInfinite) {
            this.isInfinite.set(isInfinite);
        }

        public LocalDate getNewDate() {
            return newDate.get();
        }

        public SimpleObjectProperty<LocalDate> newDateProperty() {
            return newDate;
        }

        public void setNewDate(LocalDate newDate) {
            this.newDate.set(newDate);
        }
    }
}
