package ngsdiaglim.modeles;

import ngsdiaglim.modeles.biofeatures.Region;
import ngsdiaglim.modeles.parsers.CytobandParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Cytobands {

    private static Map<String, List<Region>> cytoBands;
    private static final String hg19CytobandPath = "/data/cytoband_UCSC_GRCh37.bed";

    private static Map<String, List<Region>> getCytobands() throws IOException {
        if (cytoBands == null) {
            InputStream hg19CytobandFile = Cytobands.class.getResourceAsStream(hg19CytobandPath);
            cytoBands = CytobandParser.getCytoBand(hg19CytobandFile);
        }
        return cytoBands;
    }


    public static Region getCytoBand(Region region) throws IOException {
        if (getCytobands().containsKey(region.getContig())) {
            List<Region> bands = getCytobands().get(region.getContig());

            int l = 0;
            int h = bands.size() - 1;
            int m = h / 2;

            while (l <= h) {
                int comp = bands.get(m).compareTo(region);
                if (comp == 0) {
                    return bands.get(m);
                }
                else {
                    if (comp < 0) {
                        h = m - 1;
                    }
                    else {
                        l = m + 1;
                    }
                }
                m = (l + h) / 2;
            }
        }
        return null;
    }
}
