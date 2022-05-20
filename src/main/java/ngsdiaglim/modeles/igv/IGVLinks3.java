package ngsdiaglim.modeles.igv;

import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.analyse.Analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class IGVLinks3 {

    private void createSessionFile(Analysis analysis) throws IOException {

        Path sessionFile = Files.createTempFile("igv_session", ".xml");
        try (BufferedWriter writer =
                     Files.newBufferedWriter(sessionFile, StandardCharsets.UTF_8)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            writer.newLine();
            String genomeName = analysis.getAnalysisParameters().getGenome().equals(Genome.GRCh38) ? "hg38" : "hg19";
            if (analysis.getAnalysisParameters().getGenome().equals(Genome.GRCh38)) {
                writer.write("<Session genome=\"" + genomeName + "\" hasGeneTrack=\"false\" hasSequenceTrack=\"true\" version=\"8\">");
                writer.newLine();
            }

            writer.write("<Resources>");
            writer.newLine();

            writer.write("<Resource name=\"TumSol\" path=\"../.data/panels/TumSol.bed.gz\" type=\"bed\"/>");
            writer.newLine();


            writer.write("</Resources>");
            writer.newLine();
        }
    }
}
