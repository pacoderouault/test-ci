package ngsdiaglim.utils;

import htsjdk.samtools.util.BlockCompressedOutputStream;
import htsjdk.tribble.TribbleException;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.index.tabix.TabixFormat;
import htsjdk.tribble.index.tabix.TabixIndex;
import htsjdk.variant.vcf.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
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
    public static VCFFileReader getVCFReader(File file) throws IOException, TribbleException {
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

    public static List<String> getSamplesName(File vcfFile) throws IOException, TribbleException {
        VCFFileReader vcfReader = getVCFReader(vcfFile);
        return vcfReader.getFileHeader().getSampleNamesInOrder();
    }


    public static String getVcfHeader(VCFFileReader reader) {
        StringBuilder sb = new StringBuilder();
        for (VCFHeaderLine line : reader.getFileHeader().getMetaDataInInputOrder()) {
            sb.append("##").append(line.toString()).append("\n");
        }
        return sb.toString();
    }


    public static boolean isVCFReadable(File file) {
        try {
            new VCFFileReader(file, false);
            return true;
        } catch (TribbleException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void bgZipFile(File infile, File outFile) throws IOException {
        BlockCompressedOutputStream bcos = new BlockCompressedOutputStream(outFile);
        BufferedReader br = IOUtils.getFileReader(infile);
//        BufferedReader br = new BufferedReader(new FileReader(infile));
        String line;
        while ((line = br.readLine()) != null) {
            line = line + "\n";
            bcos.write(line.getBytes(StandardCharsets.ISO_8859_1));
        }
        bcos.close();
    }


    public static void createTabixIndex(File file, File indexFile) throws IOException {
        final VCFFileReader readerVcfGz = new VCFFileReader(file, false);
        final TabixIndex tabixIndexVcfGz =
                IndexFactory.createTabixIndex(file, new VCFCodec(), TabixFormat.VCF,
                        readerVcfGz.getFileHeader().getSequenceDictionary());
        tabixIndexVcfGz.write(indexFile);
    }

    public static VCFHeaderVersion getVCFHeaderVersion(VCFHeader vcfHeader){
        Iterator<VCFHeaderLine> iter = vcfHeader.getMetaDataInInputOrder().iterator();
        while(iter.hasNext()){
            VCFHeaderLine hl = iter.next();
            if(hl.getKey().equals("fileformat")){
                return VCFHeaderVersion.toHeaderVersion(hl.getValue());
            }
        }
        return null;
    }
}
