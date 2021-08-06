package ngsdiaglim.utils;

import htsjdk.samtools.*;

import java.io.File;
import java.io.IOException;
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


}
