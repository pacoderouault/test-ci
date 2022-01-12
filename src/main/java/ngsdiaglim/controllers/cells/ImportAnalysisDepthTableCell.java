package ngsdiaglim.controllers.cells;

import javafx.stage.FileChooser;
import ngsdiaglim.App;
import ngsdiaglim.modeles.analyse.AnalysisInputData;
import ngsdiaglim.modeles.users.DefaultPreferencesEnum;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.utils.FileChooserUtils;
import ngsdiaglim.utils.FilesUtils;

import java.io.File;

public class ImportAnalysisDepthTableCell extends FileTableCell<AnalysisInputData> implements ImportAnalysisFileTableCell {

    public ImportAnalysisDepthTableCell(boolean modifiable) {
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
            FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("Text files", "*.tsv", "*.txt", "*.tsv.gz", "*.txt.gz");
            FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*");
            fc.getExtensionFilters().addAll(textFilter, allFilter);

            File selectedFile = fc.showOpenDialog(App.getPrimaryStage());
            if (selectedFile != null) {
                User user = App.get().getLoggedUser();
                user.setPreference(DefaultPreferencesEnum.INITIAL_DIR, FilesUtils.getContainerFile(selectedFile));
                user.savePreferences();
                analysisInputData.setDepthFile(selectedFile);
            }
        }
    }

    protected void removeFile() {
        AnalysisInputData analysisInputData = getTableRow().getItem();
        if (analysisInputData != null) {
            analysisInputData.setDepthFile(null);
        }
    }
}
