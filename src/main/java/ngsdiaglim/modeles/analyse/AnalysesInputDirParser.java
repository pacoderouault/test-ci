package ngsdiaglim.modeles.analyse;

import ngsdiaglim.utils.BamUtils;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class AnalysesInputDirParser {

    private final File inputDir;
    private final Run run;

    public AnalysesInputDirParser(Run run, File inputDir) {
        this.inputDir = inputDir;
        this.run = run;
    }

    public HashMap<String, AnalysisInputData> parseInputDir() throws IOException {
        return findInputFiles();
    }

    private HashMap<String, AnalysisInputData>  findInputFiles() throws IOException {

        final HashMap<String, AnalysisInputData> sampleFiles = new HashMap<>();

        List<File> vcfFiles = new ArrayList<>();
        List<File> bamFiles = new ArrayList<>();
        List<File> depthFiles = new ArrayList<>();
        Files.find(inputDir.toPath(), 10, (path, basicFileAttributes) -> path.toFile().isFile()).forEach(p -> {
            if (p.toString().toLowerCase().endsWith(".vcf") || p.toString().toLowerCase().endsWith(".vcf.gz")) {
                vcfFiles.add(p.toFile());
            }
            else if (p.toString().toLowerCase().endsWith(".bam")) {
                bamFiles.add(p.toFile());
            }
            else if (p.toString().toLowerCase().contains("_depth")) {
                depthFiles.add(p.toFile());
            }
        });

        for (File vcfFile : vcfFiles) {
            String sampleName = VCFUtils.getSamplesName(vcfFile).get(0);
            if (sampleName != null && !StringUtils.isBlank(sampleName)) {
                String analysisName = vcfFile.getName().replaceAll("\\.vcf|\\.gz", "");
                AnalysisInputData analysisInputData = new AnalysisInputData(run, analysisName, sampleName);
                analysisInputData.setVcfFile(vcfFile);
                analysisInputData.setBamFile(getBamFile(sampleName, bamFiles));
                analysisInputData.setDepthFile(getDepthFile(sampleName, depthFiles));
                sampleFiles.put(sampleName, analysisInputData);
            }
        }

        return sampleFiles;
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
}
