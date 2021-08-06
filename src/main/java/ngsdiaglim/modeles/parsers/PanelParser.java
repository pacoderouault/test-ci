package ngsdiaglim.modeles.parsers;

import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.Panel;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PanelParser {

    public static List<PanelRegion> parsePanel(File file) throws IOException, MalformedPanelFile {

        List<PanelRegion> regions = new ArrayList<>();

        try (BufferedReader reader = IOUtils.getFileReader(file)) {

            String line;
            int i = 0;
            while((line = reader.readLine()) != null) {
                ++i;
                if (line.startsWith("#")) continue;

                String[] tks = line.split("\t");
                if (tks.length < 4) {
                    throw new MalformedPanelFile("Bad columns number (!= 4)");
                }
                if (!NumberUtils.isInt(tks[1]) || !NumberUtils.isInt(tks[2])) {
                    throw new MalformedPanelFile("Bad numeric columns, line " + i);
                }
                String contig = tks[0];
                int start = Integer.parseInt(tks[1]);
                int end = Integer.parseInt(tks[2]);
                String name = tks[3];

                PanelRegion region = new PanelRegion(contig, start, end, name);
                regions.add(region);
            }

        }

        return regions;
    }

}
