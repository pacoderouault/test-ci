package ngsdiaglim.modeles;

import htsjdk.tribble.readers.TabixIteratorLineReader;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeaderVersion;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.enumerations.Genome;
import ngsdiaglim.modeles.analyse.Analysis;
import ngsdiaglim.modeles.parsers.VCFParser;
import ngsdiaglim.modeles.parsers.VepPredictionParser;
import ngsdiaglim.modeles.variants.Annotation;
import ngsdiaglim.modeles.variants.GenomicVariant;
import ngsdiaglim.modeles.variants.Variant;
import ngsdiaglim.utils.VCFUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabixGetter {

    private final Analysis analysis;
    private final TabixReader tabixReader;
    private final VCFCodec vcfDecoder;
    private final VepPredictionParser vepParser;
    private boolean parseVepPredictions = false;
    private final LiftOverMapper liftOverMapper;

    public TabixGetter(Analysis analysis, File vcfFile) throws Exception {

        this.analysis = analysis;
        VCFFileReader vcfReader = new VCFFileReader(vcfFile);
        vepParser = new VepPredictionParser(vcfReader.getHeader());

        VCFHeaderVersion vcfVersion = VCFUtils.getVCFHeaderVersion(vcfReader.getHeader());
        if (vcfVersion == null) vcfVersion = VCFHeaderVersion.VCF4_2;
        vcfDecoder = new VCFCodec();
        vcfDecoder.setVCFHeader(vcfReader.getHeader(), vcfVersion);

        tabixReader = new TabixReader(vcfFile.getPath());

        liftOverMapper = new LiftOverMapper(
                new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.REFERENCE_GRCH37.name())),
                new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.REFERENCE_GRCH38.name())),
                new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.GRCH37_TO_GRCH38_CHAIN.name())),
                new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.GRCH38_TO_GRCH37_CHAIN.name()))
        );
    }

    public List<Annotation> getVariant(Genome genome, Annotation annotation) throws IOException {
        return getVariant(genome, annotation.getGenomicVariant().getContig(),
                annotation.getGenomicVariant().getStart() - 1,
                annotation.getGenomicVariant().getEnd(),
                annotation.getGenomicVariant().getRef(),
                annotation.getGenomicVariant().getAlt());
    }

//    public List<Annotation> getVariant(String contig, int start, int end) throws IOException {
//        List<Annotation> annotations = new ArrayList<>();
//        TabixReader.Iterator iter = tabixReader.query(contig, start, end);
//        TabixIteratorLineReader lineReader = new TabixIteratorLineReader(iter);
//
//        String nextLine;
//        while ((nextLine = lineReader.readLine()) != null) {
//            VariantContext ctx = vcfDecoder.decode(nextLine);
//            Variant variant = new Variant(ctx.getContig(), ctx.getStart(), ctx.getEnd(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
//            Annotation a = VCFParser.variantContextToAnnotation(variant, ctx);
//            if (parseVepPredictions && analysis != null) {
//                VCFParser.parseTranscriptsConsequences(a, ctx, vepParser, analysis.getAnalysisParameters());
//            }
//            annotations.add(a);
//        }
//        return annotations;
//    }

    public List<Annotation> getVariant(Genome genome, String contig, int start, int end, String ref, String alt) throws IOException {
        List<Annotation> annotations = new ArrayList<>();
        TabixReader.Iterator iter = tabixReader.query(contig, start, end);
        TabixIteratorLineReader lineReader = new TabixIteratorLineReader(iter);

        String nextLine;
        while ((nextLine = lineReader.readLine()) != null) {
            VariantContext ctx = vcfDecoder.decode(nextLine);
            if (ctx.getReference().getBaseString().equals(ref) && ctx.getAlternateAllele(0).getBaseString().equals(alt)) {

                GenomicVariant grch37Variant;
                GenomicVariant grch38Variant;

                GenomicVariant genomicVariant = new GenomicVariant(ctx.getContig(), ctx.getStart(), ctx.getEnd(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
                if (genome.equals(Genome.GRCh38)) {
                    grch38Variant = genomicVariant;
                    grch37Variant = liftOverMapper.grch38ToGrch37(ctx.getContig(), ctx.getStart(), ctx.getEnd(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
                } else {
                    grch37Variant = genomicVariant;
                    grch38Variant = liftOverMapper.grch37ToGrch38(ctx.getContig(), ctx.getStart(), ctx.getEnd(), ctx.getReference().getBaseString(), ctx.getAlternateAllele(0).getBaseString());
                }

                Variant variant = new Variant(grch37Variant, grch38Variant);
                Annotation a = VCFParser.variantContextToAnnotation(genome, variant, ctx);
                if (parseVepPredictions && analysis != null) {
                    VCFParser.parseTranscriptsConsequences(a, ctx, vepParser, analysis.getAnalysisParameters());
                }
                annotations.add(a);
            }
        }
        return annotations;
    }

    public boolean isParseVepPredictions() {return parseVepPredictions;}

    public void parseVepPredictions(boolean parseVepPredictions) {
        this.parseVepPredictions = parseVepPredictions;
    }
}
