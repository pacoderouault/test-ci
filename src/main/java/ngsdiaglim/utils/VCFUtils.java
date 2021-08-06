package ngsdiaglim.utils;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.tribble.TribbleException;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.tabix.TabixFormat;
import htsjdk.tribble.index.tabix.TabixIndex;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class VCFUtils {

    private static final String TABIX_INDEX_EXTENSION = ".tbi";
    private static final Charset LATIN1 = StandardCharsets.ISO_8859_1;

    private VCFUtils() {}

    /**
     *
     * @param file
     * @return the appropriate VCFReader for the vcf file for .vcf or .gz.vcf.
     * @throws IOException
     */
    public static VCFFileReader getVCFReader(File file) throws IOException {
        VCFFileReader reader;

        if (VCFFileReader.isBCF(file)) {
            // Check if index of the vcf.gz exists. Create it if none.
            File index = new File(file.getPath() + TABIX_INDEX_EXTENSION);
            if (!index.exists()) {
                indexCompressedVCF(file);
            }
            reader = new VCFFileReader(file, true);
        }
        else {
            reader = new VCFFileReader(file, false);
        }
        return reader;
    }

    /**
     * Return true if the file is block compressed
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean isGZipped(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        boolean isGZippied = GZIPInputStream.GZIP_MAGIC == (raf.read() & 0xff | ((raf.read() << 8) & 0xff00));
        raf.close();
        return isGZippied;
    }

    /**
     * Create a tabix index from a block compressed VCF file
     * @param file
     * @throws IOException
     */
    public static void indexCompressedVCF(File file) throws IOException {
        TabixIndex tabixIndexGz = IndexFactory.createTabixIndex(file, new VCFCodec(), TabixFormat.VCF, null);
        tabixIndexGz.writeBasedOnFeatureFile(file);
    }

    /**
     * Block compress a VCF file
     * @param file
     * @return
     * @throws IOException
     */
    public static File compressBGZP(File file) throws IOException {
        File outfile = new File(file.getPath() + ".gz");
        BlockCompressedOutputStream bcos = new BlockCompressedOutputStream(outfile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            line = line + "\n";
            bcos.write(line.getBytes(LATIN1));
        }
        bcos.close();
        br.close();
        return outfile;
    }

    public static List<String> getSamplesName(File vcfFile) throws IOException {
        VCFFileReader vcfReader = getVCFReader(vcfFile);
        return vcfReader.getFileHeader().getSampleNamesInOrder();
    }


    public static boolean isVCFReadable(File file) {
        try {
            new VCFFileReader(file, false);
            return true;
        } catch (TribbleException e) {
            return false;
        }
    }

}
