package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ngsdiaglim.App;
import ngsdiaglim.enumerations.GnomadPopulation;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.populations.GnomADData;
import ngsdiaglim.modeles.variants.populations.GnomadPopulationFreq;
import ngsdiaglim.utils.BrowserUtils;
import ngsdiaglim.utils.ExternalDatabasesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GnomadDialog extends DialogPane.Dialog<Annotation> {

    private final static Logger logger = LogManager.getLogger(GnomadDialog.class);

    @FXML private VBox dialogContainer;

    @FXML private TextField variantTf;
    @FXML private VBox exomesGridContainer;
    @FXML private VBox genomesGridContainer;

    public GnomadDialog(Annotation a) {
        super(App.get().getAppController().getDialogPane(), DialogPane.Type.INFORMATION);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/GnomadDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }

        setTitle(App.getBundle().getString("gnomaddialog.title"));
        setContent(dialogContainer);
        setValue(a);
        variantTf.setText(a.getVariant().toString());
        exomesGridContainer.getChildren().setAll(buildGridPane(a.getGnomAD().getGnomadExomesData2_1_1()));
        genomesGridContainer.getChildren().setAll(buildGridPane(a.getGnomAD().getGnomadGenomesData2_1_1()));

    }

    private GridPane buildGridPane(GnomADData gnomad) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        Label headerPopName = new Label(App.getBundle().getString("gnomaddialog.lb.popname"));
        Label headerFrequencies = new Label(App.getBundle().getString("gnomaddialog.lb.frequencies"));
        headerPopName.getStyleClass().add("font-medium");
        headerFrequencies.getStyleClass().add("font-medium");
        grid.add(headerPopName, 0, 0);
        grid.add(headerFrequencies, 1, 0);
        buildGridPaneRow(grid, GnomadPopulation.GLOBAL, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.MAX_POP, gnomad);
        Separator separator = new Separator();
        grid.add(separator, 0, grid.getRowCount());
        GridPane.setColumnSpan(separator, grid.getColumnCount());
        buildGridPaneRow(grid, GnomadPopulation.AFR, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.AMR, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.NFE, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.SAS, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.EAS, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.FIN, gnomad);
        buildGridPaneRow(grid, GnomadPopulation.ASJ, gnomad);
        return grid;
    }

    private void buildGridPaneRow(GridPane grid, GnomadPopulation pop, GnomADData gnomad) {
        int row = grid.getRowCount();
        Label popLb = new Label();
        popLb.setText(pop.getPopulation() + " :");
        grid.add(popLb, 0, row);

        GnomadPopulationFreq gnomadPopulationFreq = gnomad.getPopulationFrequency(pop);
        if (gnomadPopulationFreq != null) {
            Label l = new Label(gnomadPopulationFreq.toString());
            l.getStyleClass().add("font-light");
            grid.add(l, 1, row);
        }

    }

    @FXML
    private void openGnomad() {
        String gnomadLink = ExternalDatabasesUtils.getGnomADLink(getValue());
        BrowserUtils.openURL(gnomadLink);
    }
}
