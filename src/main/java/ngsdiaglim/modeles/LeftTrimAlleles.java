package ngsdiaglim.modeles;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import ngsdiaglim.App;
import ngsdiaglim.AppSettings;
import ngsdiaglim.modeles.variants.Variant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class LeftTrimAlleles {

    private final static Logger logger = LogManager.getLogger(LeftTrimAlleles.class);

    private FastaSequenceGetter fastaSequenceGetter;

    public LeftTrimAlleles() {
        try {
            fastaSequenceGetter = new FastaSequenceGetter(new File(App.get().getAppSettings().getProperty(AppSettings.DefaultAppSettings.REFERENCE_HG19.name())));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public Variant leftTrimVariant(Variant variant) throws IOException {
        Variant v = new Variant(variant);
        boolean changeInAlleles = true;
        while (changeInAlleles && !v.getRef().equalsIgnoreCase(v.getAlt())) {
            changeInAlleles = false;
            if (v.getRef().charAt(v.getRef().length() - 1) == v.getAlt().charAt(v.getAlt().length() - 1)) {
                v.setRef(v.getRef().substring(0, v.getRef().length() - 1));
                v.setAlt(v.getAlt().substring(0, v.getAlt().length() - 1));
                v.setEnd(v.getEnd() - 1);
                changeInAlleles = true;
            }
            if (v.getRef().isEmpty() || v.getAlt().isEmpty()) {
                String nucl = fastaSequenceGetter.getSequence(v.getContig(), v.getStart() -1, v.getStart() -1);
                v.setRef(nucl + v.getRef());
                v.setAlt(nucl + v.getAlt());
                v.setStart(v.getStart() - 1);
                changeInAlleles = true;
            }
        }

        while (v.getRef().length() >= 2 && v.getAlt().length() >= 2 && v.getRef().charAt(0) == v.getAlt().charAt(0)) {
            v.setRef(v.getRef().substring(1));
            v.setAlt(v.getAlt().substring(1));
            v.setStart(v.getStart() + 1);
        }
        return v;
    }

    public VariantContext leftTrimVariant(VariantContext v) throws IOException {
        boolean changeInAlleles = true;
        String ref = v.getReference().getBaseString();
        String alt = v.getAlternateAllele(0).getBaseString();
        int start = v.getStart();
        while (changeInAlleles && !ref.equalsIgnoreCase(alt)) {
            changeInAlleles = false;
            if (ref.charAt(ref.length() - 1) == alt.charAt(alt.length() - 1)) {
                ref = ref.substring(0, ref.length() - 1);
                alt = alt.substring(0, alt.length() - 1);
                changeInAlleles = true;
            }
            if (ref.isEmpty() || alt.isEmpty()) {
                String nucl = fastaSequenceGetter.getSequence(v.getContig(), start -1, start -1);
                ref = nucl + ref;
                alt = nucl + alt;
                start = start - 1;
                changeInAlleles = true;
            }
        }

        while (ref.length() >= 2 && alt.length() >= 2 && ref.charAt(0) == alt.charAt(0)) {
            ref = ref.substring(1);
            alt = alt.substring(1);
            start = start + 1;
        }

        return new VariantContextBuilder(v)
                .alleles(ref, alt)
                .start(start)
                .stop(start + ref.length() - 1)
                .make();
    }

    public VariantContext leftTrimVariant(String contig, int start, String ref, String alt) throws IOException {
        boolean changeInAlleles = true;
        while (changeInAlleles && !ref.equalsIgnoreCase(alt)) {
            changeInAlleles = false;
            if (ref.charAt(ref.length() - 1) == alt.charAt(alt.length() - 1)) {
                ref = ref.substring(0, ref.length() - 1);
                alt = alt.substring(0, alt.length() - 1);
                changeInAlleles = true;
            }
            if (ref.isEmpty() || alt.isEmpty()) {
                String nucl = fastaSequenceGetter.getSequence(contig, start - 1, start - 1);
                ref = nucl + ref;
                alt = nucl + alt;
                start -= 1;
                changeInAlleles = true;
            }
        }

        while (ref.length() >= 2 && alt.length() >= 2 && ref.charAt(0) == alt.charAt(0)) {
            ref = ref.substring(1);
            alt = alt.substring(1);
            start += 1;
        }
        return new VariantContextBuilder()
                .chr(contig)
                .start(start)
                .stop(start + ref.length() - 1)
                .alleles(ref, alt)
                .make();
    }

}
