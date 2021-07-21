module NGSDiagLim {
    requires javafx.controls;
    requires javafx.fxml;

    opens ngsdiaglim to javafx.fxml;

    exports ngsdiaglim;
}