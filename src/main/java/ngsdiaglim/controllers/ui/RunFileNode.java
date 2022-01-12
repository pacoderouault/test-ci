package ngsdiaglim.controllers.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.RunFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class RunFileNode extends Label {

    private final Logger logger = LogManager.getLogger(RunFileNode.class);
    private final RunFile runFile;

    public RunFileNode(RunFile runFile) {
        super();
        this.runFile = runFile;
        initNode();
    }

    private void initNode() {
        setText(runFile.getFile().getName());
        HBox box = new HBox();
        Button showFile = new Button("", new FontIcon("mdmz-preview"));
        showFile.setOnAction(e -> showFileHandler());
        Button deleteFile = new Button("", new FontIcon("mdal-delete_forever"));
        deleteFile.setOnAction(e -> deleteFileHandler());
        box.getChildren().addAll(showFile, deleteFile);
        setGraphic(box);
        setContentDisplay(ContentDisplay.RIGHT);
        getStyleClass().add("runfile-node");
    }

    private void showFileHandler() {
        displayFile();
    }

    private void displayFile() {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().open(runFile.getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void deleteFileHandler() {
//        DialogPane.Dialog<ButtonType> dialog = Message.confirm(App.getBundle().getString("importanalysesdialog.msg.conf.deleteRunFile"));
//        dialog.getButton(ButtonType.YES).setOnAction(event -> {
            try {
                DAOController.getRunFilesDAO().removeRunFile(runFile.getId());
                if (getParent() instanceof Pane) {
                    Pane pane = (Pane) getParent();
                    pane.getChildren().remove(this);
                }

//                Message.hideDialog(dialog);
            } catch (SQLException e) {
                logger.error(e);
                Message.error(e.getMessage(), e);
            }
//        });
    }

    public RunFile getRunFile() {return runFile;}
}
