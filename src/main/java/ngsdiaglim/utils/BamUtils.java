package ngsdiaglim.utils;

import htsjdk.samtools.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class BamUtils {

    public static String getBamSampleName(File bamFile) {
        final SamReader samReader = SamReaderFactory.makeDefault()
                .enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS)
                .validationStringency(ValidationStringency.DEFAULT_STRINGENCY)
                .open(bamFile);
        SAMFileHeader header = samReader.getFileHeader();
        if (header != null) {
            List<SAMReadGroupRecord> readGroups = header.getReadGroups();
            if (readGroups.size() > 0) {
                return readGroups.get(0).getSample();
            }
        }
        return null;
    }


    public static boolean isBamFile(File file) {
        final SamReader samReader = SamReaderFactory.makeDefault()
                .enable(SamReaderFactory.Option.INCLUDE_SOURCE_IN_RECORDS)
                .validationStringency(ValidationStringency.DEFAULT_STRINGENCY)
                .open(file);
        try {
            samReader.iterator().next();
            return true;
        } catch (SAMFormatException e) {
            return false;
        }
    }


    /**
     * Build a bam index (.bai) from a bam file
     * @return The File of the index created
     */
    public static File buildBamIndex(File bamFile, SamReader reader) throws Exception {
        File indexFile = new File(bamFile.getAbsoluteFile() + ".bai");
        Files.createFile(indexFile.toPath());
        try {
            BAMIndexer.createIndex(reader, indexFile);
        } catch (Exception e) {
            Files.deleteIfExists(indexFile.toPath());
            throw new InterruptedException("impossible to index the bam file");
        }
        return indexFile;
    }
}
