package ngsdiaglim.modeles.parsers;

import ngsdiaglim.exceptions.MalformedGeneTranscriptFile;
import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.modeles.biofeatures.Transcript;
import ngsdiaglim.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class GeneSetParser {

    private static final String splitterChar = ";";

    public static HashSet<Gene> parseGeneSet(File file) throws IOException, MalformedGeneTranscriptFile {

        HashSet<Gene> genes = new HashSet<>();

        try (BufferedReader reader = IOUtils.getFileReader(file)) {

            String line;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;

                String[] tks = line.split("\t");
                if (tks.length < 2) {
                    throw new MalformedGeneTranscriptFile("Bad columns number (!= 2)");
                }

                String geneName = tks[0];
                String transcripts = tks[1];

                HashMap<String, Transcript> transcriptsMap = new HashMap<>();
                for (String transcript : transcripts.split(splitterChar)) {
                    transcriptsMap.putIfAbsent(Transcript.getNameWithoutVersion(transcript.toUpperCase()), new Transcript(transcript));
                }
                Gene gene = new Gene(geneName);
                gene.setTranscripts(transcriptsMap);
                genes.add(gene);
            }

        }

        return genes;
    }
}
