module NGSDiagLim {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires workbenchfx.core;
    requires com.h2database;

    opens ngsdiaglim to javafx.fxml;

    exports ngsdiaglim;
}