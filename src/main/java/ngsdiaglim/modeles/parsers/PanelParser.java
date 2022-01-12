package ngsdiaglim.modeles.parsers;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import ngsdiaglim.exceptions.MalformedPanelFile;
import ngsdiaglim.modeles.analyse.PanelRegion;
import ngsdiaglim.utils.IOUtils;
import ngsdiaglim.utils.NumberUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PanelParser {

    private final static String column_delimiter = "\t";

    public static List<PanelRegion> parsePanel(File file) throws IOException, MalformedPanelFile {

        List<PanelRegion> regions = new ArrayList<>();

        try (BufferedReader reader = IOUtils.getFileReader(file)) {

            String line;
            int i = 0;
            while((line = reader.readLine()) != null) {
                ++i;
                if (line.startsWith("#")) continue;

                String[] tks = line.split(column_delimiter);
                if (tks.length < 4) {
                    throw new MalformedPanelFile("Bad columns number (< 4)");
                }
                if (!NumberUtils.isInt(tks[1]) || !NumberUtils.isInt(tks[2])) {
                    throw new MalformedPanelFile("Bad numeric columns, line " + i);
                }
                String contig = tks[0];
                int start = Integer.parseInt(tks[1]);
                int end = Integer.parseInt(tks[2]);
                String name = tks[3];

                PanelRegion region = new PanelRegion(contig, start, end, name);
                if (tks.length > 4) {
                    String pool = tks[4];
                    region.setPoolAmplification(pool);
                }
                regions.add(region);
            }

        }

        return regions;
    }



    public static void writePanel(List<PanelRegion> regions, File outFile) throws IOException {
        try (BlockCompressedOutputStream bcos = new BlockCompressedOutputStream(outFile)) {
            String line;
            for (PanelRegion region : regions) {
                line = region.getContig() + column_delimiter
                        + region.getStart() + column_delimiter
                        + region.getEnd() + column_delimiter
                        + region.getName() + "\n";
                bcos.write(line.getBytes(StandardCharsets.ISO_8859_1));
            }
        }
    }


}
