package ngsdiaglim.modeles.analyse;

import javafx.collections.ObservableList;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.exceptions.DuplicateSampleInRun;
import ngsdiaglim.modeles.ciq.CIQModel;
import ngsdiaglim.utils.BamUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysesInputDirParser {

    private final File inputDir;
    private final Run run;
    private final List<RunFile> runFiles = new ArrayList<>();
    private final  HashMap<String, AnalysisInputData> analysesFiles = new HashMap<>();
    private final Set<String> runFilesExtensions = new HashSet<>(){{
        add("pdf");
        add("html");
        add("xls");
        add("xlsx");
        add("txt");
        add("csv");
        add("tsv");
    }};

    public AnalysesInputDirParser(Run run, File inputDir) {
        this.inputDir = inputDir;
        this.run = run;
    }

    public List<RunFile> getRunFiles() {return runFiles;}

    public HashMap<String, AnalysisInputData> getAnalysesFiles() {return analysesFiles;}

    public void parseInputDir() throws IOException, DuplicateSampleInRun, SQLException {
        findInputFiles();
    }

    private void findInputFiles() throws IOException, DuplicateSampleInRun, SQLException {

        List<File> vcfFiles = new ArrayList<>();
        List<File> bamFiles = new ArrayList<>();
        List<File> depthFiles = new ArrayList<>();
        runFiles.clear();
        analysesFiles.clear();
        Path inputPath = inputDir.toPath();
        Files.find(inputPath, 10, (path, basicFileAttributes) -> path.toFile().isFile()).forEach(p -> {
            if (p.toString().toLowerCase().endsWith(".vcf") || p.toString().toLowerCase().endsWith(".vcf.gz")) {
                vcfFiles.add(p.toFile());
            }
            else if (p.toString().toLowerCase().endsWith(".bam")) {
                bamFiles.add(p.toFile());
            }
            else if (p.toString().toLowerCase().contains("_depth")) {
                depthFiles.add(p.toFile());
            }
            else if (p.getParent().equals(inputPath) && isValidRunFile(p)) { // check for run files in the root
                runFiles.add(new RunFile(p.toFile(), run));
            }
        });

        for (File vcfFile : vcfFiles) {
            String sampleName = VCFUtils.getSamplesName(vcfFile).get(0);
            if (sampleName != null && !StringUtils.isBlank(sampleName)) {
                if (analysesFiles.containsKey(sampleName)) {
                    throw new DuplicateSampleInRun("Duplicate sample : " + sampleName + " in the run");
                }
                String analysisName = vcfFile.getName().replaceAll("\\.vcf|\\.gz", "");
                AnalysisInputData analysisInputData = new AnalysisInputData(run, analysisName, sampleName);
                analysisInputData.setVcfFile(vcfFile);
                analysisInputData.setBamFile(getBamFile(sampleName, bamFiles));
                analysisInputData.setDepthFile(getDepthFile(sampleName, depthFiles));
                Optional<CIQModel> ciq = getCIQModel(sampleName);
                ciq.ifPresent(analysisInputData::setCiqModel);

                analysesFiles.put(sampleName, analysisInputData);
            }
        }
    }


    private File getBamFile(String sampleName, List<File> bamFiles) {
        for (File bamFile : bamFiles) {
            String bamSampleName = BamUtils.getBamSampleName(bamFile);
            if (bamSampleName != null && bamSampleName.equals(sampleName)) {
                return bamFile;
            }
        }
        return null;
    }

    private File getDepthFile(String sampleName, List<File> depthFiles) {
        for (File depthFile : depthFiles) {
            String filename = depthFile.getName().replaceAll("\\.gz|\\.tsv|\\.txt", "");
            if (filename.equalsIgnoreCase(sampleName + "_depth")) {
                return depthFile;
            }
        }
        return null;
    }


    /**
     * Check if the samplename correspond to a CIQ, based on regex pattern
     */
    private Optional<CIQModel> getCIQModel(String sampleName) throws SQLException {
        Optional<CIQModel> optionalCiq = Optional.empty();
        ObservableList<CIQModel> ciqModels = DAOController.getCiqModelDAO().getActiveCIQModels();
        for (CIQModel ciq : ciqModels) {
            String pattern = ciq.getBarcode();
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(sampleName);
            if(m.find()) {
                optionalCiq = Optional.of(ciq);
                break;
            }
        }
        return optionalCiq;
    }

    private boolean isValidRunFile(Path path) {
        return runFilesExtensions.contains(FilenameUtils.getExtension(path.getFileName().toString()));
    }
}
