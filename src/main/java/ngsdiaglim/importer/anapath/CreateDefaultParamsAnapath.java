package ngsdiaglim.importer.anapath;

import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.MalformedGeneTranscriptFile;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.SpecificCoverage;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.ciq.CIQHotspot;
import ngsdiaglim.modeles.parsers.GeneSetParser;
import ngsdiaglim.modeles.parsers.HotspotsParser;
import ngsdiaglim.modeles.parsers.PanelParser;
import ngsdiaglim.modeles.variants.Hotspot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class CreateDefaultParamsAnapath {

    public static void createDefaultTumSol() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Anapath/designs/TumorSol");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Anapath/designs/genes.tsv");
        File hotspotsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Anapath/designs/hotspots.tsv");

        long panel_id = addPanel(bedFile, "TumSol");
        long transcripts_id = addTranscripts(transcriptsFile, "TumSol");
        long hotspots_id = addHotspots(hotspotsFile, "TumSol");

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "TumSol",
                Genome.GRCh37,
                100,
                500,
                0.01,
                panel_id,
                transcripts_id,
                null,
                hotspots_id,
                TargetEnrichment.AMPLICON
        );
    }


    public static void createDefaultNF2() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Anapath/designs/NF2");

        long panel_id = addPanel(bedFile, "NF2");
        long transcripts_id = DAOController.getGeneSetDAO().getGeneSet("TumSol").getId();

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "NF2",
                Genome.GRCh37,
                100,
                500,
                0.01,
                panel_id,
                transcripts_id,
                null,
                null,
                TargetEnrichment.AMPLICON
        );
    }


    public static void createDefaultAgilent() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Anapath/designs/agilent");

        long panel_id = addPanel(bedFile, "Agilent");
        long transcripts_id = DAOController.getGeneSetDAO().getGeneSet("TumSol").getId();

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "Agilent",
                Genome.GRCh37,
                100,
                500,
                0.01,
                panel_id,
                transcripts_id,
                null,
                null,
                TargetEnrichment.CAPTURE
        );
    }


    public static void createDefaultAgilentcfDNA() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Anapath/designs/agilent_cfDNA");

        long panel_id = addPanel(bedFile, "agilent_cfDNA");
        long transcripts_id = DAOController.getGeneSetDAO().getGeneSet("TumSol").getId();

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "Agilent_cfDNA",
                Genome.GRCh37,
                1000,
                10000,
                0.001,
                panel_id,
                transcripts_id,
                null,
                null,
                TargetEnrichment.CAPTURE
        );
    }


    private static long addPanel(File bedFile, String name) throws MalformedPanelFile, IOException, SQLException {

        List<PanelRegion> regions = PanelParser.parsePanel(bedFile);
        // save the panel in file
        Path panelDataPath = App.getPanelsDataPath();
        if (!Files.exists(panelDataPath)) {
            Files.createDirectories(panelDataPath);
        }
        File panelFile = Paths.get(panelDataPath.toString(), name + ".bed.gz").toFile();
        PanelParser.writePanel(regions, panelFile);

        long panelId = DAOController.getPanelDAO().addPanel(name, panelFile.getPath());
        for (PanelRegion region : regions) {
            DAOController.getPanelRegionDAO().addRegion(region, panelId);
        }

        return panelId;
    }

    private static long addTranscripts(File transcriptsFile, String name) throws IOException, SQLException, MalformedGeneTranscriptFile {
        HashSet<Gene> genes = GeneSetParser.parseGeneSet(transcriptsFile);
        long geneSetId = DAOController.getGeneSetDAO().addGeneSet(name);
        for (Gene gene : genes) {
            long geneId = DAOController.getGeneDAO().addGene(gene, geneSetId);
            gene.setId(geneId);
            for (Transcript transcript : gene.getTranscripts().values()) {
                long transcriptId = DAOController.getTranscriptsDAO().addTranscript(transcript.getName(), gene.getId());
                transcript.setId(transcriptId);
            }
            // if only one transcript for the gene, set it as "preferred transcript"
            if (gene.getTranscripts().size() == 1) {
                Optional<Transcript> opt = gene.getTranscripts().values().stream().findAny();
                if(opt.isPresent()) {
                    DAOController.getGeneDAO().setPreferredTranscript(gene.getId(), opt.get().getId());
                }
            }
        }
        return geneSetId;
    }

    private static long addHotspots(File hotspotsFile, String name) throws IOException, SQLException, MalformedGeneTranscriptFile, MalformedPanelFile {
        List<Hotspot> hotspots = HotspotsParser.parseHotspotFile(hotspotsFile);
        long hotspotsSetId = DAOController.getHotspotsSetDAO().addHotspotsSet(name);
        for (Hotspot h : hotspots) {
            DAOController.getHotspotDAO().addHotspot(hotspotsSetId, h);
        }
        return hotspotsSetId;
    }


    private static long addSpecificCoverage(List<SpecificCoverage> cov, String name) throws SQLException {
        long specificCovSetId = DAOController.getSpecificCoverageSetDAO().addSpecificCoverageSet(name);
        for (SpecificCoverage r : cov) {
            DAOController.getSpecificCoverageDAO().addSpecificCoverage(specificCovSetId, r, r.getMinCov());
        }
        return specificCovSetId;
    }

    private static long addCIQ(List<CIQHotspot> hotspots, String name, String pattern) throws SQLException {
        long ciqModelId = DAOController.getCiqModelDAO().addCIQModel(name, pattern);
        for (CIQHotspot h : hotspots) {
            DAOController.getCiqHotspotDAO().addCIQHotspot(h, ciqModelId);
        }
        return ciqModelId;
    }
}
