package ngsdiaglim.utils;

import htsjdk.variant.vcf.VCFFileReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

class VCFUtilsTest {

    private static final File resourcesDirectory = new File("src/test/resources");
    @TempDir
    File tempDir;

    @Test
    void isGZipped() {
        File gzipFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf.gz").toFile();
        File noGzipFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf").toFile();
        try {
            assertTrue(VCFUtils.isGZipped(gzipFile));
            assertFalse(VCFUtils.isGZipped(noGzipFile));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void indexCompressedVCF() {
        File gzipFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf.gz").toFile();
        try {
            VCFUtils.indexCompressedVCF(gzipFile);
            VCFFileReader reader = VCFUtils.getVCFReader(gzipFile);
            assertDoesNotThrow(() -> reader.query("chr1", 116946469, 116946470));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getSamplesName() {

        File vcfFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf").toFile();
        File gzipFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf.gz").toFile();
        try {
            List<String> samples = VCFUtils.getSamplesName(vcfFile);
            assertTrue(samples.size() == 1 && samples.get(0).equals("M21.05_15B68a"));

            List<String> samples2 = VCFUtils.getSamplesName(gzipFile);
            assertTrue(samples2.size() == 1 && samples2.get(0).equals("M21.05_15B68a"));
        } catch (IOException e) {
            fail(e);
        }
    }


    @Test
    void isVCFReadable() {
        File vcfFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf").toFile();
        File gzipFile = Paths.get(resourcesDirectory.getPath(), "data/M21.05_15B68a.vcf.gz").toFile();
        File noVCF = Paths.get(resourcesDirectory.getPath(), "data/CMT_panel.bed").toFile();
        assertTrue(VCFUtils.isVCFReadable(gzipFile));
        assertTrue(VCFUtils.isVCFReadable(vcfFile));
        assertFalse(VCFUtils.isVCFReadable(noVCF));
    }

    @Test
    void bgZipFile() {
        File vcfFile = Paths.get(resourcesDirectory.getPath(), "data/test_bgzip.vcf").toFile();
        File output = new File(tempDir, "test_bgzip.vcf.gz");
        try {
            VCFUtils.bgZipFile(vcfFile, output);
            assertTrue(VCFUtils.isGZipped(output));

            StringBuilder orgFile = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(vcfFile)))) {
                String content;
                while ((content = in.readLine()) != null) {
                    orgFile.append(content);
                }
            }

            StringBuilder outFile = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(output))))) {
                String content;
                while ((content = in.readLine()) != null) {
                    outFile.append(content);
                }
            }

            assertEquals(orgFile.toString(), outFile.toString());

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void createTabixIndex() {
        File gzipFile = Paths.get(resourcesDirectory.getPath(), "data/test_index.vcf.gz").toFile();
        File indexFile = Paths.get(resourcesDirectory.getPath(), "data/test_index.vcf.gz.tbi").toFile();
        try {
            VCFUtils.createTabixIndex(gzipFile, indexFile);
            VCFFileReader reader = VCFUtils.getVCFReader(gzipFile);
            assertDoesNotThrow(() -> reader.query("chr1", 116946469, 116946470));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getVCFHeaderVersion() {
        File gzipFile = Paths.get(resourcesDirectory.getPath(), "data/test_index.vcf.gz").toFile();
        try {
            assertEquals("VCFv4.2", VCFUtils.getVCFHeaderVersion(VCFUtils.getVCFReader(gzipFile).getHeader()).getVersionString());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void getVcfHeader() {
        File vcfFile = Paths.get(resourcesDirectory.getPath(), "data/test_header.vcf").toFile();
        try {
            String header = "##fileformat=VCFv4.2\n" +
                    "##FILTER=<ID=PASS,Description=\"All filters passed\">\n" +
                    "##FILTER=<ID=LowQual,Description=\"Low quality\">\n" +
                    "##FORMAT=<ID=AD,Number=R,Type=Integer,Description=\"Allelic depths for the ref and alt alleles in the order listed\">\n" +
                    "##FORMAT=<ID=AF,Number=A,Type=Float,Description=\"Allele fractions of alternate alleles in the tumor\">\n" +
                    "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Approximate read depth (reads with MQ=255 or with bad mates are filtered)\">\n" +
                    "##FORMAT=<ID=F1R2,Number=R,Type=Integer,Description=\"Count of reads in F1R2 pair orientation supporting each allele\">\n" +
                    "##FORMAT=<ID=F2R1,Number=R,Type=Integer,Description=\"Count of reads in F2R1 pair orientation supporting each allele\">\n" +
                    "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">\n" +
                    "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n" +
                    "##FORMAT=<ID=PL,Number=G,Type=Integer,Description=\"Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification\">\n" +
                    "##FORMAT=<ID=UNIQ_ALT_READ_COUNT,Number=1,Type=Integer,Description=\"Number of ALT reads with unique start and mate end positions at a variant site\">\n" +
                    "##INFO=<ID=AC,Number=A,Type=Integer,Description=\"Allele count in genotypes, for each ALT allele, in the same order as listed\">\n" +
                    "##INFO=<ID=AF,Number=A,Type=Float,Description=\"Allele Frequency, for each ALT allele, in the same order as listed\">\n" +
                    "##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total number of alleles in called genotypes\">";
            assertEquals(header, VCFUtils.getVcfHeader(VCFUtils.getVCFReader(vcfFile)).trim());
        } catch (IOException e) {
            fail(e);
        }
    }
}