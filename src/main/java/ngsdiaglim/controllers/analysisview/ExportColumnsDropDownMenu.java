package ngsdiaglim.controllers.analysisview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.controllers.VariantTableBuilder;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.TableExporter;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.users.ColumnsExport;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportColumnsDropDownMenu extends VBox {

    private static final Logger logger = LogManager.getLogger(ExportColumnsDropDownMenu.class);
    @FXML private ListView<VariantsTableColumns> lv;
    private Analysis analysis;
    private TableView<Annotation> tableview;
    private VariantTableBuilder variantTableBuilder;
    private ColumnsExport columnsExport;

    public ExportColumnsDropDownMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ColumnsExportDropDownMenuContent.fxml"), App.getBundle());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        lv.setCellFactory(data -> new ExportColumnsDropDownMenu.VariantsTableColumnsCell());
    }

    private void fillListView() {
        try {
            columnsExport = DAOController.getColumnsExportDAO().getColumnsExport(App.get().getLoggedUser().getId());
            if (columnsExport == null) {
                columnsExport = new ColumnsExport();
                for (VariantsTableColumns col : variantTableBuilder.getDefaultColumnsOrder()) {
                    if (variantTableBuilder.getColumn(col).isVisible()) {
                        columnsExport.addColumn(col);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        lv.getItems().setAll(variantTableBuilder.getDefaultColumnsOrder());
    }

    public void setParameters(Analysis analysis, TableView<Annotation> tableview, VariantTableBuilder variantTableBuilder) {
        this.analysis = analysis;
        this.tableview = tableview;
        this.variantTableBuilder = variantTableBuilder;
        fillListView();
    }

    @FXML
    private void selectAllColumns() {
        for (VariantsTableColumns col : lv.getItems()) {
            columnsExport.addColumn(col);
        }
        lv.refresh();
    }


    @FXML
    private void unselectAllColumns() {
        for (VariantsTableColumns col : lv.getItems()) {
            columnsExport.removeColumn(col);
        }
        lv.refresh();
    }

    @FXML
    private void exportColumnsHandler() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        fc.setInitialFileName(analysis.getName().replaceAll("[/\\.]", "_") + ".xlsx");
        File selectedFile = fc.showSaveDialog(App.getPrimaryStage());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            try {
                DAOController.getColumnsExportDAO().setColumnsExport(App.get().getLoggedUser().getId(), columnsExport);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                Message.error(e.getMessage(), e);
            }

            List<TableColumn<Annotation, ?>> columnsToWrite = new ArrayList<>();
            for (VariantsTableColumns vtc : columnsExport.getColumns()) {
                TableColumn<Annotation, ?> col = variantTableBuilder.getColumn(vtc);
                if (col != null) {
                    columnsToWrite.add(col);
                }
            }
            try {
                TableExporter.exportTableToExcel(analysis, tableview, columnsToWrite, selectedFile);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                Message.error(e.getMessage(), e);
            }
        }
    }



    private class VariantsTableColumnsCell extends ListCell<VariantsTableColumns> {

        @Override
        protected void updateItem(VariantsTableColumns item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
            }
            else {
                if (columnsExport != null) {
                    CheckBox cb = new CheckBox();
                    cb.setText(item.getName());
                    cb.setSelected(columnsExport.hasColumn(item));
                    cb.selectedProperty().addListener((obs, oldV, newV) -> {
                        if (newV) {
                            columnsExport.addColumn(item);
                        } else {
                            columnsExport.removeColumn(item);
                        }
                    });
                    setGraphic(cb);
                } else {
                    setGraphic(null);
                }
            }
        }
    }
}
