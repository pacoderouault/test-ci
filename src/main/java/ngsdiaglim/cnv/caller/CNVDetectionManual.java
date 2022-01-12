package ngsdiaglim.cnv.caller;

import ngsdiaglim.cnv.CNV;
import ngsdiaglim.cnv.CNVSample;
import ngsdiaglim.cnv.CovCopCNVData;
import ngsdiaglim.cnv.CovCopRegion;
import ngsdiaglim.enumerations.CNVTypes;
import org.apache.commons.collections4.iterators.ReverseListIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CNVDetectionManual extends CNVDetection {

    private final double delThreshold;
    private final double dupThreshold;
    private final int minConsecutiveAmplicons;

    private CNVDetectionManual(Builder builder) {
        super.cnvData = builder.cnvData;
        delThreshold = builder.delThreshold;
        dupThreshold = builder.dupThreshold;
        minConsecutiveAmplicons = builder.minConsecutiveAmplicons;
        amplicons = super.cnvData.getAllCovcopRegionsAsList();
    }

    public void callCNVs() {
        clearOldCVNs();
        getCNVs();
    }

    protected void getCNVs() {
        int sampleIndex = 0;
        for (String sampleName : cnvData.getSamples().keySet()) {

            CNVSample sample = cnvData.getSamples().get(sampleName);

            String contig = "";
            List<CovCopRegion> dupAmplicons = new ArrayList<>();
            List<CovCopRegion> delAmplicons = new ArrayList<>();

            for (int ampIndex = 0; ampIndex < amplicons.size(); ampIndex++) {

                CovCopRegion a = amplicons.get(ampIndex);

                if (!a.getContig().equals(contig)) {
                    setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, ampIndex);
                    setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, ampIndex);
                    delAmplicons.clear();
                    dupAmplicons.clear();
                }


                Double value = a.getNormalized_values().get(sampleIndex);
                if (value == null) {
                    dupAmplicons.add(a);
                    delAmplicons.add(a);
                }
                else {
                    if (value <= delThreshold) {
                        delAmplicons.add(a);
                        setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, ampIndex);
                        dupAmplicons.clear();
                    }
                    else if (value >= dupThreshold) {
                        dupAmplicons.add(a);
                        setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, ampIndex);
                        delAmplicons.clear();
                    }
                    else {
                        setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, ampIndex);
                        setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, ampIndex);
                        delAmplicons.clear();
                        dupAmplicons.clear();
                    }
                }

                contig = a.getContig();
            }
            setCNV(sample, sampleIndex, dupAmplicons, CNVTypes.DUPLICATION, amplicons.size());
            setCNV(sample, sampleIndex, delAmplicons, CNVTypes.DELETION, amplicons.size());

            cleanShortCNVs(sample);
            sampleIndex++;
        }
    }

    private void setCNV(CNVSample sample, int sampleIndex, List<CovCopRegion> amplicons, CNVTypes cnvType, int lastAmpIndex) {
        if (!amplicons.isEmpty()) {
            // trim null values from each ends
            Iterator<CovCopRegion> iter = amplicons.iterator();
            while (iter.hasNext() && iter.next().getNormalized_values().get(sampleIndex) == null) {
                iter.remove();
            }
            ReverseListIterator<CovCopRegion> reverseIter =  new ReverseListIterator<>(amplicons);
            while (reverseIter.hasNext() && reverseIter.next().getNormalized_values().get(sampleIndex) == null) {
                reverseIter.remove();
            }
            if (!amplicons.isEmpty()) {
                CNV cnv = new CNV(
                        cnvType,
                        amplicons.get(0).getContig(),
                        amplicons.get(0).getStart(),
                        amplicons.get(amplicons.size() - 1).getEnd(),
                        amplicons,
                        lastAmpIndex
                );
                sample.addCNV(cnv);
            }
        }
    }

    private void cleanShortCNVs(CNVSample sample) {
        sample.getCNV().removeIf(cnv -> cnv.getAmpliconsList().size() < minConsecutiveAmplicons);
    }

    public static final class Builder {
        private final CovCopCNVData cnvData;
        private double delThreshold = 0.7;
        private double dupThreshold = 1.3;
        private int minConsecutiveAmplicons = 3;

        public Builder(CovCopCNVData cnvData) {
            this.cnvData = cnvData;
        }

        public Builder delThreshold(double val) {
            delThreshold = val;
            return this;
        }

        public Builder dupThreshold(double val) {
            dupThreshold = val;
            return this;
        }

        public Builder minConsecutiveAmplicons(int val) {
            minConsecutiveAmplicons = val;
            return this;
        }

        public CNVDetectionManual build() {
            return new CNVDetectionManual(this);
        }
    }
}
