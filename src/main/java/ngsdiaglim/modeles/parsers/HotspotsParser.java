package ngsdiaglim.modeles.parsers;

import ngsdiaglim.database.DAOController;
import ngsdiaglim.enumerations.HotspotType;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.modeles.variants.Hotspot;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotspotsParser {

    private final static String column_delimiter = "\t";
    private final static int column_number = 9;

    public static List<Hotspot> parseHotspotFile(File file) throws IOException, MalformedPanelFile {

        List<Hotspot> hotspots = new ArrayList<>();

        try (BufferedReader reader = IOUtils.getFileReader(file)) {

            String line;
            int i = 0;
            while((line = reader.readLine()) != null) {
                ++i;
                if (line.startsWith("#")) continue;

                String[] tks = line.split(column_delimiter);
                if (tks.length < column_number) {
                    throw new MalformedPanelFile("Bad columns number (< " + column_number + ")");
                }
                if (!NumberUtils.isInt(tks[2]) || !NumberUtils.isInt(tks[3])) {
                    throw new MalformedPanelFile("Bad numeric columns, line " + i);
                }
                int idx = 0;
                String hotspot_id = tks[0];
                String contig = tks[1];
                int start = Integer.parseInt(tks[2]);
                int end = Integer.parseInt(tks[3]);
                String ref = tks[4];
                String alt = tks[5];
                String gene = tks[6];
                String codingMut = tks[7];
                String proteinMut = tks[8];
                HotspotType type;
                if (ref.equals(".")){
                    ref = "";
                    alt = "";
                    type = HotspotType.REGION;
                }
                else {
                    type = HotspotType.POINT_MUTATION;
                }

                Hotspot hotspot = new Hotspot(hotspot_id, contig, start, end, ref, alt, gene, proteinMut, codingMut, type);
                hotspots.add(hotspot);
            }
        }

        return hotspots;
    }
}
