package ngsdiaglim.modeles.analyse;

import ngsdiaglim.App;
import ngsdiaglim.controllers.dialogs.AddRunDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.utils.FilesUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;

public class RunCreator {


    private final AddRunDialog.RunCreattionData runCreationData;
    private final Path runsDataPath;
    private Path runPath;

    public RunCreator(AddRunDialog.RunCreattionData runCreationData) {
        this.runCreationData = runCreationData;
       runsDataPath = App.getRunsDataPath();
    }

    public RunCreator(AddRunDialog.RunCreattionData runCreationData, Path runsDataPath) {
        this.runCreationData = runCreationData;
        this.runsDataPath = runsDataPath;
    }

    public long createRun() throws IOException, SQLException {
        LocalDate currentDate = LocalDate.now();
        String currentYear = String.valueOf(currentDate.getYear());

        Path runsYearPath = Paths.get(runsDataPath.toString(), currentYear);
        if (!Files.exists(runsYearPath)) {
            Files.createDirectories(runsYearPath);
        }

        // replace all space characters to underscore for dirname
        String formattedRunDirname = runCreationData.getRunName().replaceAll("\\s+", "_");
        runPath = Paths.get(runsYearPath.toString(), formattedRunDirname);

        if (Files.exists(runPath)) {
            throw new IOException("A run with a same name already exists");
        }

        Files.createDirectories(runPath);

        Path runFilesPath = Paths.get(runPath.toString(), RunConstants.RUN_FILES_DIRNAME);
        if (!Files.exists(runFilesPath)) {
            Files.createDirectories(runFilesPath);
        }

        Path runAnalysesPath = Paths.get(runPath.toString(), RunConstants.RUN_ANALYSES_DIRNAME);
        if (!Files.exists(runAnalysesPath)) {
            Files.createDirectories(runAnalysesPath);
        }

        Path relativeRunPath = FilesUtils.convertAbsolutePathToRelative(runPath);
        return DAOController.getRunsDAO().addRun(
                runCreationData.getRunName(),
                relativeRunPath.toString(),
                runCreationData.getRunDate(),
                currentDate,
                App.get().getLoggedUser().getUsername());
    }



    public void deleteRunDirectory() throws IOException {
//        if (runPath != null) {
            Files.deleteIfExists(runPath);
//        }
    }

}
