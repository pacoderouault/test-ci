package ngsdiaglim.modeles;

import htsjdk.samtools.SAMException;
import htsjdk.samtools.reference.FastaSequenceIndexCreator;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import htsjdk.samtools.reference.ReferenceSequence;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;

import java.io.File;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

public class FastaSequenceGetter {

    private final File fastaFile;
    private IndexedFastaSequenceFile fastaSequence;

    public FastaSequenceGetter(File fastaFile) throws Exception {
        this.fastaFile = fastaFile;
        if (fastaFile.exists()) {
            try {
                FastaSequenceIndexCreator.create(fastaFile.toPath(), false);

            }
            catch (SAMException ignored) {}
            fastaSequence = new IndexedFastaSequenceFile(fastaFile);
        }
    }

    public String getSequence(String contig, int start, int end) throws IOException {
        if (fastaSequence == null) return null;
        String seq = null;
        if (fastaFile != null && fastaFile.exists()) {
            seq = getSequenceFromFastaFile(contig, start, end);
        }
        return seq;
    }

    private String getSequenceFromFastaFile(String contig, int start, int end) throws IOException {
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
}
