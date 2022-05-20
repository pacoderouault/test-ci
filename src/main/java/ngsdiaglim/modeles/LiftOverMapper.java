package ngsdiaglim.modeles;

import htsjdk.samtools.liftover.LiftOver;
import htsjdk.samtools.util.Interval;
import ngsdiaglim.modeles.variants.GenomicVariant;

import java.io.File;
import java.io.IOException;

public class LiftOverMapper {

    private final LiftOver grch37toGrch38LiftOver;
    private final LiftOver grch38toGrch37LiftOver;
    private final FastaSequenceGetter grch37ReferenceGetter;
    private final FastaSequenceGetter grch38ReferenceGetter;

    public LiftOverMapper(File grch37Reference, File grch38Reference, File grch37To38Chain, File grch38To37Chain) throws Exception {
        grch37toGrch38LiftOver = new LiftOver(grch37To38Chain);
        grch38toGrch37LiftOver = new LiftOver(grch38To37Chain);
        grch37ReferenceGetter = new FastaSequenceGetter(grch37Reference);
        grch38ReferenceGetter = new FastaSequenceGetter(grch38Reference);
    }

    public GenomicVariant grch37ToGrch38(String contig, int start, int end, String ref, String alt) throws IOException {
        Interval interval = grch37toGrch38LiftOver.liftOver(new Interval(contig, start, end));
        if (interval == null) {
            return new GenomicVariant(null, null, null, null, null);
        }
        String newRef = grch38ReferenceGetter.getSequence(interval.getContig(), interval.getStart(), interval.getEnd());
        if (newRef != null) {
            newRef = newRef.toUpperCase();
        }
        return new GenomicVariant(interval.getContig(), interval.getStart(), interval.getEnd(), newRef, alt);
    }

    public GenomicVariant grch38ToGrch37(String contig, int start, int end, String ref, String alt) throws IOException {
        Interval interval = grch38toGrch37LiftOver.liftOver(new Interval(contig, start, end));
        if (interval == null) {
            return new GenomicVariant(null, null, null, null, null);
        }
        String newRef = grch37ReferenceGetter.getSequence(interval.getContig(), interval.getStart(), interval.getEnd());
        if (newRef != null) {
            newRef = newRef.toUpperCase();
        }
        return new GenomicVariant(interval.getContig(), interval.getStart(), interval.getEnd(), newRef, alt);
    }
}
