package ngsdiaglim.modeles.analyse;

import htsjdk.samtools.util.IOUtil;
import htsjdk.tribble.Tribble;
import javafx.collections.ObservableList;
import ngsdiaglim.App;
import ngsdiaglim.controllers.WorkIndicatorDialog;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.exceptions.DuplicateAnalysisInRun;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.modeles.parsers.BamParser;
import ngsdiaglim.modeles.parsers.SamtoolsDepthParser;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.BundleFormatter;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RunImporter {

    private static final Logger logger = LogManager.getLogger(RunImporter.class);
    private final Run run;
    private final List<RunFile> runsFiles;
    private final ObservableList<AnalysisInputData> analysisInputData;
    private final static String GZIP_EXTENSION = ".gz";
    private final static String TABIX_INDEX_EXTENSION = ".tbi";
    private WorkIndicatorDialog<String> wid = null;

    public RunImporter(Run run, List<RunFile> runsFiles, ObservableList<AnalysisInputData> analysisInputData) {
        this.run = run;
        this.runsFiles = runsFiles;
        this.analysisInputData = analysisInputData;
    }
    public RunImporter(Run run, List<RunFile> runsFiles, ObservableList<AnalysisInputData> analysisInputData, WorkIndicatorDialog<String> wid) {
        this(run, runsFiles, analysisInputData);
        this.wid = wid;
        if (this.wid != null) {
            this.wid.maxProgress = analysisInputData.size() + 1;
        }
    }

    public void importRun() throws Exception {
        importRunFiles();
        importAnalyses();

    }

    private void importRunFiles() throws IOException, SQLException {
        updateWid(App.getBundle().getString("importanalysesdialog.msg.importingRunFiles"), 0);
        File targetDirectory = Paths.get(run.getPath(), RunConstants.RUN_FILES_DIRNAME).toFile();
        for (RunFile runFile : runsFiles) {
            File destFile = new File(targetDirectory, runFile.getFile().getName());
            if (!destFile.exists()) {
                FileUtils.copyFile(runFile.getFile(), destFile);
                if (!FilesUtils.compareFiles(runFile.getFile(), destFile)) {
                    throw new IOException("Invalid copy file : " + runFile.getFile().getName());
                }
                if (!DAOController.getRunFilesDAO().runFileExists(run.getId(), destFile)) {
                    DAOController.getRunFilesDAO().addRunFile(run.getId(), destFile);
                }
            }
        }
    }

    private void importAnalyses() throws Exception {

        File targetDirectory = Paths.get(run.getPath(), RunConstants.RUN_ANALYSES_DIRNAME).toFile();
        for (AnalysisInputData analysisInputData : analysisInputData) {
            String analysisName = analysisInputData.getAnalysisName();

            Object[] messageArguments = {analysisName};
            String message = BundleFormatter.format("importanalysesdialog.msg.importingAnalysis", messageArguments);
            updateWid(message, 1);

            String sampleName = analysisInputData.getSampleName();
            Run run = analysisInputData.getRun();
            File vcfFile = analysisInputData.getVcfFile();
            File bamFile = analysisInputData.getBamFile();
            File depthFile = analysisInputData.getDepthFile();
            File targetDepthFile = null;
            AnalysisParameters analysisParameters = analysisInputData.getAnalysisParameters();
            CIQModel ciqModel = analysisInputData.getCiqModel();

            // create analysis directory in the analysis dir of the run
            // Replace spaces with underscores
            String analysisDirName = analysisName.replaceAll("\\s+", "_");
            File analysisDirectory = Paths.get(targetDirectory.toString(), analysisDirName).toFile();
            if (analysisDirectory.exists()) {
                throw new DuplicateAnalysisInRun("Duplicate analysis name in the run (" + analysisName + ")");
            }
            Files.createDirectories(analysisDirectory.toPath());

            try {
                if (vcfFile != null && vcfFile.exists()) {

                    File targetVCFFile = Paths.get(analysisDirectory.toString(), vcfFile.getName().replace(GZIP_EXTENSION, "") + GZIP_EXTENSION).toFile();
                    File targetIndexFile = Paths.get(analysisDirectory.toString(), targetVCFFile.getName().replace(TABIX_INDEX_EXTENSION, "") + TABIX_INDEX_EXTENSION).toFile();

                    // check if vcf is block-compressed
                    boolean isBGzip = IOUtil.isBlockCompressed(vcfFile.toPath(), false);
                    if (isBGzip) {
                        // copy vcf file
                        FileUtils.copyFile(vcfFile, targetVCFFile);
                        if (!FilesUtils.compareFiles(vcfFile, targetVCFFile)) {
                            throw new IOException("Invalid copy of " + vcfFile.getName());
                        }

                        // check for tabix index file
                        File indexFile = Tribble.tabixIndexFile(vcfFile);
                        if (indexFile.exists()) {

                            FileUtils.copyFile(indexFile, targetIndexFile);
                            if (!FilesUtils.compareFiles(vcfFile, targetVCFFile)) {
                                throw new IOException("Invalid copy of " + vcfFile.getName());
                            }
                        } else {
                            // create tabix index
                            VCFUtils.createTabixIndex(targetVCFFile, targetIndexFile);
                        }
                    } else {
                        // block compress vcf and index it
                        VCFUtils.bgZipFile(vcfFile, targetVCFFile);
                        VCFUtils.createTabixIndex(targetVCFFile, targetIndexFile);
                    }

                    File targetCoverageFile = Paths.get(analysisDirectory.toString(), RunConstants.ANALYSIS_COVERAGE_FILENAME).toFile();
                    File targetSpecCoverageFile = Paths.get(analysisDirectory.toString(), RunConstants.ANALYSIS_SPECIFIC_COVERAGE_FILENAME).toFile();

                    if (depthFile != null && depthFile.exists()) {
                        targetDepthFile = Paths.get(analysisDirectory.toString(), depthFile.getName().replace(GZIP_EXTENSION, "") + GZIP_EXTENSION).toFile();
                        // check if vcf is block-compressed
                        isBGzip = IOUtil.isBlockCompressed(depthFile.toPath(), false);
                        if (isBGzip) {
                            // copy vcf file
                            FileUtils.copyFile(depthFile, targetDepthFile);
                            if (!FilesUtils.compareFiles(depthFile, depthFile)) {
                                throw new IOException("Invalid copy of " + depthFile.getName());
                            }
                        }
                        else {
                            VCFUtils.bgZipFile(depthFile, targetDepthFile);
                        }

                        if (targetDepthFile.exists()) {
                            SamtoolsDepthParser samtoolsDepthParser = new SamtoolsDepthParser(analysisParameters, targetDepthFile);
                            samtoolsDepthParser.parseFile(targetCoverageFile, targetSpecCoverageFile);

                        }
                    } else if (bamFile != null && bamFile.exists()){
                        BamParser bamParser = new BamParser(analysisParameters, bamFile);
                        bamParser.parseFile(targetCoverageFile, targetSpecCoverageFile);
                    }

                    String metadata = App.getAppName() + ":" + App.getVersion();
                    long analysis_id = DAOController.getAnalysisDAO().addAnalyse(
                            analysisName,
                            analysisDirectory.getPath(),
                            targetVCFFile,
                            bamFile,
                            targetDepthFile,
                            targetCoverageFile,
                            targetSpecCoverageFile,
                            LocalDateTime.now(),
                            sampleName,
                            run,
                            analysisParameters,
                            metadata
                    );

                    // read vcf
                    VCFParser vcfParser = new VCFParser(targetVCFFile, analysisParameters, run);
                    vcfParser.parseVCF(true);

                    // set analysis as CIQ
                    if (ciqModel != null) {
                        DAOController.getCiqAnalysisDAO().addAnalysisCIQ(analysis_id, ciqModel.getId());
                    }

                    for (Annotation annotation : vcfParser.getAnnotations()) {
                        if (!DAOController.getVariantAnalysisDAO().hasVariant(analysis_id, annotation.getVariant().getId())) {
                            DAOController.getVariantAnalysisDAO().insertVariantAnalysis(annotation.getVariant().getId(), analysis_id);
                        }
                        Object[] analysisIds = new Object[run.getAnalyses().size()];
                        for (int i = 0; i < run.getAnalyses().size(); i++) {
                            analysisIds[i] = (int) run.getAnalyses().get(i).getId();
                        }
                        annotation.getVariant().setOccurrenceInRun(DAOController.getVariantAnalysisDAO().countRunOccurrence(annotation.getVariant().getId(), analysisIds));

                        // check for CIQ hotspots variants
                        if (ciqModel != null) {
                            CIQHotspot hotspot = ciqModel.getHotspot(annotation);
                            if (hotspot != null) {
                                DAOController.getCiqRecordDAO().addCIQRecord(
                                        ciqModel,
                                        hotspot,
                                        analysis_id,
                                        annotation.getDepth(),
                                        annotation.getAlleleDepth(),
                                        annotation.getVaf());
                            }
                        }
                    }

                    DAOController.getAnalysisDAO().updateAnalysisImportState(analysis_id, true);
                }

                // force reloading analyses of the run
                run.loadAnalysesFromDB();
            } catch (Exception e) {

                e.printStackTrace();
                FileUtils.deleteDirectory(analysisDirectory);
                throw new Exception(e);
            }
        }
    }

    private void updateWid(String message, float increment) {
        if (wid != null) {
            wid.updateMessage = message;
            float cp = wid.currentProgress + increment;
            wid.currentProgress = cp;
        }
    }
}
