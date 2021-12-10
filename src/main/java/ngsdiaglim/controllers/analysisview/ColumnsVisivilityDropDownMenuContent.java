package ngsdiaglim.controllers.analysisview;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.controllers.VariantTableBuilder;
import ngsdiaglim.controllers.VariantTableBuilderOrg;
import ngsdiaglim.enumerations.VariantsTableColumns;
import ngsdiaglim.modeles.variants.Annotation;
import org.controlsfx.control.tableview2.TableColumn2;

import java.io.IOException;

public class ColumnsVisivilityDropDownMenuContent extends VBox {

    @FXML private ListView<VariantsTableColumns> lv;
    private VariantTableBuilder variantTableBuilder;

    public ColumnsVisivilityDropDownMenuContent() {
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
    }

    private void fillListView() {
        lv.getItems().setAll(variantTableBuilder.getDefaultColumnsOrder());
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


    private class VariantsTableColumnsCell extends ListCell<VariantsTableColumns> {

        @Override
        protected void updateItem(VariantsTableColumns item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty || item == null) {
                setGraphic(null);
            }
            else {
                TableColumn<Annotation, ?> col = variantTableBuilder.getColumn(item);
                if (col != null) {
                    CheckBox cb = new CheckBox();
                    cb.setText(item.getName());
                    cb.selectedProperty().bindBidirectional(col.visibleProperty());
                    setGraphic(cb);
                } else {
                    setGraphic(null);
                }
            }
        }
    }
}
