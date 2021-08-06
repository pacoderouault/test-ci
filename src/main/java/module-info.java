module NGSDiagLim {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires com.dlsc.gemsfx;
    requires com.h2database;
    requires org.controlsfx.controls;
    requires htsjdk;
    requires org.jfxtras.styles.jmetro;
    requires fr.brouillard.oss.cssfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;

    exports ngsdiaglim.modeles.users;
    exports ngsdiaglim.modeles.users.Roles;
    exports ngsdiaglim.controllers;
    exports ngsdiaglim.database;
    exports ngsdiaglim.skins;
    exports ngsdiaglim;
    exports ngsdiaglim.modules;

    opens ngsdiaglim to javafx.fxml;
    opens ngsdiaglim.controllers to javafx.fxml;
    opens ngsdiaglim.skins to javafx.fxml;
    opens ngsdiaglim.modules to javafx.fxml;
    opens ngsdiaglim.controllers.dialogs to javafx.fxml;
}