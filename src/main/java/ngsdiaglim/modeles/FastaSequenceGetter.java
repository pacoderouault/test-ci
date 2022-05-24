package ngsdiaglim.modeles;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

public class FastaSequenceGetter {

    private static Logger logger = LogManager.getLogger(FastaSequenceGetter.class);
    private final File fastaFile;
    private IndexedFastaSequenceFile fastaSequence;

    private ReferenceSequenceFile referenceSequenceFile;

    public FastaSequenceGetter(File fastaFile) throws Exception {
        this.fastaFile = fastaFile;
        if (fastaFile.exists()) {
            try {
                FastaSequenceIndexCreator.create(fastaFile.toPath(), false);
            }
            catch (SAMException ignored) {}

            this.referenceSequenceFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(this.fastaFile);
//            fastaSequence = new IndexedFastaSequenceFile(fastaFile);
        } else {
            logger.error("Fasta file : " + fastaFile.getPath() + " doesn't exists.");
        }
    }

    public File getFastaFile() {return fastaFile;}

    public String getSequence1(String contig, int start, int end) throws IOException {
        if (fastaSequence == null) return null;
        String seq = null;
        if (fastaFile != null && fastaFile.exists()) {
            seq = getSequenceFromFastaFile(contig, start, end);
        }
        return seq;
    }

    public String getSequence(String contig, int start, int end) throws IOException {
        if (referenceSequenceFile == null) return null;
        String seq = null;
        if (fastaFile != null && fastaFile.exists()) {
            seq = getSequenceFromFastaFile(contig, start, end);
        }
        return seq;
    }

    private String getSequenceFromFastaFile1(String contig, int start, int end) throws IOException {
        String seq = null;
        ReferenceSequence refSq;
        try {
            refSq = fastaSequence.getSubsequenceAt(contig, start, end);
            seq = new String(refSq.getBases(),
                    StandardCharsets.UTF_8);
        } catch (SAMException cce) {
            if (cce.getCause() instanceof ClosedChannelException) {
                fastaSequence.close();
                fastaSequence = new IndexedFastaSequenceFile(fastaFile);
                getSequenceFromFastaFile(contig, start, end);
            }
            else {
                throw cce;
            }
        }
        return seq;
    }

    private String getSequenceFromFastaFile(String contig, int start, int end) throws IOException {
        String seq = null;
        ReferenceSequence refSq;
        try {
            refSq = referenceSequenceFile.getSubsequenceAt(contig, start, end);
            seq = new String(refSq.getBases(),
                    StandardCharsets.UTF_8);
        } catch (SAMException cce) {
            if (cce.getCause() instanceof ClosedChannelException) {
                referenceSequenceFile.close();
                this.referenceSequenceFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(this.fastaFile);
                getSequenceFromFastaFile(contig, start, end);
            }
            else {
                throw cce;
            }
        }
        return seq;
    }
}
