package ngsdiaglim.importer.bgm;

import ngsdiaglim.App;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.MalformedGeneTranscriptFile;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.modeles.parsers.GeneSetParser;
import ngsdiaglim.modeles.parsers.PanelParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class CreateDefaultParamsBGM {

    public static void createDefaultMiseq() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/DesignMiseq/CCMT_A_v1_target_for_approval_version2_6col.bed");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/DesignMiseq/liste_transcripts_avec_alternatifs.tsv");

        long panel_id = addPanel(bedFile, "NeuroCalcium_capture");
        long transcripts_id = addTranscripts(transcriptsFile, "NeuroCalcium_capture");

        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "NeuroCalcium_capture",
                Genome.GRCh37,
                30,
                50,
                0.1,
                panel_id,
                transcripts_id,
                null,
                null,
                TargetEnrichment.CAPTURE
        );
    }

    public static void createDefaultProton() throws MalformedPanelFile, SQLException, IOException, MalformedGeneTranscriptFile {
        File bedFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Designproton/design.bed");
        File transcriptsFile = new File("/mnt/Data/dev/IdeaProjects/NGSDiagLim/dev_data/Import/Designproton/transcript_liste1.tsv");

        long panel_id = addPanel(bedFile, "CMT126_amplicon");
        long transcripts_id = addTranscripts(transcriptsFile, "CMT126_amplicon");
        DAOController.getAnalysisParametersDAO().addAnalysisParameters(
                "NeuroCalcium_amplicon",
                Genome.GRCh37,
                30,
                50,
                0.1,
                panel_id,
                transcripts_id,
                null,
                null,
                TargetEnrichment.AMPLICON
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
}
