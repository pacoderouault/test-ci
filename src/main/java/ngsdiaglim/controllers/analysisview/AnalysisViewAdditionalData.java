package ngsdiaglim.controllers.analysisview;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.controllers.cells.AnalysisCommentaryListCell;
import ngsdiaglim.controllers.dialogs.AddAnalysisCommentaryDialog;
import ngsdiaglim.controllers.dialogs.DisplayImageDialog;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.analyse.AdditionalImage;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.analyse.AnalysisCommentary;
import ngsdiaglim.modeles.analyse.ImageImporter;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.Roles.PermissionsEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnalysisViewAdditionalData extends HBox {

    private static final Logger logger = LogManager.getLogger(AnalysisViewAdditionalData.class);

    @FXML private ListView<AnalysisCommentary> commentariesLv;
    @FXML private VBox imagesBox;
    @FXML private FlowPane imagesFp;
    @FXML private Button addComentaryBtn;
    @FXML private Button addImageBtn;
    @FXML private Button pasteImageBtn;
    private final SimpleObjectProperty<Analysis> analysis = new SimpleObjectProperty<>();
    private ImageImporter imageImporter;
    private final KeyCodeCombination pastKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);

    public AnalysisViewAdditionalData() {

        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AnalysisViewAdditionalData.fxml"), App.getBundle());
            fxml.setRoot(this);
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error(e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        commentariesLv.setCellFactory(data -> new AnalysisCommentaryListCell());

        analysis.addListener((obs, oldV, newV) -> {
            updateView();
        });
    }

    public Analysis getAnalysis() {
        return analysis.get();
    }

    public SimpleObjectProperty<Analysis> analysisProperty() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis.set(analysis);
    }

    private void updateView() {
        if (analysis.get() != null) {
            this.imageImporter = new ImageImporter(analysis.get());

            loadAnalysisCommentaries();
            loadAnalysisImages();

            addComentaryBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_ANALYSIS_COMMENT));
            addImageBtn.setDisable(!App.get().getLoggedUser().isPermitted(PermissionsEnum.IMPORT_ANALYSIS_IMAGES));

        }
    }


    @FXML
    private void addCommentaryHandler() {
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.ADD_ANALYSIS_COMMENT)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
        } else {
            AddAnalysisCommentaryDialog addVariantCommentaryDialog = new AddAnalysisCommentaryDialog(App.get().getAppController().getDialogPane());
            Message.showDialog(addVariantCommentaryDialog);
            Button b = addVariantCommentaryDialog.getButton(ButtonType.OK);
            b.setOnAction(e -> {
                if (addVariantCommentaryDialog.isValid() && addVariantCommentaryDialog.getValue() != null) {
                    String comment = addVariantCommentaryDialog.getValue().getCommentary();
                    try {
                        DAOController.getAnalysisCommentaryDAO().addAnalysisCommentary(analysis.get().getId(), comment);
                        loadAnalysisCommentaries();
                        Message.hideDialog(addVariantCommentaryDialog);
                    } catch (SQLException ex) {
                        logger.error(ex);
                        Message.error(ex.getMessage(), ex);
                    }

                }
            });
        }
    }

    @FXML
    private void addImageHandler() {
        if (!App.get().getLoggedUser().isPermitted(PermissionsEnum.IMPORT_ANALYSIS_IMAGES)) {
            Message.error(App.getBundle().getString("app.msg.err.nopermit"));
        } else {
            FileChooser fc = FileChooserUtils.getFileChooser();
            List<File> selectedFiles = fc.showOpenMultipleDialog(App.getPrimaryStage());
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFiles.get(0)));
                user.savePreferences();
                List<String> notImageFiles = new ArrayList<>();
                for (File selectedFile : selectedFiles) {
                    if (ImageUtils.isImage(selectedFile)) {
                        addImage(selectedFile);
                    } else {
                        notImageFiles.add(selectedFile.getName());
                    }
                }
                if (!notImageFiles.isEmpty()) {
                    Message.error(String.join("\n", notImageFiles), "The following files are not images :");
                }
            }
        }
    }

    @FXML
    private void pasteImageHandler() {
        Image image = Clipboard.getSystemClipboard().getImage();
        if (image != null)
        {
            try {
                File importedImage = imageImporter.importImage(image);
                AdditionalImage ai = new AdditionalImage(importedImage);
                imagesFp.getChildren().add(buildImageContainer(ai));
            } catch (IOException e) {
                logger.error(e);
                Message.error(e.getMessage(), e);
            }
//            AdditionalImage ai = new AdditionalImage(image);
//            imagesFp.getChildren().add(buildImageContainer(ai));
        }
    }

    public void loadAnalysisCommentaries() {
        try {
            commentariesLv.setItems(DAOController.getAnalysisCommentaryDAO().getVariantCommentaries(analysis.get().getId()));
        } catch (SQLException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }


    public void loadAnalysisImages() {
        imagesFp.getChildren().clear();
        try {
            List<AdditionalImage> images = imageImporter.loadImages();
            for (AdditionalImage ai : images) {
                imagesFp.getChildren().add(buildImageContainer(ai));
            }
        } catch (IOException e) {
            logger.error(e);
            Message.error(e.getMessage(), e);
        }
    }

    /**
     * Add an image to this node container in the view and the database
     */
    private void addImage(File imageFile) {
        if (imageFile.exists() && imageFile.isFile()) {
            try {
                File importedImage = imageImporter.importImage(imageFile);
                AdditionalImage ai = new AdditionalImage(importedImage);
                imagesFp.getChildren().add(buildImageContainer(ai));
            } catch (IOException e) {
                logger.error(e);
                Message.error(e.getMessage());
            }
        }
    }


    /**
     * Create the node  containing the image
     */
    private HBox buildImageContainer(AdditionalImage ai) {
        int IMAGES_HEIGHT = 180;
        ImageView imgView = new ImageView(ai.getImage());
        imgView.setPreserveRatio(true);
        imgView.setFitHeight(IMAGES_HEIGHT);

        HBox container = new HBox();
        container.setSpacing(2);
        VBox buttonContainer = new VBox();
        buttonContainer.setSpacing(5);
        Button deleteBtn = new Button("", new FontIcon("mdal-delete_forever"));
        deleteBtn.getStyleClass().addAll("icon-button", "icon-button-small");
        Tooltip deleteTp = new Tooltip("Delete image");
        Tooltip.install(deleteBtn, deleteTp);
        deleteBtn.setOnAction(e -> deleteImage(ai));

        Button copyImageBtn = new Button("", new FontIcon("mdal-content_copy"));
        copyImageBtn.getStyleClass().addAll("icon-button", "icon-button-small");
        Tooltip copyTp = new Tooltip("Copy image");
        Tooltip.install(copyImageBtn, copyTp);
        copyImageBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(ai.getImage()); // the image you want, as javafx.scene.image.Image
            clipboard.setContent(content);
        });

        Button downloadImageBtn = new Button("", new FontIcon("mdmz-save_alt"));
        downloadImageBtn.getStyleClass().addAll("icon-button", "icon-button-small");
        Tooltip downloadTp = new Tooltip("Save image");
        Tooltip.install(downloadImageBtn, downloadTp);
        downloadImageBtn.setOnAction(e -> {

            FileChooser fc = FileChooserUtils.getFileChooser();

            fc.setInitialFileName(ai.getImageFile().getName());
            File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
                user.savePreferences();
                ImageUtils.saveImageToFile(ai.getImage(), selectedFile);
            }
        });

        buttonContainer.getChildren().addAll(deleteBtn, copyImageBtn, downloadImageBtn);
        container.getChildren().addAll(imgView, buttonContainer);
        imgView.setOnMouseClicked(e -> {
            DisplayImageDialog dialog = new DisplayImageDialog(App.get().getAppController().getDialogPane());
            dialog.setValue(imgView.getImage());
            Message.showDialog(dialog);
        });
        return container;
    }


    /**
     * Remove an image from the view and the ddb
     */
    public void deleteImage(AdditionalImage ai) {
        DialogPane.Dialog<ButtonType> dialog =  Message.confirm(App.getBundle().getString(""));
        dialog.getButton(ButtonType.YES).setOnAction(event -> {
            try {
                imageImporter.removeImage(ai.getImageFile());
                loadAnalysisImages();
            } catch (IOException e) {
                logger.error(e);
                Platform.runLater(() -> Message.error(e.getMessage(), e));
            }
        });
    }
}
