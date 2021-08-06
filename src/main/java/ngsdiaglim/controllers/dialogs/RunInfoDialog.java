package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.utils.DateFormatterUtils;

public class RunInfoDialog extends DialogPane.Dialog<Run> {

    private final GridPane gridPane = new GridPane();
    private final Label runNameLb = new Label(App.getBundle().getString("runinfodialog.lb.runName"));
    private final TextField runNameTf = new TextField();
    private final Label runDateLb = new Label(App.getBundle().getString("runinfodialog.lb.runDate"));
    private final TextField runDateTf = new TextField();
    private final Label runCreationDateLb = new Label(App.getBundle().getString("runinfodialog.lb.runCreationDate"));
    private final TextField runCreationDateTf = new TextField();
    private final Label runUserCreationLb = new Label(App.getBundle().getString("runinfodialog.lb.userCreationDate"));
    private final TextField runUserCreationTf = new TextField();

    public RunInfoDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INFORMATION);

        setTitle(App.getBundle().getString("runinfodialog.title"));
        setContent(gridPane);
        initView();

        valueProperty().addListener((obs, oldV, newV) -> {
            fillFields();
        });
    }

    private void initView() {

        runNameTf.setEditable(false);
        runDateTf.setEditable(false);
        runCreationDateTf.setEditable(false);
        runUserCreationTf.setEditable(false);

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        int rowIdx = 0;
        gridPane.add(runNameLb, 0, ++rowIdx);
        gridPane.add(runNameTf, 1, rowIdx);
        gridPane.add(runDateLb, 0, ++rowIdx);
        gridPane.add(runDateTf, 1, rowIdx);
        gridPane.add(runCreationDateLb, 0, ++rowIdx);
        gridPane.add(runCreationDateTf, 1, rowIdx);
        gridPane.add(runUserCreationLb, 0, ++rowIdx);
        gridPane.add(runUserCreationTf, 1, rowIdx);
    }

    private void fillFields() {
        runNameTf.setText(getValue().getName());
        runDateTf.setText(DateFormatterUtils.formatLocalDate(getValue().getDate()));
        runCreationDateTf.setText(DateFormatterUtils.formatLocalDate(getValue().getCreationDate()));
        runUserCreationTf.setText(getValue().getCreationUser());
    }
}
