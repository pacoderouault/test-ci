package ngsdiaglim.controllers.cells;

import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;

import java.io.File;

public class ImportAnalysisVCFTableCell extends FileTableCell<AnalysisInputData> implements ImportAnalysisFileTableCell {

    public ImportAnalysisVCFTableCell() {
        this(false);
    }

    public ImportAnalysisVCFTableCell(boolean modifiable) {
        super(modifiable);
        setOnMouseClicked(e -> {
            if (e.getClickCount() > 1) {
                openFile();
            }
        });
    }

    protected void openFile() {
        AnalysisInputData analysisInputData = getTableRow().getItem();
        if (analysisInputData != null) {
            FileChooser fc = FileChooserUtils.getFileChooser();
            FileChooser.ExtensionFilter vcfFilter = new FileChooser.ExtensionFilter("VCF files", "*.vcf", "*.vcf.gz");
            FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
            fc.getExtensionFilters().addAll(vcfFilter, allFilter);

            File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
                user.savePreferences();
                analysisInputData.setVcfFile(selectedFile);
            }
        }
    }

    protected void removeFile() {
        AnalysisInputData analysisInputData = getTableRow().getItem();
        if (analysisInputData != null) {
            analysisInputData.setVcfFile(null);
        }
    }
}
