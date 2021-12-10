package ngsdiaglim.importer;

import htsjdk.samtools.util.IOUtil;
import htsjdk.tribble.Tribble;
import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.exceptions.DuplicateAnalysisInRun;
import ngsdiaglim.exceptions.NotBiallelicVariant;
import ngsdiaglim.modeles.analyse.*;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.users.User;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.utils.FilesUtils;
import ngsdiaglim.utils.ImageUtils;
import ngsdiaglim.utils.VCFUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportsRuns {

    private final File runsDir;
    private final File localRunDir;
    private final Path runsDataPath = App.getRunsDataPath();
    private final static String GZIP_EXTENSION = ".gz";
    private final static String TABIX_INDEX_EXTENSION = ".tbi";

    public ImportsRuns(File runsDir, File localRunDir) {
        this.runsDir = runsDir;
        this.localRunDir = localRunDir;
    }

    public void importRuns() throws IOException, SQLException, DuplicateAnalysisInRun, NotBiallelicVariant {
        for (File d : Files.list(runsDir.toPath()).map(Path::toFile).filter(File::isDirectory).collect(Collectors.toList())) {
            File runParams = new File(d, "runParams.txt");
            String runName = null;
            LocalDate runDate = null;
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(runParams));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] tks = line.split("\t");
                    if (tks[0].equals("name")) {
                        runName = tks[1];
                    } else if (tks[0].equals("date")) {
                        runDate = LocalDate.parse(tks[1]);
                    }
                }
            }
            reader.close();

            if (runName == null || runDate == null) {
                throw new IOException(d.getPath());
            }
            long run_id = createRun(runName, runDate);
            Run run = DAOController.get().getRunsDAO().getRun(run_id);
            // import analyses
            for (File analysisDir : Files.list(d.toPath()).map(Path::toFile).filter(File::isDirectory).collect(Collectors.toList())) {
                importAnalyse(run, analysisDir);
            }
        }

    }


    public long createRun(String runName, LocalDate runDate) throws IOException, SQLException {
        LocalDate currentDate = LocalDate.now();
        String currentYear = String.valueOf(runDate.getYear());

        Path runsYearPath = Paths.get(runsDataPath.toString(), currentYear);
        if (!Files.exists(runsYearPath)) {
            Files.createDirectories(runsYearPath);
        }

        Path runPath = Paths.get(runsYearPath.toString(), runName);
        if (!Files.exists(runPath)) {
            Files.createDirectories(runPath);
        }

        Path runFilesPath = Paths.get(runPath.toString(), RunConstants.RUN_FILES_DIRNAME);
        if (!Files.exists(runFilesPath)) {
            Files.createDirectories(runFilesPath);
        }

        Path runAnalysesPath = Paths.get(runPath.toString(), RunConstants.RUN_ANALYSES_DIRNAME);
        if (!Files.exists(runAnalysesPath)) {
            Files.createDirectories(runAnalysesPath);
        }

        Path relativeRunPath = FilesUtils.convertAbsolutePathToRelative(runPath);
        return DAOController.get().getRunsDAO().addRun(
                runName,
                relativeRunPath.toString(),
                runDate,
                currentDate,
                "admin");
    }

    private void importAnalyse(Run run, File analysisDir) throws IOException, SQLException, DuplicateAnalysisInRun, NotBiallelicVariant {
        File params = new File(analysisDir, "params.txt");

        String analysisName = null;
        LocalDate analysisDate = null;
        String sampleName = null;
        String design = null;
        String metadata = null;

        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(params));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                String[] tks = line.split("\t");
                switch (tks[0]) {
                    case "name":
                        analysisName = tks[1];
                        break;
                    case "date":
                        analysisDate = LocalDate.parse(tks[1]);
                        break;
                    case "samplename":
                        sampleName = tks[1];
                        break;
                    case "design":
                        design = tks[1];
                        break;
                    case "params":
                        metadata = tks[1];
                        break;
                }
            }
        }
        reader.close();

        if (analysisName == null || analysisDate == null || sampleName == null || design == null || metadata == null) {
            throw new IOException(analysisDir.getPath());
        }

        AnalysisParameters analysisParameters = null;
        if (design.equals("CMT_Calcium_MiSeq")) {
            analysisParameters = DAOController.get().getAnalysisParametersDAO().getAnalysisParameters("NeuroCalcium_capture");



        } else if (design.equals("CMT126_proton")) {
            analysisParameters = DAOController.get().getAnalysisParametersDAO().getAnalysisParameters("NeuroCalcium_amplicon");
        }

        if (analysisParameters == null) {
            throw new NullPointerException("Design not found : " + design);
        }




        Optional<File> vcfFile = Files.list(analysisDir.toPath()).map(Path::toFile).filter(f -> f.getName().equals("variants.vcf")).findAny();
        if (vcfFile.isEmpty()) {
            throw new NullPointerException("No VCF file found : " + analysisDir.getPath());
        }

        // Create analysis Dir
        File targetDirectory = Paths.get(run.getPath(), RunConstants.RUN_ANALYSES_DIRNAME).toFile();
        File analysisDirectory = Paths.get(targetDirectory.toString(), analysisName).toFile();
        if (analysisDirectory.exists()) {
            analysisDirectory = Objects.requireNonNull(renameFile(targetDirectory, analysisName));
//            throw new DuplicateAnalysisInRun("Duplicate analysis name in the run (" + analysisName + ")");
        }
        Files.createDirectories(analysisDirectory.toPath());


        File targetVCFFile = Paths.get(analysisDirectory.toString(), vcfFile.get().getName().replace(GZIP_EXTENSION, "") + GZIP_EXTENSION).toFile();
        File targetIndexFile = Paths.get(analysisDirectory.toString(), targetVCFFile.getName().replace(TABIX_INDEX_EXTENSION, "") + TABIX_INDEX_EXTENSION).toFile();

        // Import VCF
        // check if vcf is block-compressed
        boolean isBGzip = IOUtil.isBlockCompressed(vcfFile.get().toPath(), false);
        if (isBGzip) {
            // copy vcf file
            FileUtils.copyFile(vcfFile.get(), targetVCFFile);
            if (!FilesUtils.compareFiles(vcfFile.get(), targetVCFFile)) {
                throw new IOException("Invalid copy of " + vcfFile.get().getName());
            }

            // check for tabix index file
            File indexFile = Tribble.tabixIndexFile(vcfFile.get());
            if (indexFile.exists()) {

                FileUtils.copyFile(indexFile, targetIndexFile);
                if (!FilesUtils.compareFiles(vcfFile.get(), targetVCFFile)) {
                    throw new IOException("Invalid copy of " + vcfFile.get().getName());
                }
            } else {
                // create tabix index
                VCFUtils.createTabixIndex(targetVCFFile, targetIndexFile);
            }
        } else {
            // block compress vcf and index it
            VCFUtils.bgZipFile(vcfFile.get(), targetVCFFile);
            VCFUtils.createTabixIndex(targetVCFFile, targetIndexFile);
        }

        // Import Coverage Bed
        Optional<File> covFile = Files.list(analysisDir.toPath()).map(Path::toFile).filter(f -> f.getName().equals("coverage.bed")).findAny();
        if (covFile.isPresent()) {
            File targetCoverageFile = Paths.get(analysisDirectory.toString(), RunConstants.ANALYSIS_COVERAGE_FILENAME).toFile();
            FileUtils.copyFile(covFile.get(), targetCoverageFile);
//            VCFUtils.bgZipFile(covFile.get(), targetCoverageFile);
        }

        // Import Depth File
        File depthFile = null;
        if (design.equals("CMT_Calcium_MiSeq")) {
            File localRunDir = getLocalRunDir(run.getName());
            depthFile = searchFile(localRunDir, sampleName);
            File targetDepthFile = Paths.get(analysisDirectory.toString(), Objects.requireNonNull(depthFile).getName() + ".gz").toFile();
            VCFUtils.bgZipFile(depthFile, targetDepthFile);
        }

        // Insert Analysis in database
        long analysis_id = DAOController.get().getAnalysisDAO().addAnalyse(
                analysisName,
                analysisDirectory.getPath(),
                targetVCFFile,
                null,
                depthFile,
                covFile.isEmpty() ? null : covFile.get(),
                LocalDateTime.now(),
                sampleName,
                run,
                analysisParameters,
                metadata
        );

        Analysis analysis = DAOController.get().getAnalysisDAO().getAnalysis(run, analysis_id);
        if (analysis == null) {
            throw new NullPointerException("Analysis is null : " + analysisDirectory.getPath());
        }

        // read vcf
        VCFParser vcfParser = new VCFParser(targetVCFFile, analysisParameters, run);
        vcfParser.parseVCF(true);

        for (Annotation annotation : vcfParser.getAnnotations()) {
            DAOController.get().getVariantAnalysisDAO().insertVariantAnalysis(annotation.getVariant().getId(), analysis_id);

            Object[] analysisIds = new Object[run.getAnalyses().size()];
            for (int i = 0; i < run.getAnalyses().size(); i++) {
                analysisIds[i] = (int) run.getAnalyses().get(i).getId();
            }
            annotation.getVariant().setOccurrenceInRun(DAOController.get().getVariantAnalysisDAO().countRunOccurrence(annotation.getVariant().getId(), analysisIds));
        }

        importImages(analysis, analysisDir);
        importComments(analysis, analysisDir);
    }

    private void importImages(Analysis analysis, File analysisSourceDir) throws IOException {
        ImageImporter imageImporter = new ImageImporter(analysis);
        Files.list(Paths.get(analysisSourceDir.getPath(), "images")).map(Path::toFile).filter(ImageUtils::isImage).forEach(imageFile -> {
            try {
                imageImporter.importImage(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void importComments(Analysis analysis, File analysisSourceDir) throws IOException, SQLException {
        for (File f : Files.list(Paths.get(analysisSourceDir.getPath(), "comments")).map(Path::toFile).collect(Collectors.toList())) {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line;
            int lineIdx = 0;

            String userName = null;
            LocalDateTime date = null;
            StringBuilder comment = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (lineIdx == 0) {
                        userName = line.trim();
                    } else if (lineIdx == 1) {
                        date = LocalDate.parse(line.trim()).atStartOfDay();
                    } else {
                        comment.append(line);
                    }
                    lineIdx++;
                }
            }
            reader.close();

            if (userName == null || date == null || comment.toString().isBlank()) {
                throw new NullPointerException("Empty file : " + f);
            }

            User user = DAOController.get().getUsersDAO().getUser(userName);
            if (user == null) {
                throw new NullPointerException("Unknow user : " + userName);
            }

            DAOController.get().getAnalysisCommentaryDAO().addAnalysisCommentary(analysis.getId(), comment.toString(), date, user);
        }
    }

    private File renameFile(File targetDirectory, String filename) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String newFilename = i + "_" + filename;
            File newFile = new File(targetDirectory, newFilename);
            if (!newFile.exists()) {
                return newFile;
            }
        }
        return null;
    }

    public File getLocalRunDir(String runName) throws IOException {
        Optional<File> r = Files.list(localRunDir.toPath()).map(Path::toFile).filter(f -> f.isDirectory() && f.getName().equals(runName)).findAny();
        return r.orElse(null);
    }

    public File getDepthFile(File runDir, String runName, String sampleName) throws IOException {
//        Files.walk()
        Optional<File> depthFile = Files.walk(runDir.toPath()).map(Path::toFile).filter(f -> {


                    String filename = f.getName();
//                    System.out.println(filename);
                    return filename.endsWith("depth_.tsv");


            }).findAny();
        return depthFile.orElse(null);
    }

    static File searchFile(File file, String sampleName) {
        if (file.isDirectory()) {
            File[] arr = file.listFiles();
            for (File f : arr) {
                File found = searchFile(f, sampleName);
                if (found != null)
                    return found;
            }
        } else {
            if (file.getName().endsWith("_depth.tsv") && file.getName().contains(sampleName)) {
                return file;
            }
        }
        return null;
    }
}
