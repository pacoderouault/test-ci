package ngsdiaglim.importer.hemato;

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

public class CreateDefaultParamsHemato {

    public static void createDefaultLympho() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/DesignLympho/designLympho.bed");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/genes.tsv");

        long panel_id = addPanel(bedFile, "Lymphoide_IonTorrent");
        long transcripts_id = addTranscripts(transcriptsFile, "Lymphoide_IonTorrent");

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "Lymphoide_IonTorrent",
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

    public static void createDefaultMyelo() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/DesignMyelo/DesignMyelo.bed");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/genes.tsv");

        long panel_id = addPanel(bedFile, "Myeloide_IonTorrent");
        long transcripts_id = addTranscripts(transcriptsFile, "Myeloide_IonTorrent");

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "Myeloide_IonTorrent",
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

    public static void createDefaultTP53ATM() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/DesignTP53ATM/DesignTP53ATM.bed");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/genes.tsv");

        long panel_id = addPanel(bedFile, "TP53ATM_IonTorrent");
        long transcripts_id = addTranscripts(transcriptsFile, "TP53ATM_IonTorrent");

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "TP53ATM_IonTorrent",
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

    public static void createDefaultMicropanel() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/DesignMicropanel/DesignMicropanel.bed");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/genes.tsv");
        File hotspotsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Hemato/DesignMicropanel/hotspots.tsv");

        long panel_id = addPanel(bedFile, "Micropanel");
        long transcripts_id = addTranscripts(transcriptsFile, "Micropanel");
        long hotspots_id = addHotspots(hotspotsFile, "Micropanel");
        List<SpecificCoverage> regions = new ArrayList<>();
        regions.add(
                new SpecificCoverage(
                        "JAK2_exon14",
                        "chr9",
                        5073602,
                        5073876,
                        6000
                ));
        long specificCov_id = addSpecificCoverage(regions, "Micropanel");

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "Micropanel",
                Genome.GRCh37,
                100,
                500,
                0.01,
                panel_id,
                transcripts_id,
                specificCov_id,
                hotspots_id,
                TargetEnrichment.AMPLICON
        );

        // create CIQ
        List<CIQHotspot> ciqHotspotsLAM = new ArrayList<>();
        ciqHotspotsLAM.add(new CIQHotspot(
                "CIQHorizonMyelo_JAK2_p.V617F",
                "chr9",
                5073770,
                "G",
                "T",
                0.01f
        ));
        addCIQ(ciqHotspotsLAM, "CIQ_Micropanel_SMP", "CIQ.+SMP");

        List<CIQHotspot> ciqHotspotsSMP = new ArrayList<>();
        ciqHotspotsSMP.add(new CIQHotspot(
                "CIQHorizonMyelo_FLT3_p.D835Y",
                "chr13",
                28592642,
                "C",
                "A",
                0.025f
        ));
        ciqHotspotsSMP.add(new CIQHotspot(
                "CIQHorizonMyelo_IDH2_p.R172K",
                "chr15",
                90631838,
                "C",
                "T",
                0.025f
        ));
        ciqHotspotsSMP.add(new CIQHotspot(
                "CIQHorizonMyelo_IDH1_p.R132C",
                "chr2",
                209113113,
                "G",
                "A",
                0.025f
        ));
        ciqHotspotsSMP.add(new CIQHotspot(
                "CIQHorizonMyelo_NPM1_p.Trp288CysfsTer12",
                "chr5",
                170837543,
                "C",
                "CTCTG",
                0.025f
        ));
        addCIQ(ciqHotspotsSMP, "CIQ_Micropanel_LAM", "CIQ.+LAM");
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
