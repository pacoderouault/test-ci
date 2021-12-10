package ngsdiaglim.stats;

import ngsdiaglim.cnv.caller.CNVDetectionRobustZScore;
import ngsdiaglim.enumerations.CNVTypes;
import org.apache.commons.math3.distribution.NormalDistribution;

public class ZTest {

    private static NormalDistribution nd = new NormalDistribution(0, 1);

    public static CNVTypes getCNVType(Double zscore, Double alpha) {

//        Double delThreshold = nd.inverseCumulativeProbability(alpha);
//        Double dupThreshold = nd.inverseCumulativeProbability(1 - alpha);

        if (zscore <= CNVDetectionRobustZScore.delThreshold) {
            return CNVTypes.DELETION;
        }
        else if (zscore >= CNVDetectionRobustZScore.dupThreshold) {
            return CNVTypes.DUPLICATION;
        }
        else {
            return CNVTypes.NORMAL;
        }
    }
//
//    public static Boolean zTest(Double zScore, Double alpha) {
//        Double threshold = nd.inverseCumulativeProbability(1 - alpha);
//        return Math.abs(zScore) >= threshold;
//    }

    public static Boolean zTest(Double zScore) {
//        Double threshold = nd.inverseCumulativeProbability(1 - alpha);
        return Math.abs(zScore) >= CNVDetectionRobustZScore.dupThreshold;
    }

    public static Boolean zTest(Double zScore, Double alpha) {
//        Double threshold = nd.inverseCumulativeProbability(1 - alpha);
        return Math.abs(zScore) >= alpha;
    }


}
