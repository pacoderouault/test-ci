package ngsdiaglim.modeles.parsers;

import ngsdiaglim.modeles.biofeatures.Region;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CytobandParser {

    public static Map<String, List<Region>> getCytoBand(InputStream cytobandFile) throws IOException {
        Map<String, List<Region>> cytoBands = new HashMap<>();
        InputStreamReader isr = new InputStreamReader(cytobandFile,
                StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String st;
        final String delim = "\t";
        while ((st = br.readLine()) != null) {
            if (!st.trim().isEmpty() && !st.startsWith("#")) {
                String[] tokens = st.trim().split(delim);
                cytoBands.putIfAbsent(tokens[0], new ArrayList<>());
                cytoBands.get(tokens[0]).add(new Region(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3]));
            }
        }
        return cytoBands;
    }
}
