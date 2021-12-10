package ngsdiaglim.controllers.analysisview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.VariantTableBuilder;
import ngsdiaglim.controllers.dialogs.Message;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.users.ColumnsExport;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.TableViewUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColumnsVisivilityDropDownMenuContent2 extends VBox {

    private final static Logger logger = LogManager.getLogger(ColumnsVisivilityDropDownMenuContent2.class);

    @FXML private ListView<TableColumn<Annotation, ?>> lv;
    private VariantTableBuilder variantTableBuilder;
    private final TableView<Annotation> previewTable;
    private final ColumnsExport columnsExport;

    public ColumnsVisivilityDropDownMenuContent2(TableView<Annotation> previewTable, ColumnsExport columnsExport) {
        this.previewTable = previewTable;
        this.columnsExport = columnsExport;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ColumnsVisivilityDropDownMenuContent.fxml"), App.getBundle());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
           e.printStackTrace();
        }

    }

    @FXML
    public void initialize() {
        lv.setCellFactory(data -> new VariantsTableColumnsCell());
        fillListView();
    }

    private void fillListView() {
        lv.getItems().setAll(previewTable.getColumns());
    }

    public void setVariantTableBuilder(VariantTableBuilder variantTableBuilder) {
        this.variantTableBuilder = variantTableBuilder;
        fillListView();
    }

    @FXML
    private void selectAllColumns() {
        for (VariantsTableColumns c : VariantsTableColumns.values()) {
            TableColumn<Annotation, ?> col = variantTableBuilder.getColumn(c);
            if (col != null && !col.isVisible()) {
                col.setVisible(true);
            }
        }
    }


    @FXML
    private void unselectAllColumns() {
        for (VariantsTableColumns c : VariantsTableColumns.values()) {
            TableColumn<Annotation, ?> col = variantTableBuilder.getColumn(c);
            if (col != null && col.isVisible()) {
                col.setVisible(false);
            }
        }
    }


    private class VariantsTableColumnsCell extends ListCell<TableColumn<Annotation, ?>> {

        @Override
        protected void updateItem(TableColumn<Annotation, ?> item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
            }
            else {
                CheckBox cb = new CheckBox();
                String colName = TableViewUtils.getColmunTitle(item);
                cb.setText(colName);
                cb.setSelected(item.isVisible());
                cb.selectedProperty().addListener((obs, oldV, newV) -> {
                    VariantsTableColumns variantsTableColumns = VariantsTableColumns.getFromName(colName);
                    if (variantsTableColumns != null) {
                        if (newV) {
                            columnsExport.addColumn(variantsTableColumns);
                        } else {
                            columnsExport.removeColumn(variantsTableColumns);
                        }
                        item.setVisible(newV);
                    }
                });
                setGraphic(cb);
            }
        }
    }
}
