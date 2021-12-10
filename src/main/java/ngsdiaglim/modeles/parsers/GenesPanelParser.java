package ngsdiaglim.modeles.parsers;

import ngsdiaglim.modeles.biofeatures.Gene;
import ngsdiaglim.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GenesPanelParser {

    public static Set<Gene> parseGenes(File file) throws IOException {
        Set<Gene> genes = new HashSet<>();
        try (BufferedReader reader = IOUtils.getFileReader(file)) {
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                ++i;
                if (line.startsWith("#")) continue;
                String geneName = line.trim().toUpperCase();
                genes.add(new Gene(geneName));
            }
        }
        return genes;
    }
}
