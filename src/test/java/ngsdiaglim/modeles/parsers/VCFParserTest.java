package ngsdiaglim.modeles.parsers;

import ngsdiaglim.BaseSetup;
import ngsdiaglim.database.DAOController;
import ngsdiaglim.database.dao.*;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.enumerations.TargetEnrichment;
import ngsdiaglim.exceptions.MalformedGeneTranscriptFile;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.exceptions.NotBiallelicVariant;
import ngsdiaglim.modeles.analyse.AnalysisParameters;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.analyse.Run;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.GeneSet;
import ngsdiaglim.modeles.biofeatures.Transcript;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VCFParserTest extends BaseSetup {

    private static AnalysisParameters params;
    private static final File resourcesDirectory = new File("src/test/resources");
    private static final PanelDAO panelDAO = new PanelDAO();
    private static final PanelRegionDAO panelRegionDAO = new PanelRegionDAO();
    private static final GeneSetDAO geneSetDAO = new GeneSetDAO();
    private static final GeneDAO geneDAO = new GeneDAO();
    private static final TranscriptsDAO transcriptsDAO = new TranscriptsDAO();
    @BeforeAll
    static void setupParams() {

        File panelFile = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel.bed").toFile();
        File geneSetFile = Paths.get(resourcesDirectory.getPath(), "data/liste_transcripts_avec_alternatifs.tsv").toFile();
        try {
            List<PanelRegion> regions = PanelParser.parsePanel(panelFile);
            long panelId = panelDAO.addPanel("panel", panelFile.getPath());
            for (PanelRegion region : regions) {
                panelRegionDAO.addRegion(region, panelId);
            }

            Panel panel = panelDAO.getPanel(panelId);

            HashSet<Gene> genes = GeneSetParser.parseGeneSet(geneSetFile);
            long geneSetId = geneSetDAO.addGeneSet("GeneSet");
            for (Gene gene : genes) {
                long geneId = geneDAO.addGene(gene, geneSetId);
                gene.setId(geneId);
                for (Transcript transcript : gene.getTranscripts().values()) {
                    long transcriptId = transcriptsDAO.addTranscript(transcript.getName(), gene.getId());
                    transcript.setId(transcriptId);
                }
                // if only one transcript for the gene, set it as "preferred transcript"
                if (gene.getTranscripts().size() == 1) {
                    Optional<Transcript> opt = gene.getTranscripts().values().stream().findAny();
                    if(opt.isPresent()) {
                        geneDAO.setPreferredTranscript(gene.getId(), opt.get().getId());
                    }
                }
            }

            GeneSet geneSet = geneSetDAO.getGeneSet(geneSetId);


            params = new AnalysisParameters(
                    1, Genome.GRCh37, "", 30, 50, 0.1f, true, panel, geneSet, null, TargetEnrichment.CAPTURE
                    );
        } catch (IOException | MalformedPanelFile | SQLException | MalformedGeneTranscriptFile e) {
            fail();
        }

    }

    @Test
    void parseVCF() {

        File vcfFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf").toFile();
//        long id, String path, String name, LocalDate date, LocalDate creationDate, String creationUser
        Run run = new Run(1, "path", "run", LocalDate.now(), LocalDate.now(), "user");

        VCFParser vcfParser = new VCFParser(vcfFile, params, run);
        try {
            vcfParser.parseVCF(true);
        } catch (IOException | NotBiallelicVariant | SQLException e) {
            fail(e.getMessage());
        }
    }
}