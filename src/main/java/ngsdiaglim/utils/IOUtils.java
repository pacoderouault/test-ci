package ngsdiaglim.utils;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.tabix.TabixFormat;
import htsjdk.tribble.index.tabix.TabixIndex;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class IOUtils {

    public static boolean isGZipped(File f) throws IOException {
        InputStream is = new FileInputStream(f);
        byte[] b = new byte[2];
        int n = is.read(b);
        return n == 2 && (b[0] == (byte) 0x1f) && (b[1] == (byte)0x8b);
    }


    public static InputStream getInputStream(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        if (isGZipped(f)) {
            return new GZIPInputStream(fis);
        }
        else {
            return fis;
        }

    }


    public static BufferedReader getFileReader(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader isr;
        if (isGZipped(f)) {
            GZIPInputStream gis = new GZIPInputStream(fis);
            isr = new InputStreamReader(gis);
        }
        else {
            isr = new InputStreamReader(fis);
        }
        return new BufferedReader(isr);
    }


    private static void bgZipFile(File infile, File outFile) throws IOException {
        BlockCompressedOutputStream bcos = new BlockCompressedOutputStream(outFile);
        BufferedReader br = IOUtils.getFileReader(infile);
        String line;
        while ((line = br.readLine()) != null) {
            line = line + "\n";
            bcos.write(line.getBytes(StandardCharsets.ISO_8859_1));
        }
        bcos.close();
    }


    private static void tabixIndex(File file, File indexFile) throws IOException {
        final VCFFileReader readerVcfGz = new VCFFileReader(file, false);
        final TabixIndex tabixIndexVcfGz =
                IndexFactory.createTabixIndex(file, new VCFCodec(), TabixFormat.VCF,
                        readerVcfGz.getFileHeader().getSequenceDictionary());
        tabixIndexVcfGz.write(indexFile);
    }
}
