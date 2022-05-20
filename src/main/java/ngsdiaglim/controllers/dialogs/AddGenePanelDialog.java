package ngsdiaglim.controllers.dialogs;

import com.dlsc.gemsfx.DialogPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.comparators.NaturalSortComparator;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GenePanel;
import ngsdiaglim.modeles.parsers.GenesPanelParser;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.ListSelectionViewUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ListSelectionView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AddGenePanelDialog extends DialogPane.Dialog<AddGenePanelDialog.GenePanelCreationData> {

    private static final Logger logger = LogManager.getLogger(AddGenePanelDialog.class);

    @FXML private VBox dialogContainer;
    @FXML private TextField genePanelNameTf;
    @FXML private TextField geneFilterTf;
    @FXML private ListSelectionView<Gene> listSelectionView;
    @FXML private ComboBox<GenePanel> genePanelCb;
    @FXML private Label errorLabel;

    private final static NaturalSortComparator naturalSortComparator = new NaturalSortComparator();

    private Set<Gene> genes;

    public AddGenePanelDialog(DialogPane pane) {
        super(pane, DialogPane.Type.INPUT);
        try {
            // Load main window
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/AddGenePanelDialog.fxml"), App.getBundle());
            dialogContainer = fxml.getRoot();
            fxml.setController(this);
            fxml.load();
        } catch (IOException e) {
            logger.error("Impossible to load the Home page", e);
            Message.error(App.getBundle().getString("app.msg.failloadfxml"), e.getMessage(), e);
        }
        try {
            genes = DAOController.getGeneDAO().getGenes();
        } catch (SQLException e) {
            genes = new HashSet<>();
            logger.error(e);
            Message.error(e.getMessage(), e);
        }

        FilteredList<Gene> filteredGenesList = new FilteredList<>(genes.stream().collect(Collectors.toCollection(FXCollections::observableArrayList)));
        SortedList<Gene> sortedGenesList = new SortedList<>(filteredGenesList);
        sortedGenesList.setComparator(Comparator.comparing(Gene::getGeneName, naturalSortComparator));

        setTitle(App.getBundle().getString("addgenepaneldialog.title"));
        setContent(dialogContainer);
        setValue(new GenePanelCreationData());
        setValid(false);
        initView();

        getValue().nameProperty().addListener((obs) -> validForm());
        getValue().getSelectedGenes().addListener((ListChangeListener<Gene>) c -> validForm());
    }

    private void initView() {
        genePanelNameTf.textProperty().bindBidirectional(getValue().nameProperty());
        listSelectionView.setSourceHeader(new Label(App.getBundle().getString("addgenepaneldialog.lb.genepanelnamelb.availablegenes")));
        listSelectionView.setTargetHeader(new Label(App.getBundle().getString("addgenepaneldialog.lb.genepanelnamelb.selectedgenes")));
        listSelectionView.getSourceItems().setAll(genes);
        listSelectionView.getSourceItems().sort(Comparator.comparing(Gene::getGeneName, naturalSortComparator));
        getValue().setSelectedGenes(listSelectionView.getTargetItems());

        geneFilterTf.textProperty().addListener((obs, oldV, newV) -> filterGenes(newV));
        geneFilterTf.setOnAction(e -> filterGenes(geneFilterTf.getText()));

        ListSelectionViewUtils.rewriteButtons(listSelectionView);

        try {
            genePanelCb.setItems(DAOController.getGenesPanelDAO().getGenesPanels());
        } catch (SQLException e) {
            logger.error(e);
        }
        genePanelCb.getSelectionModel().selectedItemProperty().addListener((obs, oldv, newV) -> {
            if (newV != null) {
                listSelectionView.getTargetItems().setAll(newV.getGenes());
            }
         });
    }


    private void filterGenes(String text) {
        if (text == null || StringUtils.isBlank(text)) {
            listSelectionView.getSourceItems().setAll(genes);
        } else {
            listSelectionView.getSourceItems().setAll(genes.stream().filter(g -> g.getGeneName().contains(text.toUpperCase())).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
        listSelectionView.getSourceItems().sort(Comparator.comparing(Gene::getGeneName, naturalSortComparator));
    }


    private void validForm() {
        String error = checkErrorForm();
        if (error != null) {
            errorLabel.setText(error);
            setValid(false);
        } else {
            errorLabel.setText(null);
            setValid(true);
        }
    }


    private String checkErrorForm() {
        errorLabel.setText(null);
        if (getValue().getName() == null || StringUtils.isBlank(getValue().getName())) {
            return App.getBundle().getString("addgenepaneldialog.msg.err.emptyName");
        }
        else if (getValue().getSelectedGenes().isEmpty()) {
            return App.getBundle().getString("addgenepaneldialog.msg.err.emptyGenes");
        }
        else {
            try {
                long existingPanelId = DAOController.getGenesPanelDAO().genesPanelNameExists(getValue().getName());
                if (existingPanelId > 0 && existingPanelId != getValue().getId()) {
                    return App.getBundle().getString("addgenepaneldialog.msg.err.nameExists");
                }
            } catch (SQLException e) {
                logger.error("Error when checking if panel exists", e);
                return e.getMessage();
            }
        }
        return null;
    }




    public void editGenesPanel(GenePanel genesPanel) {
        getValue().setId(genesPanel.getId());
        getValue().setName(genesPanel.getName());
        listSelectionView.getTargetItems().setAll(genesPanel.getGenes());
    }


    public static class GenePanelCreationData {
        private long id;
        private final SimpleStringProperty name = new SimpleStringProperty();
        private ObservableList<Gene> selectedGenes;

        public long getId() {return id;}

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public ObservableList<Gene> getSelectedGenes() {return selectedGenes;}

        public void setSelectedGenes(ObservableList<Gene> selectedGenes) {
            this.selectedGenes = selectedGenes;
        }
    }

    @FXML
    private void importGenesFromFile() {
        FileChooser fc = FileChooserUtils.getFileChooser();
        File selectedFile = fc.showOpenDialog(getDialogPane().getScene().getWindow());
        if (selectedFile != null) {
            User user = App.get().getLoggedUser();
            user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
            user.savePreferences();
            try {
                Set<Gene> genes = GenesPanelParser.parseGenes(selectedFile);
                listSelectionView.getTargetItems().addAll(genes);
            } catch (IOException e) {
                logger.error(e);
                Message.error(e.getMessage(), e);
            }
        }
    }

}
